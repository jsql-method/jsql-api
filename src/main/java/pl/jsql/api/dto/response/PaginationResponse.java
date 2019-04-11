package pl.jsql.api.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.domain.PageRequest;

public class  PaginationResponse {

    public Integer totalPages;

    @JsonIgnore
    public Integer offset;

    public Integer page;

    @JsonIgnore
    public Integer limit;

    @JsonIgnore
    public PageRequest pageRequest;
}
