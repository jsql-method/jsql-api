package pl.jsql.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.jsql.api.controller.generic.ValidateController;
import pl.jsql.api.dto.request.DeveloperAssignRequest;
import pl.jsql.api.dto.response.AppDeveloperApplicationResponse;
import pl.jsql.api.dto.response.BasicResponse;
import pl.jsql.api.dto.response.MessageResponse;
import pl.jsql.api.enums.RoleTypeEnum;
import pl.jsql.api.security.annotation.Security;
import pl.jsql.api.service.admin.AppDevAppsService;

import javax.validation.Valid;

@CrossOrigin
@RestController
@RequestMapping("/api/app-dev/application")
public class AppDevAppsController extends ValidateController {

    @Autowired
    private AppDevAppsService appDevAppsService;

    @Security(roles = {RoleTypeEnum.ADMIN, RoleTypeEnum.COMPANY_ADMIN, RoleTypeEnum.APP_ADMIN})
    @PostMapping
    public BasicResponse<MessageResponse> assign(@RequestBody @Valid DeveloperAssignRequest developerAssignRequest) {
        MessageResponse messageResponse = appDevAppsService.assign(developerAssignRequest);
        return new BasicResponse<>(200, messageResponse);
    }

    @Security
    @GetMapping("/{developerId}")
    public BasicResponse<AppDeveloperApplicationResponse> getByDeveloperId(@PathVariable("developerId") Long developerId) {
        AppDeveloperApplicationResponse response = appDevAppsService.getById(developerId);
        return new BasicResponse<>(200, response);
    }

    @Security(roles = {RoleTypeEnum.ADMIN, RoleTypeEnum.COMPANY_ADMIN, RoleTypeEnum.APP_ADMIN})
    @PostMapping("/unassign")
    public BasicResponse<MessageResponse> unassign(@RequestBody @Valid DeveloperAssignRequest memberAssignRequest) {
        MessageResponse messageResponse = appDevAppsService.unassign(memberAssignRequest);
        return new BasicResponse<>(200, messageResponse);
    }

}
