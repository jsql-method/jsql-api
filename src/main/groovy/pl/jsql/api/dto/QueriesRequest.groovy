package pl.jsql.api.dto

class QueriesRequest {

    Date dateFrom
    Date dateTo
    List<Long> applications
    List<Long> members
    Boolean used
    Boolean dynamic

    QueriesRequest() {
    }

    QueriesRequest(List<Long> applications, List<Long> members, Boolean used, Boolean dynamic) {
        this.applications = applications
        this.members = members
        this.used = used
        this.dynamic = dynamic
    }
}
