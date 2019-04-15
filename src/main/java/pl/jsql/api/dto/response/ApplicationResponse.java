package pl.jsql.api.dto.response;

public class ApplicationResponse {

    public Long id;
    public String apiKey;
    public String name;
    public String developerKey;
    public Boolean prod;

    public ApplicationResponse(Long id, String apiKey, String name, String developerKey, Boolean prod) {
        this.id = id;
        this.apiKey = apiKey;
        this.name = name;
        this.developerKey = developerKey;
        this.prod = prod;
    }

}
