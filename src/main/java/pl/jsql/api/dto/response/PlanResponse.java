package pl.jsql.api.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class PlanResponse {

    @JsonFormat(pattern = "dd-MM-yyyy")
    public Date activationDate;

    public Boolean active;
    public Integer maxApps;
    public Integer usedApps;
    public Integer maxUsers;
    public Integer usedUsers;
    public String name;


}
