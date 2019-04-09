package pl.jsql.api.dto.request;

import javax.validation.constraints.NotNull;

public class  QueryUpdateRequest {

    @NotNull
    public String query;

    @NotNull
    public Long applicationId;

}
