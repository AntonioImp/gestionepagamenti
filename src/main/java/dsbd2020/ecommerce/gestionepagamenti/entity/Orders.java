package dsbd2020.ecommerce.gestionepagamenti.entity;

import javax.persistence.*;
import java.util.Map;

@Entity
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private Long unixTimestamp;

    //Kafka params
    private Integer KafkaOrderId;
    private Integer KafkaUserId;
    private Double KafkaAmountPaid;

    //Ipn params
    private Integer IpnInvoice;
    private Integer IpnItem_id;
    private Double IpnMc_gross;
    private String IpnBusiness;

    public void setUnixTimestamp(Long unixTimestamp) {
        this.unixTimestamp = unixTimestamp;
    }

    public void setKafkaOrderId(Integer kafkaOrderId) {
        KafkaOrderId = kafkaOrderId;
    }

    public void setKafkaUserId(Integer kafkaUserId) {
        KafkaUserId = kafkaUserId;
    }

    public void setKafkaAmountPaid(Double kafkaAmountPaid) {
        KafkaAmountPaid = kafkaAmountPaid;
    }

    public void setIpnAttribute(Map<String, Object> data) {
        IpnInvoice = (Integer)data.get("invoice");
        IpnItem_id = (Integer)data.get("item_id");
        IpnMc_gross = Double.valueOf(data.get("mc_gross").toString());
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

    public Integer getKafkaOrderId() {
        return KafkaOrderId;
    }

    public Integer getKafkaUserId() {
        return KafkaUserId;
    }

    public Double getKafkaAmountPaid() {
        return KafkaAmountPaid;
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

    public void setIpnMc_gross(Object ipnMc_gross) {
        IpnMc_gross = Double.valueOf(ipnMc_gross.toString());
    }

    public String getIpnBusiness() {
        return IpnBusiness;
    }

    public void setIpnBusiness(String ipnBusiness) {
        IpnBusiness = ipnBusiness;
    }

    @Override
    public String toString() {
        return "Orders{" +
                "id=" + id +
                ", unixTimestamp=" + unixTimestamp +
                ", KafkaOrderId=" + KafkaOrderId +
                ", KafkaUserId=" + KafkaUserId +
                ", KafkaAmountPaid=" + KafkaAmountPaid +
                ", IpnInvoice=" + IpnInvoice +
                ", IpnItem_id=" + IpnItem_id +
                ", IpnMc_gross=" + IpnMc_gross +
                ", IpnBusiness='" + IpnBusiness + '\'' +
                '}';
    }
}
