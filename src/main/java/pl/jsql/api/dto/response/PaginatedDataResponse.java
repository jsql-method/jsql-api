package pl.jsql.api.dto.response;

public class  PaginatedDataResponse<T> {

    public T list;
    public PaginationResponse pagination;

    public PaginatedDataResponse(T list, PaginationResponse pagination) {
        this.list = list;
        this.pagination = pagination;
    }
}
