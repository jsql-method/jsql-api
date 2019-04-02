package pl.jsql.api.dto.request

class QueriesRequest {

    Date dateFrom
    Date dateTo
    List<Long> applications
    List<Long> members
    Boolean used
    Boolean dynamic

}
