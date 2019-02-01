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

    public boolean hasPermission(RoleTypeEnum type) {
        return (this.id >= type.id);
    }

    @Override
    public String toString() {
        return this.name;
    }

    public static RoleTypeEnum fromString(String role) {
        for (RoleTypeEnum type : RoleTypeEnum.values()) {
            if (role.equals(type.name))
                return type;
        }
        throw new IllegalArgumentException("RoleTypeEnum fromString: '" + role + "' could not be resolved into object of type RoleTypeEnum");
    }

    public static RoleTypeEnum fromInteger(int role) {
        for (RoleTypeEnum type : RoleTypeEnum.values()) {
            if (role == type.id)
                return type;
        }
        throw new IllegalArgumentException("RoleTypeEnum fromInteger: '" + role + "' could not be resolved into object of type RoleTypeEnum");
    }

    public boolean isEqualTo(RoleTypeEnum role) {
        return (this.id == role.id);
    }

    public boolean isGreaterThan(RoleTypeEnum role) {
        return (this.id > role.id);
    }

    public boolean isGreaterOrEqualTo(RoleTypeEnum role) {
        return (this.id > role.id || this.id == role.id);
    }

    public boolean isLessThan(RoleTypeEnum role) {
        return (this.id < role.id);
    }

    public boolean isLessOrEqualTo(RoleTypeEnum role) {
        return (this.id < role.id || this.id == role.id);
    }
}
