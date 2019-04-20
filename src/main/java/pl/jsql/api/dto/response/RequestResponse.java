package pl.jsql.api.dto.response;

import java.util.Date;

public class RequestResponse {

    public String applicationName;
    public Long applicationId;

    public Date requestDate;
    public String query;
    public String hash;

    public RequestResponse(String applicationName, Long applicationId, Date requestDate, String query, String hash) {
        this.applicationName = applicationName;
        this.applicationId = applicationId;
        this.requestDate = requestDate;
        this.query = query;
        this.hash = hash;
    }

    public RequestResponse(String applicationName, Long applicationId, Date requestDate) {
        this.applicationName = applicationName;
        this.applicationId = applicationId;
        this.requestDate = requestDate;
    }

    public RequestResponse(String applicationName, Date requestDate) {
        this.applicationName = applicationName;
        this.requestDate = requestDate;
    }

}
