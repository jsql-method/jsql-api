package pl.jsql.api.enums;

public enum SettingEnum {

    RESET_PASSWORD_LINK_EXPIRATION("2"),
    ACTIVATION_LINK_EXPIRATION("7"),
    ORIGIN_URL("https://customer.jsql.it");

    public String defaultValue;

    SettingEnum(String defaultValue) {
        this.defaultValue = defaultValue;
    }

}