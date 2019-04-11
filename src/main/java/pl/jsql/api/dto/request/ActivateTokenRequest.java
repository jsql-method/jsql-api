package pl.jsql.api.dto.request;

import javax.validation.constraints.NotEmpty;

public class ActivateTokenRequest {

    @NotEmpty(message = "${validation.message.notEmpty}")
    public String token;

}
