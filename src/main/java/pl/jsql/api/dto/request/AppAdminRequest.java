package pl.jsql.api.dto.request;

import pl.jsql.api.validator.UniqueEmail;
import pl.jsql.api.validator.UniqueUserNameForCurrentCompany;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@UniqueUserNameForCurrentCompany
public class AppAdminRequest {

    @NotEmpty(message = "${validation.message.notEmpty}")
    @Size(min = 5, max = 100, message = "${validation.message.size}")
    @Email(regexp = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$", message = "${validation.message.email.pattern}")
    @UniqueEmail
    public String email;

    @NotEmpty(message = "${validation.message.notEmpty}")
    @Size(min = 1, max = 100, message = "${validation.message.size}")
    public String firstName;

    @NotEmpty(message = "${validation.message.notEmpty}")
    @Size(min = 1, max = 100, message = "${validation.message.size}")
    public String lastName;

}