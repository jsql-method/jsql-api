package pl.jsql.api.dto.request;

import javax.validation.constraints.NotNull;

public class DatabaseConnectionRequest {

    @NotNull(message = "${validation.message.notNull}")
    public Integer databaseConnectionTimeout;

    public String databaseConnectionUrl;

    public String databaseConnectionUsername;

    public String databaseConnectionPassword;

}
