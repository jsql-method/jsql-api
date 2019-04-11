package pl.jsql.api.dto.request;

import java.math.BigDecimal;
import java.util.Date;

public class PabblyPaymentDataRequest {

    public PabblyPaymentPlanRequest plan;
    public String setup_fee;
    public String payment_terms;
    public String currency_symbol;
    public String payment_method;
    public String cron_process;
    public String createdAt;
    public String updatedAt;
    public String id;
    public String customer_id;
    public String product_id;
    public String plan_id;
    public BigDecimal amount;
    public String email_id;
    public String status;
    public BigDecimal quantity;
    public Date starts_at;
    public Date activation_date;
    public Date expires_at;
    public Date expiry_date;
    public Integer trial_days;
    public Date trial_expiry_date;
    public Date next_billing_date;
    public Date last_billing_date;
    public PabblyTransactionRequest transaction;

}
