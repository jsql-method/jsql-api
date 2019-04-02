package pl.jsql.api.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import pl.jsql.api.controller.generic.ValidateController
import pl.jsql.api.dto.request.UserRequest
import pl.jsql.api.dto.response.BasicResponse
import pl.jsql.api.enums.RoleTypeEnum
import pl.jsql.api.security.annotation.Security
import pl.jsql.api.service.AppDevService

import javax.servlet.http.HttpServletRequest
import javax.validation.Valid

@CrossOrigin
@RestController
@RequestMapping("/api/app-dev")
class AppDevController extends ValidateController {

    @Autowired
    AppDevService appDevService

    @Security(roles = [RoleTypeEnum.ADMIN, RoleTypeEnum.COMPANY_ADMIN, RoleTypeEnum.APP_ADMIN])
    @GetMapping
    BasicResponse getAll() {
        def response = appDevService.getAll()
        return new BasicResponse(status: 200, data: response)
    }

    @Security(roles = [RoleTypeEnum.ADMIN, RoleTypeEnum.COMPANY_ADMIN, RoleTypeEnum.APP_ADMIN])
    @PostMapping
    BasicResponse create(@RequestBody @Valid UserRequest userRequest, HttpServletRequest request) {
        userRequest.origin = request.getHeader('origin')
        def response = appDevService.register(userRequest)
        return new BasicResponse(status: 200, data: response)
    }

    @Security(roles = [RoleTypeEnum.ADMIN, RoleTypeEnum.COMPANY_ADMIN, RoleTypeEnum.APP_ADMIN])
    @DeleteMapping("/{id}")
    BasicResponse delete(@PathVariable("id") Long id) {
        def response = appDevService.delete(id)
        return new BasicResponse(status: 200, data: response)
    }

}
