package pl.jsql.api.dto;


class BuildsRequest {
    Date dateFrom
    Date dateTo
    List<Long> applications
    List<Long> members

    BuildsRequest() {
    }

    BuildsRequest(List<Long> applications, List<Long> members) {
        this.applications = applications
        this.members = members
    }

    @Override
    public String toString() {
        return "BuildsRequest{" +
                "from=" + dateFrom +
                ", to=" + dateTo +
                ", applications=" + applications +
                ", members=" + members +
                '}';
    }
}
