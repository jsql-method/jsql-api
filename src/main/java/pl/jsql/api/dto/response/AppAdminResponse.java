package pl.jsql.api.dto.response;

public class AppAdminResponse {

    public Long id;
    public String email;
    public String firstName;
    public String lastName;
    public Boolean confirmed;
    public Boolean isCompanyAdmin;

    public AppAdminResponse(Long id, String email, String firstName, String lastName, Boolean confirmed) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.confirmed = confirmed;
        this.isCompanyAdmin = false;
    }

    public AppAdminResponse(Long id, String email, String firstName, String lastName, Boolean confirmed, Boolean isCompanyAdmin) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.confirmed = confirmed;
        this.isCompanyAdmin = isCompanyAdmin;
    }

}
