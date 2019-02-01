package pl.jsql.api.enums;

public enum LicenseEnum {
    DEMO(0, "DEMO"),
    PAID(100, "PAID");

    public int id;
    public String name;

    LicenseEnum(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public boolean isEqualTo(LicenseEnum license) {
        return this.id == license.id;
    }
}
