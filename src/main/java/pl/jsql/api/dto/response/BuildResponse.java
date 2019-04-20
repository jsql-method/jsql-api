package pl.jsql.api.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class BuildResponse {

    public String applicationName;
    public Long applicationId;

    public String developerName;
    public Long developerId;

    @JsonFormat(pattern = "dd-MM-yyyy")
    public Date hashingDate;

    public Integer queriesCount;

    public BuildResponse(String applicationName, Long applicationId, String developerName, Long developerId, Date hashingDate, Integer queriesCount) {
        this.applicationName = applicationName;
        this.applicationId = applicationId;
        this.developerName = developerName;
        this.developerId = developerId;
        this.hashingDate = hashingDate;
        this.queriesCount = queriesCount;
    }

    public BuildResponse(String applicationName, String developerName, Date hashingDate, Integer queriesCount) {
        this.applicationName = applicationName;
        this.developerName = developerName;
        this.hashingDate = hashingDate;
        this.queriesCount = queriesCount;
    }

}
