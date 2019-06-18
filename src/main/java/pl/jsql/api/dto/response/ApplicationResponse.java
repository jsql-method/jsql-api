package pl.jsql.api.dto.response;

public class ApplicationResponse {

    public Long id;
    public String apiKey;
    public String name;
    public String developerKey;
    public String productionKey;
    public Boolean prodCache;

    public ApplicationResponse(Long id, String apiKey, String name, String developerKey, String productionKey, Boolean prodCache) {
        this.id = id;
        this.apiKey = apiKey;
        this.name = name;
        this.developerKey = developerKey;
        this.productionKey = productionKey;
        this.prodCache = prodCache;
    }

}
