package pl.jsql.api.enums;

public enum HttpMessageEnum {

    SUCCESS(200, ""),
    BAD_REQUEST(400, "Bad request"),
    UNAUTHORIZED(401, "Unauthorized"),
    FORBIDDEN(403, "Insufficient permissions"),
    ALREADY_AUTHORIZED(600, "User already authorized"),
    ACTIVATION_TOKEN_ERROR(601, "User with that token does not exist"),
    QUERY_CHANGE_TYPE_ERROR(602, "You can not change the type of query"),
    API_KEY_DOES_NOT_MATCH(603, "Your application key does not match"),
    EMPTY_EMAIL(604, "Empty email field"),
    EMPTY_PASSWORD(605, "Empty password field"),
    ACCOUNT_NOT_EXIST(606, "Account with given data not exist"),
    PASSWORD_NOT_MATCH(607, "Password not match"),
    ACCOUNT_DISABLED(608, "Account is disabled"),
    ACCOUNT_NOT_ACTIVATED(609, "Account is not activated"),
    ACCOUNT_BLOCKED(610, "Account is blocked"),
    NO_SUCH_APP_OR_MEMBER(611, "No such application or member"),
    PASSWORD_MIN_LENGTH(612, "Password is too short"),
    EMAIL_TOO_LONG(613, "Given email is too long"),
    EMAIL_USED(614, "The given e-mail is already registered in the system. Use the Forgot Password option"),
    USER_EMAIL_NOT_FOUND(615, "User with given email not found"),
    ACTIVATION_URL_EXPIRED(616, "Activation URL has expired"),
    RESET_PASSWORD_URL_EXPIRED(617, "Reset password URL has expired"),
    CHANGE_PASSWORD_ERROR(618, "Old password is incorrect"),
    USER_AND_ROLE_ALREADY_EXISTS(619, "User with given role and email already exists"),
    QUERY_WITH_ID_DOES_NOT_EXIST(620, "Query with given id does not exist"),
    MISSING_HEADER(621, "You forgot to pass following header: "),
    COMPANY_NOT_FOUND(622, "Company with given id does not exist"),
    NO_ACTIVE_PLAN(623, "Your plan is not active. Make sure that your payment was successful or change your plan to `STARTER`"),
    APPS_LIMIT_REACHED(624, "You can not create more applications because of your plan"),
    DEVS_LIMIT_REACHED(625, "You can not create more members because of your plan"),
    APP_ALREADY_EXISTS(626, "There is an application with given name");

    Integer code;
    String description;

    HttpMessageEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public Integer getCode() {
        return code;
    }


    public String getDescription() {
        return description;
    }

}
