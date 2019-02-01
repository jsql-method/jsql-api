package pl.jsql.api.dto

class MemberAssignRequest {

    Long member
    Long application

    MemberAssignRequest() {
    }

    MemberAssignRequest(Long member, Long application) {
        this.member = member
        this.application = application
    }

}