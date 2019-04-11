package pl.jsql.api.dto.request;

import java.util.Date;

public class PabblyPaymentRequest {

    public String event_type;
    public String event_source;
    public PabblyPaymentDataRequest data;
    public Date create_time;

}
