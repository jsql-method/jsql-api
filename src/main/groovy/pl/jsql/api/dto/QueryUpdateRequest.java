package pl.jsql.api.dto;

public class QueryUpdateRequest {

    public String query;
    public String apiKey;

    public QueryUpdateRequest(){
    }

    public QueryUpdateRequest(String query, String apiKey) {
        this.query = query;
        this.apiKey = apiKey;
    }
}
