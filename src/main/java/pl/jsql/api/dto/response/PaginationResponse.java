package pl.jsql.api.dto.response

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.data.domain.PageRequest

public class  PaginationResponse {

    Integer totalPages

    @JsonIgnore
    Integer offset

    Integer page

    @JsonIgnore
    Integer limit

    @JsonIgnore
    PageRequest pageRequest

}
