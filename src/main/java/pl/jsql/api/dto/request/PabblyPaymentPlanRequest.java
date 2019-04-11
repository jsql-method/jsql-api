package pl.jsql.api.dto.request;

import java.math.BigDecimal;
import java.util.Date;

public class PabblyPaymentPlanRequest {

    public Boolean plan_active;
    public String bump_offer;
    public Date createdAt;
    public Date updatedAt;
    public String id;
    public String product_id;
    public String user_id;
    public String plan_name;
    public String plan_code;
    public BigDecimal price;
    public String billing_period;
    public String billing_period_num;
    public String billing_cycle;
    public String billing_cycle_num;
    public Integer trial_period;
    public BigDecimal setup_fee;
    public String plan_description;

}
