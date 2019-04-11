package pl.jsql.api.dto.response;

import java.util.Date;

public class RequestResponse {

    public String applicationName;
    public Long applicationId;

    public Date requestDate;

    public RequestResponse(String applicationName, Long applicationId, Date requestDate) {
        this.applicationName = applicationName;
        this.applicationId = applicationId;
        this.requestDate = requestDate;
    }

}
