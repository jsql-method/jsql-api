package pl.jsql.api.dto.response;

public class SelectResponse<T> {

    public T name;
    public T value;

    public SelectResponse() {
    }

    public SelectResponse(T enumVal) {
        this.name = enumVal;
        this.value = enumVal;
    }

}
