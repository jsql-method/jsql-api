package pl.jsql.api.dto.request;

import javax.validation.constraints.NotNull;

public class DeveloperAssignRequest {

    @NotNull(message = "${validation.message.notNull}")
    public Long developer;

    @NotNull(message = "${validation.message.notNull}")
    public Long application;

}