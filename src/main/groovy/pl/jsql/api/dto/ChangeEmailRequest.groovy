package pl.jsql.api.dto

class ChangeEmailRequest {

    String oldEmail
    String newEmail

    ChangeEmailRequest() {

    }

    ChangeEmailRequest(String oldEmail, String newEmail) {
        this.oldEmail = oldEmail
        this.newEmail = newEmail
    }
}
