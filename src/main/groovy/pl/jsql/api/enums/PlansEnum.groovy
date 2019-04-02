package pl.jsql.api.enums;

public enum PlansEnum {
    STARTER(1, "STARTER", 1, 1),
    BUSINESS(2, "BUSINESS", 5, 10),
    LARGE(3, "LARGE", 15, 20),
    ENTERPRISE(4, "ENTERPRISE", 0, 0);


    public int id;
    public String name;
    public int appQty;
    public int userQty;

    PlansEnum(int id, String name, int appQty, int userQty) {
        this.id = id;
        this.name = name;
        this.appQty = appQty;
        this.userQty = userQty;
    }

    public boolean isEqualTo(PlansEnum plansEnum) {
        return this.id == plansEnum.id;
    }

}