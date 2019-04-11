package pl.jsql.api.dto.response;

import java.util.List;

public class QueriesResponse {

    public Integer totalQueries;
    public Integer todayQueries;

    public List<QueryResponse> queries;

}
