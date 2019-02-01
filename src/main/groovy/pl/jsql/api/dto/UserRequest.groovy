package pl.jsql.api.dto

class UserRequest {

    String password
    String email
    String firstName
    String lastName
    String role
    Long company
    String application
    String origin
    String plan

    UserRequest() {
    }

    UserRequest(String password, String email, String firstName, String lastName) {
        this.password = password
        this.email = email
        this.firstName = firstName
        this.lastName = lastName
        this.role = null
        this.company = null
        this.application = null
        this.origin = null
        this.plan = null
    }
}