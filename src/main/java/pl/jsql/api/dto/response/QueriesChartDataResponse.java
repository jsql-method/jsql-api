package pl.jsql.api.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class QueriesChartDataResponse {

    public Long id;

    @JsonFormat(pattern = "dd-MM-yyyy")
    public Date queryDate;

    public String developerName;
    public String applicationName;

    public QueriesChartDataResponse(Long id, Date queryDate, String developerName, String applicationName) {
        this.id = id;
        this.queryDate = queryDate;
        this.developerName = developerName;
        this.applicationName = applicationName;
    }

}
