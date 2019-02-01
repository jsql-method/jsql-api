package pl.jsql.api.controller.members

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pl.jsql.api.dto.MemberAssignRequest
import pl.jsql.api.enums.RoleTypeEnum
import pl.jsql.api.security.annotation.Security
import pl.jsql.api.service.AppDevAppsService

@CrossOrigin
@RestController
@RequestMapping("/api/app-dev/application")
class AppDevAppsController {

    @Autowired
    AppDevAppsService appDevAppsService


    @Security(roles = [RoleTypeEnum.ADMIN, RoleTypeEnum.COMPANY_ADMIN, RoleTypeEnum.APP_ADMIN])
    @PostMapping
    def assign(
            @RequestBody MemberAssignRequest memberAssignRequest,
            @RequestHeader(value = "Session", required = false) String session) {

        def response = appDevAppsService.assign(memberAssignRequest)

        return new ResponseEntity(response, HttpStatus.OK)
    }

    @Security
    @GetMapping("/{id}")
    def getAll(
            @PathVariable("id") Long id,
            @RequestHeader(value = "Session", required = false) String session) {

        def response = appDevAppsService.getAll(id)

        return new ResponseEntity(response, HttpStatus.OK)
    }

    @Security(roles = [RoleTypeEnum.ADMIN, RoleTypeEnum.COMPANY_ADMIN, RoleTypeEnum.APP_ADMIN])
    @PostMapping("/unassign")
    def unassign(
            @RequestHeader(value = "Session", required = false) String session,
            @RequestBody MemberAssignRequest memberAssignRequest) {

        def response = appDevAppsService.unassign(memberAssignRequest)

        return new ResponseEntity(response, HttpStatus.OK)
    }

}
