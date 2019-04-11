package pl.jsql.api.dto.request;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class  ForgotPasswordRequest {

    @NotNull(message = "${validation.message.notNull}")
    @NotEmpty(message = "${validation.message.notEmpty}")
    public String email;

    public ForgotPasswordRequest() {
    }

    public ForgotPasswordRequest(String email) {
        this.email = email;
    }

}
