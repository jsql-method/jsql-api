package pl.jsql.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.jsql.api.controller.generic.ValidateController;
import pl.jsql.api.dto.request.OptionsRequest;
import pl.jsql.api.dto.response.BasicResponse;
import pl.jsql.api.enums.RoleTypeEnum;
import pl.jsql.api.security.annotation.Security;
import pl.jsql.api.service.OptionsService;

import javax.validation.Valid;

@CrossOrigin
@RestController
@RequestMapping("/api/options")
public class  OptionsController extends ValidateController {

    @Autowired
   private OptionsService optionsService;

    @Security
    @GetMapping
    BasicResponse getAll() {
        def response = optionsService.getAll()
        return new BasicResponse<>(200, response);
    }

    @Security(roles = [RoleTypeEnum.ADMIN, RoleTypeEnum.COMPANY_ADMIN, RoleTypeEnum.APP_ADMIN])
    @GetMapping("/{id}")
    BasicResponse get(@PathVariable("id") Long id) {
        def response = optionsService.getByAppId(id)
        return new BasicResponse<>(200, response);
    }

    @Security(roles = [RoleTypeEnum.ADMIN, RoleTypeEnum.COMPANY_ADMIN, RoleTypeEnum.APP_ADMIN])
    @PatchMapping("/{id}")
    BasicResponse update(@PathVariable("id") Long id, @RequestBody @Valid OptionsRequest optionsRequest) {
        def response = optionsService.update(id, optionsRequest)
        return new BasicResponse<>(200, response);
    }

    @Security(roles = [RoleTypeEnum.ADMIN, RoleTypeEnum.COMPANY_ADMIN, RoleTypeEnum.APP_ADMIN])
    @GetMapping("/values")
    BasicResponse getOptionsValue() {
        def response = optionsService.getValues()
        return new BasicResponse<>(200, response);
    }

}
