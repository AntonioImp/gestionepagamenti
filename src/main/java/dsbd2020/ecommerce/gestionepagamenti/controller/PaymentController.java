package dsbd2020.ecommerce.gestionepagamenti.controller;

import dsbd2020.ecommerce.gestionepagamenti.entity.Logging;
import dsbd2020.ecommerce.gestionepagamenti.entity.Orders;
import dsbd2020.ecommerce.gestionepagamenti.service.LoggingService;
import dsbd2020.ecommerce.gestionepagamenti.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping(path = "/ipn")
    public @ResponseBody
    Object ipn(@RequestBody Map<String, Object> data) {
        System.out.println(data);
        Map<String, Object> kafka_msg = new HashMap<>();
        Map<String, Object> value_msg = new HashMap<>();
        if (true) {
            if (data.get("business").equals(MY_PAYPAL_ACCOUNT)) {
                LOG.info("---------------------------------");
                kafka_msg.put("key", "order_paid");
                value_msg.put("orderId", data.get("invoice"));
                value_msg.put("userId", data.get("item_id"));
                value_msg.put("amountPaid", data.get("mc_gross"));
                kafka_msg.put("value", value_msg);
                sendCustomMessage(kafka_msg, "orders");

                Orders order = new Orders();
                order.setKafkaOrderId((Integer)data.get("invoice"));
                order.setKafkaUserId((Integer)data.get("item_id"));
                order.setKafkaAmountPaid((Double)data.get("mc_gross"));
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
        logging.setKafkaKey((String)kafka_msg.get("key"));
        logging.setUnixTimestamp((Long)value_msg.get("timestamp"));
        logging.setIpnAttribute(data);
        return loggingService.addLogging(logging);
    }

    @GetMapping(path = "/transactions")
    public @ResponseBody Iterable<Orders> getOrders(@RequestHeader(value = "X-User-ID") Integer ID ,@RequestParam Long fromTimestamp, @RequestParam Long endTimestamp) {
        if (ID == 0) {
            return orderService.getOrdersBetweenTimestamp(fromTimestamp, endTimestamp);
        }
        return null;
    }
}
