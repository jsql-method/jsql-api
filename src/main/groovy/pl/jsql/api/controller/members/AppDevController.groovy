package pl.jsql.api.controller.members

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pl.jsql.api.dto.UserRequest
import pl.jsql.api.enums.RoleTypeEnum
import pl.jsql.api.security.annotation.Security
import pl.jsql.api.service.AppDevService

import javax.servlet.http.HttpServletRequest

@CrossOrigin
@RestController
@RequestMapping("/api/app-dev")
class AppDevController {

    @Autowired
    AppDevService appDevService

    @Security(roles = [RoleTypeEnum.ADMIN, RoleTypeEnum.COMPANY_ADMIN, RoleTypeEnum.APP_ADMIN])
    @GetMapping
    def getAll(@RequestHeader(value = "Session", required = false) String session) {

        def response = appDevService.getAll()

        return new ResponseEntity(response, HttpStatus.OK)
    }

    @Security(roles = [RoleTypeEnum.ADMIN, RoleTypeEnum.COMPANY_ADMIN, RoleTypeEnum.APP_ADMIN])
    @PostMapping
    def create(
            @RequestBody UserRequest userRequest,
            @RequestHeader(value = "Session", required = false) String session, HttpServletRequest request) {

        userRequest.origin = request.getHeader('origin')

        def response = appDevService.register(userRequest)

        return new ResponseEntity(response, HttpStatus.OK)
    }

    @Security(roles = [RoleTypeEnum.ADMIN, RoleTypeEnum.COMPANY_ADMIN, RoleTypeEnum.APP_ADMIN])
    @DeleteMapping("/{id}")
    def delete(
            @PathVariable("id") Long id,
            @RequestHeader(value = "Session", required = false) String session) {

        def response = appDevService.delete(id)

        return new ResponseEntity(response, HttpStatus.OK)
    }

}
