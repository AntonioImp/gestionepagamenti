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

//    @Autowired
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

    private String IPN_verify(Map<String, Object> data) throws UnsupportedEncodingException{
//        String url = "https://httpstat.us/500";
        String url = "https://ipnpb.sandbox.paypal.com/cgi-bin/webscr";
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
        return responseMessage.getBody();
    }

    @PostMapping(path = "/real_ipn", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody int real_ipn(IPN ipn) {
        System.out.println(ipn.toString());
        return 200;
//        IPN persistedPerson = personService.save(pojo);
//        ResponseEntity<IPN> tmp = ResponseEntity
//                .created(URI
//                        .create(String.format("/persons/%s", person.getFirstName())))
//                .body(persistedPerson);
//        PhoneNumber fromPhone = new PhoneNumber(request.getFrom());
//        String commandText = request.getBody();

//        UserProfile userProfile = userProfileRepository.findByPhoneNumber(fromPhone)
//                .orElseThrow(() -> new NoSuchElementException(
//                        "Could not find authorized User Profile for " + fromPhone + ", command text was: " + commandText));
//
//        return executeCommand(commandText, userProfile);
    }

    @PostMapping(path = "/ipn")
    public @ResponseBody
    ResponseEntity ipn(@RequestBody Map<String, Object> data) throws UnsupportedEncodingException{
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

                    LOG.info("Orders data stored : {}", orderService.addOrders(order));
                    LOG.info("--------------------------------");
                    return ResponseEntity.ok("200 ok");

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

    @GetMapping(path = "/ping")
    public @ResponseBody Map<String, String> pingAck() {
        Map<String, String> ack = new HashMap<>();
        ack.put("serviceStatus", "up");
        ack.put("dbStatus", "up");
        return ack;
    }
}
