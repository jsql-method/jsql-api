package pl.jsql.api.dto.request;

import java.util.Date;
import java.util.List;

public class BuildsRequest {

    public Date dateFrom;
    public Date dateTo;
    public List<Long> applications;
    public List<Long> developers;

}
