package pl.jsql.api.dto

class LoginRequest {

    String email
    String password
    String ipAddress

    LoginRequest() {

    }

    LoginRequest(String email, String password) {
        this.email = email
        this.password = password
    }

    LoginRequest(String email, String password, String ipAddress) {
        this.email = email
        this.password = password
        this.ipAddress = ipAddress
    }

}
