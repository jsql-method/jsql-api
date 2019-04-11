package pl.jsql.api.dto.request;

import pl.jsql.api.enums.PlansEnum;
import pl.jsql.api.enums.RoleTypeEnum;
import pl.jsql.api.validator.UniqueEmail;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public class UserRequest {

    @NotEmpty(message = "${validation.message.notEmpty}")
    @Size(min = 8, message = "${validation.message.size}")
    public String password;

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

    public RoleTypeEnum role;
    public Long company;
    public String application;
    public PlansEnum plan;

    public UserRequest() {
    }

    public UserRequest(String password, String email, String firstName, String lastName, RoleTypeEnum role, Long company, String application, PlansEnum plan) {
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.company = company;
        this.application = application;
        this.plan = plan;
    }

}