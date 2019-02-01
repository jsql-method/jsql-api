package pl.jsql.api.dto

class OptionsRequest {

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
    String applicationLanguage
    Boolean allowedPlainQueries
    Boolean prod

    OptionsRequest() {
    }
}