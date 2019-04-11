package pl.jsql.api.dto.request;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public class ChangePasswordRequest {

    @NotEmpty(message = "${validation.message.notEmpty}")
    @Size(min = 8, message = "${validation.message.size}")
    public String newPassword;

    @NotEmpty(message = "${validation.message.notEmpty}")
    @Size(min = 8, message = "${validation.message.size}")
    public String oldPassword;

}
