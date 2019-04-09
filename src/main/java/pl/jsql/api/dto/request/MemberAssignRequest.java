package pl.jsql.api.dto.request;

import javax.validation.constraints.NotNull;

public class  MemberAssignRequest {

    @NotNull
    public Long member;

    @NotNull
    public Long application;

}