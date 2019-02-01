package pl.jsql.api.controller.members

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pl.jsql.api.dto.UserRequest
import pl.jsql.api.enums.RoleTypeEnum
import pl.jsql.api.security.annotation.Security
import pl.jsql.api.service.AppAdminService

import javax.servlet.http.HttpServletRequest

@CrossOrigin
@RestController
@RequestMapping("/api/app-admin")
class AppAdminController {

    @Autowired
    AppAdminService appAdminService

    @Security(roles = [RoleTypeEnum.ADMIN, RoleTypeEnum.COMPANY_ADMIN, RoleTypeEnum.APP_ADMIN])
    @GetMapping
    def getAll(@RequestHeader(value = "Session", required = false) String session) {

        def response = appAdminService.getAll()

        return new ResponseEntity(response, HttpStatus.OK)

    }

    @Security(roles = [RoleTypeEnum.ADMIN, RoleTypeEnum.COMPANY_ADMIN])
    @PostMapping
    def create(@RequestHeader(value = "Session", required = false) String session, HttpServletRequest request,
               @RequestBody UserRequest userRequest) {

        userRequest.origin = request.getHeader('origin')

        def response = appAdminService.register(userRequest)

        return new ResponseEntity(response, HttpStatus.OK)
    }

    @Security(roles = [RoleTypeEnum.ADMIN, RoleTypeEnum.COMPANY_ADMIN])
    @PatchMapping
    def demote(@RequestHeader(value = "Session", required = false) String session,
               @RequestBody UserRequest userRequest) {

        def response = appAdminService.demote(userRequest)

        return new ResponseEntity(response, HttpStatus.OK)
    }


}
