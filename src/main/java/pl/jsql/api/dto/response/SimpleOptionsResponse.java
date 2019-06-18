package pl.jsql.api.dto.response;

import pl.jsql.api.enums.DatabaseDialectEnum;

public class SimpleOptionsResponse {

    public DatabaseDialectEnum databaseDialect;
    public Integer productionDatabaseConnectionTimeout;
    public Integer developmentDatabaseConnectionTimeout;
}
