package pl.jsql.api.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class PlanResponse {

    @JsonFormat(pattern = "dd-MM-yyyy")
    public Date activationDate;

    @JsonFormat(pattern = "dd-MM-yyyy")
    public Date expiryDate;

    public Boolean active;
    public Integer maxApps;
    public Integer usedApps;
    public Integer maxUsers;
    public Integer usedUsers;
    public String name;
    public Boolean trial;
    public Integer trialDays;
    public boolean doubledSubscriptions;

}
