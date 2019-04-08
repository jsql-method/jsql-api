package pl.jsql.api.dto.response;

public class  BasicResponse<T> {

    public Integer status;
    public T data;

    public BasicResponse(Integer status, T data) {
        this.status = status;
        this.data = data;
    }

}