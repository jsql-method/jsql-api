package pl.jsql.api.dto.request;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class  QueryUpdateRequest {

    @NotNull(message = "${validation.message.notNull}")
    @NotEmpty(message = "${validation.message.notEmpty}")
    public String query;

    @NotNull(message = "${validation.message.notNull}")
    public Long applicationId;

}
