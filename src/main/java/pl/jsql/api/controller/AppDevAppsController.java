package pl.jsql.api.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import pl.jsql.api.controller.generic.ValidateController
import pl.jsql.api.dto.request.MemberAssignRequest
import pl.jsql.api.dto.response.BasicResponse
import pl.jsql.api.enums.RoleTypeEnum
import pl.jsql.api.security.annotation.Security
import pl.jsql.api.service.AppDevAppsService

import javax.validation.Valid

@CrossOrigin
@RestController
@RequestMapping("/api/app-dev/application")
public class  AppDevAppsController extends ValidateController {

    @Autowired
    AppDevAppsService appDevAppsService

    @Security(roles = [RoleTypeEnum.ADMIN, RoleTypeEnum.COMPANY_ADMIN, RoleTypeEnum.APP_ADMIN])
    @PostMapping
    BasicResponse assign(@RequestBody @Valid MemberAssignRequest memberAssignRequest) {
        def response = appDevAppsService.assign(memberAssignRequest)
        return new BasicResponse(status: 200, data: response)
    }

    @Security
    @GetMapping("/{id}")
    BasicResponse getAll(@PathVariable("id") Long id) {
        def response = appDevAppsService.getAll(id)
        return new BasicResponse(status: 200, data: response)
    }

    @Security(roles = [RoleTypeEnum.ADMIN, RoleTypeEnum.COMPANY_ADMIN, RoleTypeEnum.APP_ADMIN])
    @PostMapping("/unassign")
    BasicResponse unassign(@RequestBody @Valid MemberAssignRequest memberAssignRequest) {
        def response = appDevAppsService.unassign(memberAssignRequest)
        return new BasicResponse(status: 200, data: response)
    }

}
