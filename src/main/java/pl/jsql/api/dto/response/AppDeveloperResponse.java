package pl.jsql.api.dto.response;

public class AppDeveloperResponse {

    public Long id;
    public String email;
    public String firstName;
    public String lastName;
    public Boolean confirmed;

    public AppDeveloperResponse(Long id, String email, String firstName, String lastName, Boolean confirmed) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.confirmed = confirmed;
    }

}
