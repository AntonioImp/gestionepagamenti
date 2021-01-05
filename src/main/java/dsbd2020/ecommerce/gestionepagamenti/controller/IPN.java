package dsbd2020.ecommerce.gestionepagamenti.controller;

import java.io.IOException;
import java.net.URLEncoder;

public class IPN {
    private String payment_type;
    private String payment_date;
    private String payment_status;
    private String address_status;
    private String payer_status;
    private String first_name;
    private String last_name;
    private String payer_email;
    private String payer_id;
    private String address_name;
    private String address_country;
    private String address_country_code;
    private String address_zip;
    private String address_state;
    private String address_city;
    private String address_street;
    private String business;
    private String receiver_email;
    private String receiver_id;
    private String residence_country;
    private String item_name1;
    private String item_number1;
    private String tax;
    private String mc_currency;
    private String mc_fee;
    private String mc_gross;
    private String mc_gross_1;
    private String mc_handling;
    private String mc_handling1;
    private String mc_shipping;
    private String mc_shipping1;
    private String txn_type;
    private String txn_id;
    private String notify_version;
    private String custom;
    private String invoice;
    private String test_ipn;
    private String verify_sign;

    public String getPayment_type() {
        return payment_type;
    }

    public void setPayment_type(String payment_type) {
        this.payment_type = payment_type;
    }

    public String getPayment_date() {
        return payment_date;
    }

    public void setPayment_date(String payment_date) {
        this.payment_date = payment_date;
    }

    public String getPayment_status() {
        return payment_status;
    }

    public void setPayment_status(String payment_status) {
        this.payment_status = payment_status;
    }

    public String getAddress_status() {
        return address_status;
    }

    public void setAddress_status(String address_status) {
        this.address_status = address_status;
    }

    public String getPayer_status() {
        return payer_status;
    }

    public void setPayer_status(String payer_status) {
        this.payer_status = payer_status;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getPayer_email() {
        return payer_email;
    }

    public void setPayer_email(String payer_email) {
        this.payer_email = payer_email;
    }

    public String getPayer_id() {
        return payer_id;
    }

    public void setPayer_id(String payer_id) {
        this.payer_id = payer_id;
    }

    public String getAddress_name() {
        return address_name;
    }

    public void setAddress_name(String address_name) {
        this.address_name = address_name;
    }

    public String getAddress_country() {
        return address_country;
    }

    public void setAddress_country(String address_country) {
        this.address_country = address_country;
    }

    public String getAddress_country_code() {
        return address_country_code;
    }

    public void setAddress_country_code(String address_country_code) {
        this.address_country_code = address_country_code;
    }

    public String getAddress_zip() {
        return address_zip;
    }

    public void setAddress_zip(String address_zip) {
        this.address_zip = address_zip;
    }

    public String getAddress_state() {
        return address_state;
    }

    public void setAddress_state(String address_state) {
        this.address_state = address_state;
    }

    public String getAddress_city() {
        return address_city;
    }

    public void setAddress_city(String address_city) {
        this.address_city = address_city;
    }

    public String getAddress_street() {
        return address_street;
    }

    public void setAddress_street(String address_street) {
        this.address_street = address_street;
    }

    public String getBusiness() {
        return business;
    }

    public void setBusiness(String business) {
        this.business = business;
    }

    public String getReceiver_email() {
        return receiver_email;
    }

    public void setReceiver_email(String receiver_email) {
        this.receiver_email = receiver_email;
    }

    public String getReceiver_id() {
        return receiver_id;
    }

    public void setReceiver_id(String receiver_id) {
        this.receiver_id = receiver_id;
    }

    public String getResidence_country() {
        return residence_country;
    }

    public void setResidence_country(String residence_country) {
        this.residence_country = residence_country;
    }

    public String getItem_name1() {
        return item_name1;
    }

    public void setItem_name1(String item_name1) {
        this.item_name1 = item_name1;
    }

    public String getItem_number1() {
        return item_number1;
    }

    public void setItem_number1(String item_number1) {
        this.item_number1 = item_number1;
    }

    public String getTax() {
        return tax;
    }

    public void setTax(String tax) {
        this.tax = tax;
    }

    public String getMc_currency() {
        return mc_currency;
    }

    public void setMc_currency(String mc_currency) {
        this.mc_currency = mc_currency;
    }

    public String getMc_fee() {
        return mc_fee;
    }

    public void setMc_fee(String mc_fee) {
        this.mc_fee = mc_fee;
    }

    public String getMc_gross() {
        return mc_gross;
    }

    public void setMc_gross(String mc_gross) {
        this.mc_gross = mc_gross;
    }

    public String getMc_gross_1() {
        return mc_gross_1;
    }

    public void setMc_gross_1(String mc_gross_1) {
        this.mc_gross_1 = mc_gross_1;
    }

    public String getMc_handling() {
        return mc_handling;
    }

    public void setMc_handling(String mc_handling) {
        this.mc_handling = mc_handling;
    }

    public String getMc_handling1() {
        return mc_handling1;
    }

    public void setMc_handling1(String mc_handling1) {
        this.mc_handling1 = mc_handling1;
    }

    public String getMc_shipping() {
        return mc_shipping;
    }

    public void setMc_shipping(String mc_shipping) {
        this.mc_shipping = mc_shipping;
    }

    public String getMc_shipping1() {
        return mc_shipping1;
    }

    public void setMc_shipping1(String mc_shipping1) {
        this.mc_shipping1 = mc_shipping1;
    }

    public String getTxn_type() {
        return txn_type;
    }

    public void setTxn_type(String txn_type) {
        this.txn_type = txn_type;
    }

    public String getTxn_id() {
        return txn_id;
    }

    public void setTxn_id(String txn_id) {
        this.txn_id = txn_id;
    }

    public String getNotify_version() {
        return notify_version;
    }

    public void setNotify_version(String notify_version) {
        this.notify_version = notify_version;
    }

    public String getCustom() {
        return custom;
    }

    public void setCustom(String custom) {
        this.custom = custom;
    }

    public String getInvoice() {
        return invoice;
    }

    public void setInvoice(String invoice) {
        this.invoice = invoice;
    }

    public String getTest_ipn() {
        return test_ipn;
    }

    public void setTest_ipn(String test_ipn) {
        this.test_ipn = test_ipn;
    }

    public String getVerify_sign() {
        return verify_sign;
    }

    public void setVerify_sign(String verify_sign) {
        this.verify_sign = verify_sign;
    }

    @Override
    public String toString() {
        return "IPN{" +
                "payment_type='" + payment_type + '\'' +
                ", payment_date='" + payment_date + '\'' +
                ", payment_status='" + payment_status + '\'' +
                ", address_status='" + address_status + '\'' +
                ", payer_status='" + payer_status + '\'' +
                ", first_name='" + first_name + '\'' +
                ", last_name='" + last_name + '\'' +
                ", payer_email='" + payer_email + '\'' +
                ", payer_id='" + payer_id + '\'' +
                ", address_name='" + address_name + '\'' +
                ", address_country='" + address_country + '\'' +
                ", address_country_code='" + address_country_code + '\'' +
                ", address_zip='" + address_zip + '\'' +
                ", address_state='" + address_state + '\'' +
                ", address_city='" + address_city + '\'' +
                ", address_street='" + address_street + '\'' +
                ", business='" + business + '\'' +
                ", receiver_email='" + receiver_email + '\'' +
                ", receiver_id='" + receiver_id + '\'' +
                ", residence_country='" + residence_country + '\'' +
                ", item_name1='" + item_name1 + '\'' +
                ", item_number1='" + item_number1 + '\'' +
                ", tax='" + tax + '\'' +
                ", mc_currency='" + mc_currency + '\'' +
                ", mc_fee='" + mc_fee + '\'' +
                ", mc_gross='" + mc_gross + '\'' +
                ", mc_gross_1='" + mc_gross_1 + '\'' +
                ", mc_handling='" + mc_handling + '\'' +
                ", mc_handling1='" + mc_handling1 + '\'' +
                ", mc_shipping='" + mc_shipping + '\'' +
                ", mc_shipping1='" + mc_shipping1 + '\'' +
                ", txn_type='" + txn_type + '\'' +
                ", txn_id='" + txn_id + '\'' +
                ", notify_version='" + notify_version + '\'' +
                ", custom='" + custom + '\'' +
                ", invoice='" + invoice + '\'' +
                ", test_ipn='" + test_ipn + '\'' +
                ", verify_sign='" + verify_sign + '\'' +
                '}';
    }

    public String IPN_verify() throws IOException {
        return "payment_type='" + URLEncoder.encode(String.valueOf(payment_type), "UTF-8") + '\'' +
                "&payment_date='" + URLEncoder.encode(String.valueOf(payment_date), "UTF-8") + '\'' +
                "&payment_status='" + URLEncoder.encode(String.valueOf(payment_status), "UTF-8") + '\'' +
                "&address_status='" + URLEncoder.encode(String.valueOf(address_status), "UTF-8") + '\'' +
                "&payer_status='" + URLEncoder.encode(String.valueOf(payer_status), "UTF-8") + '\'' +
                "&first_name='" + URLEncoder.encode(String.valueOf(first_name), "UTF-8") + '\'' +
                "&last_name='" + URLEncoder.encode(String.valueOf(last_name), "UTF-8") + '\'' +
                "&payer_email='" + URLEncoder.encode(String.valueOf(payer_email), "UTF-8") + '\'' +
                "&payer_id='" + URLEncoder.encode(String.valueOf(payer_id), "UTF-8") + '\'' +
                "&address_name='" + URLEncoder.encode(String.valueOf(address_name), "UTF-8") + '\'' +
                "&address_country='" + URLEncoder.encode(String.valueOf(address_country), "UTF-8") + '\'' +
                "&address_country_code='" + URLEncoder.encode(String.valueOf(address_country_code), "UTF-8") + '\'' +
                "&address_zip='" + URLEncoder.encode(String.valueOf(address_zip), "UTF-8") + '\'' +
                "&address_state='" + URLEncoder.encode(String.valueOf(address_state), "UTF-8") + '\'' +
                "&address_city='" + URLEncoder.encode(String.valueOf(address_city), "UTF-8") + '\'' +
                "&address_street='" + URLEncoder.encode(String.valueOf(address_street), "UTF-8") + '\'' +
                "&business='" + URLEncoder.encode(String.valueOf(business), "UTF-8") + '\'' +
                "&receiver_email='" + URLEncoder.encode(String.valueOf(receiver_email), "UTF-8") + '\'' +
                "&receiver_id='" + URLEncoder.encode(String.valueOf(receiver_id), "UTF-8") + '\'' +
                "&residence_country='" + URLEncoder.encode(String.valueOf(residence_country), "UTF-8") + '\'' +
                "&item_name1='" + URLEncoder.encode(String.valueOf(item_name1), "UTF-8") + '\'' +
                "&item_number1='" + URLEncoder.encode(String.valueOf(item_number1), "UTF-8") + '\'' +
                "&tax='" + URLEncoder.encode(String.valueOf(tax), "UTF-8") + '\'' +
                "&mc_currency='" + URLEncoder.encode(String.valueOf(mc_currency), "UTF-8") + '\'' +
                "&mc_fee='" + URLEncoder.encode(String.valueOf(mc_fee), "UTF-8") + '\'' +
                "&mc_gross='" + URLEncoder.encode(String.valueOf(mc_gross), "UTF-8") + '\'' +
                "&mc_gross_1='" + URLEncoder.encode(String.valueOf(mc_gross_1), "UTF-8") + '\'' +
                "&mc_handling='" + URLEncoder.encode(String.valueOf(mc_handling), "UTF-8") + '\'' +
                "&mc_handling1='" + URLEncoder.encode(String.valueOf(mc_handling1), "UTF-8") + '\'' +
                "&mc_shipping='" + URLEncoder.encode(String.valueOf(mc_shipping), "UTF-8") + '\'' +
                "&mc_shipping1='" + URLEncoder.encode(String.valueOf(mc_shipping1), "UTF-8") + '\'' +
                "&txn_type='" + URLEncoder.encode(String.valueOf(txn_type), "UTF-8") + '\'' +
                "&txn_id='" + URLEncoder.encode(String.valueOf(txn_id), "UTF-8") + '\'' +
                "&notify_version='" + URLEncoder.encode(String.valueOf(notify_version), "UTF-8") + '\'' +
                "&custom='" + URLEncoder.encode(String.valueOf(custom), "UTF-8") + '\'' +
                "&invoice='" + URLEncoder.encode(String.valueOf(invoice), "UTF-8") + '\'' +
                "&test_ipn='" + URLEncoder.encode(String.valueOf(test_ipn), "UTF-8") + '\'' +
                "&verify_sign='" + URLEncoder.encode(String.valueOf(verify_sign), "UTF-8") + '\'' +
                "&cmd=_notify-validate";
    }
}
