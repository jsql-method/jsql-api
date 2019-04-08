package pl.jsql.api.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import pl.jsql.api.controller.generic.ValidateController
import pl.jsql.api.dto.request.UserRequest
import pl.jsql.api.dto.response.BasicResponse
import pl.jsql.api.enums.RoleTypeEnum
import pl.jsql.api.security.annotation.Security
import pl.jsql.api.service.AppAdminService

import javax.servlet.http.HttpServletRequest
import javax.validation.Valid

@CrossOrigin
@RestController
@RequestMapping("/api/app-admin")
public class  AppAdminController extends ValidateController {

    @Autowired
    AppAdminService appAdminService

    @Security(roles = [RoleTypeEnum.ADMIN, RoleTypeEnum.COMPANY_ADMIN, RoleTypeEnum.APP_ADMIN])
    @GetMapping
    BasicResponse getAll() {
        def response = appAdminService.getAll()
        return new BasicResponse(status: 200, data: response)
    }

    @Security(roles = [RoleTypeEnum.ADMIN, RoleTypeEnum.COMPANY_ADMIN])
    @PostMapping
    BasicResponse create(HttpServletRequest request, @RequestBody @Valid UserRequest userRequest) {
        userRequest.origin = request.getHeader('origin')
        def response = appAdminService.register(userRequest)
        return new BasicResponse(status: 200, data: response)
    }

    @Security(roles = [RoleTypeEnum.ADMIN, RoleTypeEnum.COMPANY_ADMIN])
    @PatchMapping
    BasicResponse demote(@RequestBody @Valid UserRequest userRequest) {
        def response = appAdminService.demote(userRequest)
        return new BasicResponse(status: 200, data: response)
    }


}
