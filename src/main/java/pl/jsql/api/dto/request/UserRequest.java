package pl.jsql.api.dto.request;

import pl.jsql.api.enums.PlansEnum;
import pl.jsql.api.enums.RoleTypeEnum;
import pl.jsql.api.validator.UniqueEmail;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public class UserRequest {

    @NotEmpty(message = "${validation.message.notEmpty}")
    @Size(min = 5, max = 100, message = "${validation.message.size}")
    @Email(regexp = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$", message = "${validation.message.email.pattern}")
    @UniqueEmail
    public String email;

    @NotEmpty(message = "${validation.message.notEmpty}")
    @Size(min = 8, message = "${validation.message.size}")
    public String password;

    @NotEmpty(message = "${validation.message.notEmpty}")
    @Size(min = 1, max = 100, message = "${validation.message.size}")
    public String firstName;

    @NotEmpty(message = "${validation.message.notEmpty}")
    @Size(min = 1, max = 100, message = "${validation.message.size}")
    public String lastName;

    public String pabblySubscriptionId;

    public RoleTypeEnum role;
    public Long company;
    public String companyName;
    public String application;
    public PlansEnum plan;

    public Boolean isFakeDeveloper = false;

    public Boolean isTrial = false;

    public UserRequest() {
    }

    public UserRequest(@NotEmpty(message = "${validation.message.notEmpty}") @Size(min = 5, max = 100, message = "${validation.message.size}") @Email(regexp = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$", message = "${validation.message.email.pattern}") String email, @NotEmpty(message = "${validation.message.notEmpty}") @Size(min = 8, message = "${validation.message.size}") String password, @NotEmpty(message = "${validation.message.notEmpty}") @Size(min = 1, max = 100, message = "${validation.message.size}") String firstName, @NotEmpty(message = "${validation.message.notEmpty}") @Size(min = 1, max = 100, message = "${validation.message.size}") String lastName, RoleTypeEnum role, Long company, String companyName, String application, PlansEnum plan, Boolean isFakeDeveloper) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.company = company;
        this.companyName = companyName;
        this.application = application;
        this.plan = plan;
        this.isFakeDeveloper = isFakeDeveloper;
    }

    public UserRequest(String email, String password, String firstName, String lastName, String companyName, PlansEnum plan) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.companyName = companyName;
        this.plan = plan;
    }

    @Override
    public String toString() {
        return "UserRequest{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", role=" + role +
                ", company=" + company +
                ", companyName='" + companyName + '\'' +
                ", application='" + application + '\'' +
                ", plan=" + plan +
                ", isFakeDeveloper=" + isFakeDeveloper +
                '}';
    }

}