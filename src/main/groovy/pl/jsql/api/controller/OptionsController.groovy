package pl.jsql.api.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pl.jsql.api.dto.OptionsRequest
import pl.jsql.api.enums.RoleTypeEnum
import pl.jsql.api.security.annotation.Security
import pl.jsql.api.service.OptionsService

@CrossOrigin
@RestController
@RequestMapping("/api/options")
class OptionsController {

    @Autowired
    OptionsService optionsService

    @Security
    @GetMapping
    def getAll(@RequestHeader(value = "Session", required = false) String session) {

        def response = optionsService.getAll()


        return new ResponseEntity(response, HttpStatus.OK)
    }

    @Security(roles = [RoleTypeEnum.ADMIN, RoleTypeEnum.COMPANY_ADMIN, RoleTypeEnum.APP_ADMIN])
    @GetMapping("/{id}")
    def get(
            @PathVariable("id") Long id,
            @RequestHeader(value = "Session", required = false) String session
    ) {
        def response = optionsService.getByAppId(id)

        return new ResponseEntity(response, HttpStatus.OK)
    }

    @Security(roles = [RoleTypeEnum.ADMIN, RoleTypeEnum.COMPANY_ADMIN, RoleTypeEnum.APP_ADMIN])
    @PatchMapping("/{id}")
    def update(
            @PathVariable("id") Long id,
            @RequestBody OptionsRequest optionsRequest,
            @RequestHeader(value = "Session", required = false) String session
    ) {
        def response = optionsService.update(id, optionsRequest)

        return new ResponseEntity(response, HttpStatus.OK)
    }

    @Security(roles = [RoleTypeEnum.ADMIN, RoleTypeEnum.COMPANY_ADMIN, RoleTypeEnum.APP_ADMIN])
    @GetMapping("/values")
    def getOptionsValue(@RequestHeader(value = "Session", required = false) String session) {

        def response = optionsService.getValues()

        return new ResponseEntity(response, HttpStatus.OK)
    }

}
