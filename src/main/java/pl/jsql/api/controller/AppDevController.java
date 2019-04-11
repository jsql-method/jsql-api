package pl.jsql.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.jsql.api.controller.generic.ValidateController;
import pl.jsql.api.dto.request.AppDeveloperRequest;
import pl.jsql.api.dto.response.AppDeveloperResponse;
import pl.jsql.api.dto.response.BasicResponse;
import pl.jsql.api.dto.response.MessageResponse;
import pl.jsql.api.enums.RoleTypeEnum;
import pl.jsql.api.security.annotation.Security;
import pl.jsql.api.service.admin.AppDevService;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/app-dev")
public class AppDevController extends ValidateController {

    @Autowired
    private AppDevService appDevService;

    @Security(roles = {RoleTypeEnum.ADMIN, RoleTypeEnum.COMPANY_ADMIN, RoleTypeEnum.APP_ADMIN})
    @GetMapping
    public BasicResponse<List<AppDeveloperResponse>> getAll() {
        List<AppDeveloperResponse> response = appDevService.list();
        return new BasicResponse<>(200, response);
    }

    @Security(roles = {RoleTypeEnum.ADMIN, RoleTypeEnum.COMPANY_ADMIN, RoleTypeEnum.APP_ADMIN})
    @PostMapping
    BasicResponse<MessageResponse> create(@RequestBody @Valid AppDeveloperRequest appDeveloperRequest) {
        MessageResponse response = appDevService.register(appDeveloperRequest);
        return new BasicResponse<>(200, response);
    }

    @Security(roles = {RoleTypeEnum.ADMIN, RoleTypeEnum.COMPANY_ADMIN, RoleTypeEnum.APP_ADMIN})
    @DeleteMapping("/{id}")
    BasicResponse<MessageResponse> delete(@PathVariable("id") Long id) {
        MessageResponse response = appDevService.delete(id);
        return new BasicResponse<>(200, response);
    }
}
