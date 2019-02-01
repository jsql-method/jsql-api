package pl.jsql.api.dto;


class RequestsRequest {
    Date dateFrom
    Date dateTo
    List<Long> applications

    RequestsRequest() {
    }

    RequestsRequest(List<Long> applications) {
        this.applications = applications
    }

    @Override
    public String toString() {
        return "BuildsRequest{" +
                "from=" + dateFrom +
                ", to=" + dateTo +
                ", applications=" + applications +
                '}';
    }
}
