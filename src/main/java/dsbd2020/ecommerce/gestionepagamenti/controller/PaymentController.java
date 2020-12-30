package dsbd2020.ecommerce.gestionepagamenti.controller;

import dsbd2020.ecommerce.gestionepagamenti.entity.Logging;
import dsbd2020.ecommerce.gestionepagamenti.entity.Orders;
import dsbd2020.ecommerce.gestionepagamenti.service.LoggingService;
import dsbd2020.ecommerce.gestionepagamenti.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(path = "/")
public class PaymentController {

    @Value("${MY_PAYPAL_ACCOUNT}")
    private String MY_PAYPAL_ACCOUNT;

    private final Logger LOG = LoggerFactory.getLogger(PaymentController.class);

    private KafkaTemplate<String, Map> dataKafkaTemplate;

    @Autowired
    private OrderService orderService;

    @Autowired
    private LoggingService loggingService;

    @Autowired
    PaymentController(KafkaTemplate<String, Map> dataKafkaTemplate) {
        this.dataKafkaTemplate = dataKafkaTemplate;
    }

    void sendCustomMessage(Map<String, Object> data, String topicName) {
        LOG.info("Sending Json Serializer : {}", data);
        LOG.info("--------------------------------");

        dataKafkaTemplate.send(topicName, data);
    }

    private String IPN_verify(Map<String, Object> data) {
        String response = "";
//        String url = "https://httpstat.us/500";
        String ipAddress = "";
        String url = "https://ipnpb.sandbox.paypal.com/cgi-bin/webscr";
        try {
            ipAddress = Inet4Address.getLocalHost().getHostAddress();
            StringBuilder urlParam = new StringBuilder();
            for (Map.Entry<String, Object> param : data.entrySet()) {
                if (urlParam.length() != 0) urlParam.append('&');
                urlParam.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                urlParam.append('=');
                urlParam.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
            }
            urlParam.append("&cmd=_notify-validate");
            byte[] postData = urlParam.toString().getBytes(StandardCharsets.UTF_8);

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "application/x-www-form-urlencoded");
            HttpEntity<byte[]> r = new HttpEntity<>(postData, headers);
            ResponseEntity<String> responseMessage = restTemplate.exchange(url, HttpMethod.POST, r, String.class);

            response = responseMessage.getBody();

        } catch (HttpServerErrorException e) {
            Map<String, Object> kafka_msg = new HashMap<>();
            Map<String, Object> value_msg = new HashMap<>();

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);

            LOG.info("---------------------------------");
            kafka_msg.put("key", "http_errors");
            value_msg.put("timestamp", Instant.now().getEpochSecond());
            value_msg.put("sourceIp", ipAddress);
            value_msg.put("service", "payment management");
            value_msg.put("request", url + "|POST");
            value_msg.put("error", sw);
            kafka_msg.put("value", value_msg);
            sendCustomMessage(kafka_msg, "logging");
        } catch (HttpClientErrorException e) {
            Map<String, Object> kafka_msg = new HashMap<>();
            Map<String, Object> value_msg = new HashMap<>();
            StackTraceElement[] stackTrace = e.getStackTrace();
            System.out.println(e.getRawStatusCode());

            LOG.info("---------------------------------");
            kafka_msg.put("key", "http_errors");
            value_msg.put("timestamp", Instant.now().getEpochSecond());
            value_msg.put("sourceIp", ipAddress);
            value_msg.put("service", "payment management");
            value_msg.put("request", url + "|POST");
            value_msg.put("error", e.getRawStatusCode());
            kafka_msg.put("value", value_msg);
            sendCustomMessage(kafka_msg, "logging");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    @PostMapping(path = "/ipn")
    public @ResponseBody
    Object ipn(@RequestBody Map<String, Object> data) {
        System.out.println(data);
        Map<String, Object> kafka_msg = new HashMap<>();
        Map<String, Object> value_msg = new HashMap<>();

        String notify = IPN_verify(data);

        if(!notify.equals("")) {
            if (!notify.equals("INVALID")) {
                if (data.get("business").equals(MY_PAYPAL_ACCOUNT)) {
                    LOG.info("---------------------------------");
                    kafka_msg.put("key", "order_paid");
                    value_msg.put("orderId", data.get("invoice"));
                    value_msg.put("userId", data.get("item_id"));
                    value_msg.put("amountPaid", data.get("mc_gross"));
                    kafka_msg.put("value", value_msg);
                    sendCustomMessage(kafka_msg, "orders");

                    Orders order = new Orders();
                    order.setKafkaOrderId((Integer) data.get("invoice"));
                    order.setKafkaUserId((Integer) data.get("item_id"));
                    order.setKafkaAmountPaid(Double.valueOf(data.get("mc_gross").toString()));
                    order.setUnixTimestamp(Instant.now().getEpochSecond());
                    order.setIpnAttribute(data);
                    return orderService.addOrders(order);
                } else {
                    LOG.info("---------------------------------");
                    kafka_msg.put("key", "received_wrong_business_paypal_payment");
                    value_msg.put("timestamp", Instant.now().getEpochSecond());
                    value_msg.putAll(data);
                    kafka_msg.put("value", value_msg);
                    sendCustomMessage(kafka_msg, "logging");
                }
            } else {
                LOG.info("---------------------------------");
                kafka_msg.put("key", "bad_ipn_error");
                value_msg.put("timestamp", Instant.now().getEpochSecond());
                value_msg.putAll(data);
                kafka_msg.put("value", value_msg);
                sendCustomMessage(kafka_msg, "logging");
            }

            Logging logging = new Logging();
            logging.setKafkaKey((String) kafka_msg.get("key"));
            logging.setUnixTimestamp((Long) value_msg.get("timestamp"));
            logging.setIpnAttribute(data);
            return loggingService.addLogging(logging);
        }
        return null;
    }

    @GetMapping(path = "/transactions")
    public @ResponseBody Iterable<Orders> getOrders(@RequestHeader(value = "X-User-ID") Integer ID ,@RequestParam Long fromTimestamp, @RequestParam Long endTimestamp) {
        if (ID == 0) {
            return orderService.getOrdersBetweenTimestamp(fromTimestamp, endTimestamp);
        }
        return null;
    }

    @GetMapping(path = "/ping")
    public @ResponseBody Map<String, String> pingAck() {
        Map<String, String> ack = new HashMap<>();
        ack.put("serviceStatus", "up");
        ack.put("dbStatus", "up");
        return ack;
    }
}
