package pl.jsql.api.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import pl.jsql.api.controller.generic.ValidateController
import pl.jsql.api.dto.request.QueryUpdateRequest
import pl.jsql.api.dto.response.BasicResponse
import pl.jsql.api.enums.RoleTypeEnum
import pl.jsql.api.security.annotation.Security
import pl.jsql.api.service.ApiService
import pl.jsql.api.service.HashingService

import javax.validation.Valid

@CrossOrigin
@RestController
@RequestMapping("/api/query")
class QueryController extends ValidateController {

    @Autowired
    ApiService apiService

    @Security(roles = [RoleTypeEnum.ADMIN, RoleTypeEnum.APP_ADMIN, RoleTypeEnum.APP_DEV])
    @PatchMapping("/query/{id}")
    BasicResponse updateQuery(@PathVariable("id") Long id, @RequestBody @Valid QueryUpdateRequest queryUpdateRequest) {
        def response = apiService.updateQueriesById(id, queryUpdateRequest)
        return new BasicResponse(status: 200, data: response)
    }

}
