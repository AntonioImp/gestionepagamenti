package dsbd2020.ecommerce.gestionepagamenti.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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
    void ipn(@RequestBody Map<String, Object> data) {
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
            } else {
                LOG.info("---------------------------------");
                kafka_msg.put("key", "received_wrong_business_paypal_payment");
                value_msg.put("timestamp", Long.toString(Instant.now().getEpochSecond()));
                value_msg.putAll(data);
                kafka_msg.put("value", value_msg);
                sendCustomMessage(kafka_msg, "logging");
            }
        } else {
            LOG.info("---------------------------------");
            kafka_msg.put("key", "bad_ipn_error");
            value_msg.put("timestamp", Long.toString(Instant.now().getEpochSecond()));
            value_msg.putAll(data);
            kafka_msg.put("value", value_msg);
            sendCustomMessage(kafka_msg, "logging");
        }
    }
}
