package pl.jsql.api.dto.request;

import java.util.Date;
import java.util.List;

public class  RequestsRequest {

    public Date dateFrom;
    public Date dateTo;
    public List<Long> applications;
}
