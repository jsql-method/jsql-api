package pl.jsql.api.enums;

public enum DatabaseDialectEnum {

    POSTGRES,
    MYSQL;

    String name;
    String value;

    DatabaseDialectEnum(){
        this.name = this.toString();
        this.value = this.toString();
    }

}
