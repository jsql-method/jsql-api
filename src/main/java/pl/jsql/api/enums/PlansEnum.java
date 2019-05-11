package pl.jsql.api.enums;

public enum PlansEnum {

    STARTER("STARTER", 1, 1),
    BUSINESS("BUSINESS", 10, 5),
    LARGE("LARGE", 20, 15);

    public String name;
    public int maxApps;
    public int maxUsers;

    PlansEnum(String name, int maxApps, int maxUsers) {
        this.name = name;
        this.maxApps = maxApps;
        this.maxUsers = maxUsers;
    }

}