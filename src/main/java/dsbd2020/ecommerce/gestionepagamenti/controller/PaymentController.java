package dsbd2020.ecommerce.gestionepagamenti.controller;

import dsbd2020.ecommerce.gestionepagamenti.entity.Logging;
import dsbd2020.ecommerce.gestionepagamenti.entity.Orders;
import dsbd2020.ecommerce.gestionepagamenti.exception.CustomException;
import dsbd2020.ecommerce.gestionepagamenti.service.LoggingService;
import dsbd2020.ecommerce.gestionepagamenti.service.OrderService;
import dsbd2020.ecommerce.gestionepagamenti.exception.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import com.google.common.base.Splitter;


import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;

@Controller
@RequestMapping(path = "/")
public class PaymentController {

    @Value("${MY_PAYPAL_ACCOUNT}")
    private String MY_PAYPAL_ACCOUNT;

    private final Logger LOG = LoggerFactory.getLogger(PaymentController.class);

    private KafkaTemplate<String, Map> dataKafkaTemplate;

    private OrderService orderService;

    private LoggingService loggingService;

    private DatabaseHealthContributor dbc;

    @Autowired
    PaymentController(KafkaTemplate<String, Map> dataKafkaTemplate, OrderService orderService, LoggingService loggingService, DatabaseHealthContributor dbc) {
        this.dataKafkaTemplate = dataKafkaTemplate;
        this.orderService = orderService;
        this.loggingService = loggingService;
        this.dbc = dbc;
    }

    void sendCustomMessage(Map<String, Object> data, String key, String topicName) {
        LOG.info("Sending Json Serializer : {}", data);
        LOG.info("--------------------------------");
        dataKafkaTemplate.send(topicName, key, data);
    }

    private String IPN_verify(String ipn) {
//        String url = "https://httpstat.us/500";
        String url = "https://ipnpb.sandbox.paypal.com/cgi-bin/webscr";
        ipn += "&cmd=_notify-validate";

        byte[] postData = ipn.getBytes(StandardCharsets.UTF_8);

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded");
        HttpEntity<byte[]> r = new HttpEntity<>(postData, headers);
        ResponseEntity<String> responseMessage = restTemplate.exchange(url, HttpMethod.POST, r, String.class);

//        System.out.println(responseMessage.getBody());

        return responseMessage.getBody();
    }

    @PostMapping(path = "/ipn")
    public @ResponseBody
    ResponseEntity ipn(@RequestBody String request) throws UnsupportedEncodingException{
        Map<String, Object> value_msg = new HashMap<>();
        String key = "";

        String notify = IPN_verify(request);

        Map<String, String> newMap = new HashMap<>();
        for(Map.Entry<String, String> entry : Splitter.on('&').trimResults().withKeyValueSeparator('=').split(request).entrySet()) {
//            System.out.println("Key: " + entry.getKey() + "; Value: " + entry.getValue());
            newMap.put(entry.getKey(), URLDecoder.decode(entry.getValue(), StandardCharsets.UTF_8.toString()));
        }

        if(!notify.equals("")) {
            if (!notify.equals("INVALID")) {
                if (newMap.get("business").equals(MY_PAYPAL_ACCOUNT)) {
                    LOG.info("---------------------------------");
                    value_msg.put("orderId", newMap.get("invoice"));
                    value_msg.put("userId", newMap.get("item_number"));
                    value_msg.put("amountPaid", Double.valueOf(newMap.get("mc_gross")));
                    value_msg.put("extraArgs", new HashMap<String, String>());
                    sendCustomMessage(value_msg, "order_paid","orders");

                    Orders order = new Orders();
                    order.setKafkaOrderId(newMap.get("invoice"));
                    order.setKafkaUserId(newMap.get("item_number"));
                    order.setKafkaAmountPaid(Double.valueOf(newMap.get("mc_gross")));
                    order.setUnixTimestamp(Instant.now().getEpochSecond());
                    order.setIpnAttribute(newMap);

                    LOG.info("Orders data stored : {}", orderService.addOrders(order));
                    LOG.info("--------------------------------");
                    return ResponseEntity.ok("200 ok");

                } else {
                    LOG.info("---------------------------------");
                    value_msg.put("timestamp", Instant.now().getEpochSecond());
                    value_msg.putAll(newMap);
                    key = "received_wrong_business_paypal_payment";
                    sendCustomMessage(value_msg, "received_wrong_business_paypal_payment","logging");
                }
            } else {
                LOG.info("---------------------------------");
                value_msg.put("timestamp", Instant.now().getEpochSecond());
                value_msg.putAll(newMap);
                key = "bad_ipn_error";
                sendCustomMessage(value_msg, "bad_ipn_error","logging");
            }

            Logging logging = new Logging();
            logging.setKafkaKey(key);
            logging.setUnixTimestamp((Long) value_msg.get("timestamp"));
            logging.setIpnAttribute(newMap);

            LOG.info("Logging data stored : {}", loggingService.addLogging(logging));
            LOG.info("--------------------------------");
        }
        return ResponseEntity.ok("200 ok");
    }

    @GetMapping(path = "/transactions")
    public @ResponseBody Object getOrders(@RequestHeader(value = "X-User-ID") Integer ID,
                                            @RequestParam(value = "fromTimestamp") Long fromTimestamp,
                                            @RequestParam(value = "endTimestamp") Long endTimestamp,
                                            @RequestParam(value = "filter", defaultValue = "-1") Integer filter) {
        if (ID == 0) {
            switch (filter) {
                case -1: //Parametro non trovato, ritorno tutto
                    HashMap<String, Iterable<Object>> response = new HashMap<>();
                    response.put("Order valid", Collections.singleton(orderService.getOrdersBetweenTimestamp(fromTimestamp, endTimestamp)));
                    response.put("Order not valid", Collections.singleton(loggingService.getLoggingBetweenTimestamp(fromTimestamp, endTimestamp)));
                    return response;
                case 0: //ritorna ordini validi
                    return orderService.getOrdersBetweenTimestamp(fromTimestamp, endTimestamp);
                case 1: //ritorna ordini non validi
                    return loggingService.getLoggingBetweenTimestamp(fromTimestamp,endTimestamp);
                default:
                    throw new CustomException("Invalid filter value, delete it or enter 0 or 1");
            }
        }
        throw new UnauthorizedException("User doesn't an administrator.");
    }

    @PostMapping(path = "/fakeorders")
    public @ResponseBody ResponseEntity fakeOrders(@RequestBody String request) throws UnsupportedEncodingException {
        Map<String, String> mapData = new HashMap<>();
        for(Map.Entry<String, String> entry : Splitter.on('&').trimResults().withKeyValueSeparator('=').split(request).entrySet()) {
            mapData.put(entry.getKey(), URLDecoder.decode(entry.getValue(), StandardCharsets.UTF_8.toString()));
        }
        Map<String, Object> value_msg = new HashMap<>();
        value_msg.put("orderId", String.valueOf(mapData.get("invoice")));
        value_msg.put("userId", mapData.get("item_number"));
        value_msg.put("amountPaid", Double.valueOf(mapData.get("mc_gross")));
        value_msg.put("extraArgs", new HashMap<String, String>());
        sendCustomMessage(value_msg, "order_paid","orders");

        LOG.info("---------------------------------");
        LOG.info("Sent: {}", value_msg);
        LOG.info("--------------------------------");
        return ResponseEntity.ok("200 ok");
    }

    @GetMapping(path = "/ping")
    public @ResponseBody Map<String, String> pingAck() {
        String status = dbc.health().getStatus().toString();
//        System.out.println(status);
        Map<String, String> ack = new HashMap<>();
        ack.put("serviceStatus", "up");
        ack.put("dbStatus", status.toLowerCase());
        return ack;
    }

    @GetMapping(path = "/health/ping")
    public @ResponseBody String pingHealthAck() {
        return "200 ok";
    }
}
