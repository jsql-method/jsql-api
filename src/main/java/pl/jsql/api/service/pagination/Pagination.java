package pl.jsql.api.service.pagination;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import pl.jsql.api.dto.response.PaginationResponse;
import pl.jsql.api.enums.PageLimit;

@Service
public class Pagination {

    public PaginationResponse paginate(int page, IPaginationCount paginationCount) {
        return this.paginate(page, paginationCount.count());
    }

    public PaginationResponse paginate(int page, Integer paginationCount) {

        PaginationResponse paginationDTO = new PaginationResponse();

        paginationDTO.totalPages = paginationCount / PageLimit.LIMIT + 1;
        paginationDTO.page = page;
        paginationDTO.offset = PageLimit.LIMIT * page;
        paginationDTO.limit = PageLimit.LIMIT;

        paginationDTO.pageRequest = PageRequest.of(page, PageLimit.LIMIT);

        return paginationDTO;

    }

}
