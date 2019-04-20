package pl.jsql.api.dto.response;

public class RequestsChartDataResponse {

    public String requestDate;
    public String applicationName;
    public Long requestsCount;

    public RequestsChartDataResponse(String requestDate, String applicationName, Long requestsCount) {
        this.requestDate = requestDate;
        this.applicationName = applicationName;
        this.requestsCount = requestsCount;
    }

}
