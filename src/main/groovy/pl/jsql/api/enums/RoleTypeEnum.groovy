package pl.jsql.api.enums;

public enum RoleTypeEnum {

    APP_DEV(100, "APP_DEV"),
    APP_ADMIN(200, "APP_ADMIN"),
    COMPANY_ADMIN(300, "COMPANY_ADMIN"),
    ADMIN(400, "ADMIN"),
    PUBLIC(500, "PUBLIC");

    public int id;
    public String name;

    RoleTypeEnum(int id, String name) {
        this.id = id;
        this.name = name;
    }

}
