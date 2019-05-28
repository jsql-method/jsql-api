package pl.jsql.api.dto.response;

import pl.jsql.api.enums.DatabaseDialectEnum;
import pl.jsql.api.enums.EncodingEnum;
import pl.jsql.api.model.hashing.Application;

public class OptionsResponse {

    public String apiKey;

    public EncodingEnum encodingAlgorithm;

    public Boolean isSalt;

    public String salt;

    public Boolean saltBefore;

    public Boolean saltAfter;

    public Boolean saltRandomize;

    public Boolean hashLengthLikeQuery;

    public Integer hashMinLength;

    public Integer hashMaxLength;

    public Boolean removeQueriesAfterBuild;

    public DatabaseDialectEnum databaseDialect;

    public Boolean allowedPlainQueries;

    public Boolean prod;

    public String randomSaltBefore;

    public String randomSaltAfter;

    public DatabaseConnectionResponse productionDatabaseOptions;

    public DatabaseConnectionResponse developerDatabaseOptions;

    @Override
    public String toString() {
        return "OptionsResponse{" +
                "apiKey='" + apiKey + '\'' +
                ", encodingAlgorithm=" + encodingAlgorithm +
                ", isSalt=" + isSalt +
                ", salt='" + salt + '\'' +
                ", saltBefore=" + saltBefore +
                ", saltAfter=" + saltAfter +
                ", saltRandomize=" + saltRandomize +
                ", hashLengthLikeQuery=" + hashLengthLikeQuery +
                ", hashMinLength=" + hashMinLength +
                ", hashMaxLength=" + hashMaxLength +
                ", removeQueriesAfterBuild=" + removeQueriesAfterBuild +
                ", databaseDialect=" + databaseDialect +
                ", allowedPlainQueries=" + allowedPlainQueries +
                ", prod=" + prod +
                ", randomSaltBefore='" + randomSaltBefore + '\'' +
                ", randomSaltAfter='" + randomSaltAfter + '\'' +
                ", productionDatabaseOptions=" + productionDatabaseOptions +
                ", developerDatabaseOptions=" + developerDatabaseOptions +
                '}';
    }

}
