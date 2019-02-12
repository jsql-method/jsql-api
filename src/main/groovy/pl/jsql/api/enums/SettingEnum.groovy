package pl.jsql.api.enums

enum SettingEnum {

    RESET_PASSWORD_LINK_EXPIRATION(2),
    ACTIVATION_LINK_EXPIRATION(7),
    ORIGIN_URL("https://customer.jsql.it")

    def defaultValue

    SettingEnum(def defaultValue) {
        this.defaultValue = defaultValue
    }

}