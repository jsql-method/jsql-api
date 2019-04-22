package pl.jsql.api.dto.response;

import java.util.Date;

public class QueryResponse {

    public Long id;
    public String query;
    public String hash;
    public Date queryDate;
    public Boolean used;
    public Boolean dynamic;
    public String developerName;
    public String applicationName;
    public Long applicationId;

    public QueryResponse(Long id, String query, String hash, Date queryDate, Boolean used, Boolean dynamic, String developerName, String applicationName, Long applicationId) {
        this.id = id;
        this.query = query;
        this.hash = hash;
        this.queryDate = queryDate;
        this.used = used;
        this.dynamic = dynamic;
        this.developerName = developerName;
        this.applicationName = applicationName;
        this.applicationId = applicationId;
    }

}
