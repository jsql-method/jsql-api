package pl.jsql.api.enums;

import pl.jsql.api.dto.response.SelectResponse;

import java.util.ArrayList;
import java.util.List;

public enum DatabaseDialectEnum {

    POSTGRES,
    MYSQL;

    String name;
    String value;

    DatabaseDialectEnum(){
        this.name = this.toString();
        this.value = this.toString();
    }

    public static List<SelectResponse<DatabaseDialectEnum>> toSelectResponse(){

        List<SelectResponse<DatabaseDialectEnum>> list = new ArrayList<>();

        for(DatabaseDialectEnum databaseDialectEnum : DatabaseDialectEnum.values()){
            list.add(new SelectResponse<>(databaseDialectEnum));
        }

        return list;

    }

}
