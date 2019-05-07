package pl.jsql.api.enums;

public enum SettingEnum {

    ORIGIN_URL("https://customer.jsql.it");

    public String defaultValue;

    SettingEnum(String defaultValue) {
        this.defaultValue = defaultValue;
    }

}