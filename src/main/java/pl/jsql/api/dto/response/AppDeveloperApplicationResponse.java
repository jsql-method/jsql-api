package pl.jsql.api.dto.response;

public class AppDeveloperApplicationResponse {

    public Boolean assigned;
    public Long developerId;
    public Long applicationId;
    public String applicationName;

    public AppDeveloperApplicationResponse(Boolean assigned, Long developerId, Long applicationId, String applicationName) {
        this.assigned = assigned;
        this.developerId = developerId;
        this.applicationId = applicationId;
        this.applicationName = applicationName;
    }


}
