package pl.jsql.api.dto.response;

public class BuildsChartDataResponse {

    public String hashingDate;
    public Integer queriesCount;
    public String developerName;
    public String applicationName;

    public BuildsChartDataResponse(String hashingDate, Integer queriesCount, String developerName, String applicationName) {
        this.hashingDate = hashingDate;
        this.queriesCount = queriesCount;
        this.developerName = developerName;
        this.applicationName = applicationName;
    }

}
