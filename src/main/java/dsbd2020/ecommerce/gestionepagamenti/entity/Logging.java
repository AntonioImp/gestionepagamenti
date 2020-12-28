package dsbd2020.ecommerce.gestionepagamenti.entity;

import javax.persistence.*;
import java.util.Map;

@Entity
public class Logging {

    private enum key {
        bad_ipn_error,
        received_wrong_business_paypal_payment
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private Long unixTimestamp;

    @Enumerated(EnumType.STRING)
    private key kafkaKey;

    //Ipn params
    private Integer IpnInvoice;
    private Integer IpnItem_id;
    private Double IpnMc_gross;
    private String IpnBusiness;

    public void setUnixTimestamp(Long unixTimestamp) {
        this.unixTimestamp = unixTimestamp;
    }

    public void setKafkaKey(String kafkaKey) {
        this.kafkaKey = key.valueOf(kafkaKey);
    }

    public void setIpnAttribute(Map<String, Object> data) {
        IpnInvoice = (Integer)data.get("invoice");
        IpnItem_id = (Integer)data.get("item_id");
        IpnMc_gross = (Double)data.get("mc_gross");
        IpnBusiness = (String)data.get("business");
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getUnixTimestamp() {
        return unixTimestamp;
    }

    public key getKafkaKey() {
        return kafkaKey;
    }

    public void setKafkaKey(key kafkaKey) {
        this.kafkaKey = kafkaKey;
    }

    public Integer getIpnInvoice() {
        return IpnInvoice;
    }

    public void setIpnInvoice(Integer ipnInvoice) {
        IpnInvoice = ipnInvoice;
    }

    public Integer getIpnItem_id() {
        return IpnItem_id;
    }

    public void setIpnItem_id(Integer ipnItem_id) {
        IpnItem_id = ipnItem_id;
    }

    public Double getIpnMc_gross() {
        return IpnMc_gross;
    }

    public void setIpnMc_gross(Double ipnMc_gross) {
        IpnMc_gross = ipnMc_gross;
    }

    public String getIpnBusiness() {
        return IpnBusiness;
    }

    public void setIpnBusiness(String ipnBusiness) {
        IpnBusiness = ipnBusiness;
    }

    @Override
    public String toString() {
        return "Logging{" +
                "id=" + id +
                ", unixTimestamp=" + unixTimestamp +
                ", kafkaKey=" + kafkaKey +
                ", IpnInvoice=" + IpnInvoice +
                ", IpnItem_id=" + IpnItem_id +
                ", IpnMc_gross=" + IpnMc_gross +
                ", IpnBusiness='" + IpnBusiness + '\'' +
                '}';
    }
}
