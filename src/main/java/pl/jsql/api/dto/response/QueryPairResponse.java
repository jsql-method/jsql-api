package pl.jsql.api.dto.response;

public class  QueryPairResponse {

    public String token;
    public String query;

    public QueryPairResponse(String token, String query) {
        this.token = token;
        this.query = query;
    }

}
