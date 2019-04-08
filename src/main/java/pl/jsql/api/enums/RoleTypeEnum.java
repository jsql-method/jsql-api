package pl.jsql.api.enums;

public enum RoleTypeEnum {

    APP_DEV("APP_DEV"),
    APP_ADMIN("APP_ADMIN"),
    COMPANY_ADMIN("COMPANY_ADMIN"),
    ADMIN("ADMIN"),
    PUBLIC("PUBLIC");

    public String name;

    RoleTypeEnum(String name) {
        this.name = name;
    }

}
