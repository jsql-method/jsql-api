package pl.jsql.api.dto.request;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class  LoginRequest {

    @NotNull(message = "${validation.message.notNull}")
    @NotEmpty(message = "${validation.message.notEmpty}")
    public String email;

    @NotNull(message = "${validation.message.notNull}")
    @NotEmpty(message = "${validation.message.notEmpty}")
    public String password;

    public String ipAddress;

}
