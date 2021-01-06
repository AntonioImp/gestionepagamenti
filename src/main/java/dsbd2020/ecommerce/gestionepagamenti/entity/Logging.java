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
    private String IpnBusiness;
    private Integer IpnInvoice;
    private Integer IpnQuantity;
    private String IpnItem_number;
    private String IpnItem_name;
    private Double IpnMc_gross;
    private String IpnMc_currency;
    private String IpnPayer_id;

    public void setIpnAttribute(Map<String, String> ipn) {
        this.IpnBusiness = ipn.get("business");
        this.IpnInvoice = Integer.valueOf(ipn.get("invoice"));
        this.IpnQuantity = Integer.valueOf(ipn.get("quantity"));
        this.IpnItem_number = ipn.get("item_number");
        this.IpnItem_name = ipn.get("item_name");
        this.IpnMc_currency = ipn.get("mc_currency");
        this.IpnMc_gross = Double.valueOf(ipn.get("mc_gross"));
        this.IpnPayer_id = ipn.get("payer_id");
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

    public void setUnixTimestamp(Long unixTimestamp) {
        this.unixTimestamp = unixTimestamp;
    }

    public key getKafkaKey() {
        return kafkaKey;
    }

    public void setKafkaKey(String kafkaKey) {
        this.kafkaKey = key.valueOf(kafkaKey);
    }

    public String getIpnBusiness() {
        return IpnBusiness;
    }

    public void setIpnBusiness(String ipnBusiness) {
        IpnBusiness = ipnBusiness;
    }

    public Integer getIpnInvoice() {
        return IpnInvoice;
    }

    public void setIpnInvoice(Integer ipnInvoice) {
        IpnInvoice = ipnInvoice;
    }

    public Integer getIpnQuantity() {
        return IpnQuantity;
    }

    public void setIpnQuantity(Integer ipnQuantity) {
        IpnQuantity = ipnQuantity;
    }

    public String getIpnItem_number() {
        return IpnItem_number;
    }

    public void setIpnItem_number(String ipnItem_number) {
        IpnItem_number = ipnItem_number;
    }

    public String getIpnItem_name() {
        return IpnItem_name;
    }

    public void setIpnItem_name(String ipnItem_name) {
        IpnItem_name = ipnItem_name;
    }

    public Double getIpnMc_gross() {
        return IpnMc_gross;
    }

    public void setIpnMc_gross(Double ipnMc_gross) {
        IpnMc_gross = ipnMc_gross;
    }

    public String getIpnMc_currency() {
        return IpnMc_currency;
    }

    public void setIpnMc_currency(String ipnMc_currency) {
        IpnMc_currency = ipnMc_currency;
    }

    public String getIpnPayer_id() {
        return IpnPayer_id;
    }

    public void setIpnPayer_id(String ipnPayer_id) {
        IpnPayer_id = ipnPayer_id;
    }

    @Override
    public String toString() {
        return "Logging{" +
                "id=" + id +
                ", unixTimestamp=" + unixTimestamp +
                ", kafkaKey=" + kafkaKey +
                ", IpnBusiness='" + IpnBusiness + '\'' +
                ", IpnInvoice=" + IpnInvoice +
                ", IpnQuantity=" + IpnQuantity +
                ", IpnItem_number='" + IpnItem_number + '\'' +
                ", IpnItem_name='" + IpnItem_name + '\'' +
                ", IpnMc_gross=" + IpnMc_gross +
                ", IpnMc_currency='" + IpnMc_currency + '\'' +
                ", IpnPayer_id='" + IpnPayer_id + '\'' +
                '}';
    }
}
