package pl.jsql.api.dto.request;

import pl.jsql.api.enums.PlansEnum;

public class UserRequest {

    public String password;
    public String email;
    public String firstName;
    public String lastName;
    public String role;
    public Long company;
    public String application;
    public PlansEnum plan;

    public UserRequest() {
    }

    public UserRequest(String password, String email, String firstName, String lastName, String role, Long company, String application, PlansEnum plan) {
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