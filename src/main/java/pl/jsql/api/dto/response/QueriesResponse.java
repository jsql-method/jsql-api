package pl.jsql.api.dto.response;

import java.util.List;

public class QueriesResponse {

    public Long totalQueries;
    public Long todayQueries;

    public List<QueryResponse> queries;

}
