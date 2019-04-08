package pl.jsql.api.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import pl.jsql.api.controller.generic.ValidateController
import pl.jsql.api.dto.request.ApplicationCreateRequest
import pl.jsql.api.dto.response.BasicResponse
import pl.jsql.api.enums.RoleTypeEnum
import pl.jsql.api.security.annotation.Security
import pl.jsql.api.service.ApplicationService

import javax.validation.Valid

@CrossOrigin
@RestController
@RequestMapping("/api/application")
public class  ApplicationController extends ValidateController {

    @Autowired
    ApplicationService applicationService

    @Security
    @GetMapping
    BasicResponse getAll() {
        def response = applicationService.getAll()
        return new BasicResponse(status: 200, data: response)
    }

    @Security(roles = [RoleTypeEnum.ADMIN, RoleTypeEnum.COMPANY_ADMIN, RoleTypeEnum.APP_ADMIN])
    @PostMapping
    BasicResponse create(@RequestBody @Valid ApplicationCreateRequest name) {
        def response = applicationService.create(name.name)
        return new BasicResponse(status: 200, data: response)
    }

    @Security
    @GetMapping("/{id}")
    BasicResponse get(@PathVariable("id") Long id) {
        def response = applicationService.getById(id)
        return new BasicResponse(status: 200, data: response)
    }

    @Security(roles = [RoleTypeEnum.ADMIN, RoleTypeEnum.COMPANY_ADMIN, RoleTypeEnum.APP_ADMIN])
    @PatchMapping("/{id}")
    BasicResponse disable(@PathVariable("id") Long id) {
        def response = applicationService.disable(id)
        return new BasicResponse(status: 200, data: response)
    }

}
