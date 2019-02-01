package pl.jsql.api.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pl.jsql.api.enums.RoleTypeEnum
import pl.jsql.api.security.annotation.Security
import pl.jsql.api.service.ApplicationService

@CrossOrigin
@RestController
@RequestMapping("/api/application")
class ApplicationController {

    @Autowired
    ApplicationService applicationService

    @Security(roles = [RoleTypeEnum.ADMIN, RoleTypeEnum.COMPANY_ADMIN, RoleTypeEnum.APP_ADMIN])
    @PostMapping
    def create(
            @RequestBody def name,
            @RequestHeader(value = "Session", required = false) String session) {

        def response = applicationService.create(String.valueOf(name.name))

        return new ResponseEntity(response, HttpStatus.OK)
    }

    @Security
    @GetMapping
    def getAll(@RequestHeader(value = "Session", required = false) String session) {

        def response = applicationService.getAll()

        return new ResponseEntity(response, HttpStatus.OK)
    }

    @Security
    @GetMapping("/{id}")
    def get(@PathVariable("id") Long id, @RequestHeader(value = "Session", required = false) String session) {


        def response = applicationService.getById(id)

        return new ResponseEntity(response, HttpStatus.OK)
    }

    @Security(roles = [RoleTypeEnum.ADMIN, RoleTypeEnum.COMPANY_ADMIN, RoleTypeEnum.APP_ADMIN])
    @PatchMapping("/{id}")
    def disable(@PathVariable("id") Long id, @RequestHeader(value = "Session", required = false) String session) {

        def response = applicationService.disable(id)

        return new ResponseEntity(response, HttpStatus.OK)
    }
}
