package pl.jsql.api.dto.request

public class  OptionsRequest {

    Boolean encodeQuery
    String encodingAlgorithm
    Boolean isSalt
    String salt
    Boolean saltBefore
    Boolean saltAfter
    Boolean saltRandomize
    Boolean hashLengthLikeQuery
    Integer hashMinLength
    Integer hashMaxLength
    Boolean removeQueriesAfterBuild
    String databaseDialect
    Boolean allowedPlainQueries
    Boolean prod

}