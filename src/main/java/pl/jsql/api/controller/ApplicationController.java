package pl.jsql.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.jsql.api.controller.generic.ValidateController;
import pl.jsql.api.dto.request.ApplicationCreateRequest;
import pl.jsql.api.dto.response.ApplicationResponse;
import pl.jsql.api.dto.response.BasicResponse;
import pl.jsql.api.dto.response.MessageResponse;
import pl.jsql.api.enums.RoleTypeEnum;
import pl.jsql.api.security.annotation.Security;
import pl.jsql.api.service.ApplicationService;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/application")
public class ApplicationController extends ValidateController {

    @Autowired
    private ApplicationService applicationService;

    @Security
    @GetMapping
    public BasicResponse<List<ApplicationResponse>> getAll() {
        List<ApplicationResponse> response = applicationService.list();
        return new BasicResponse<>(200, response);
    }

    @Security(roles = {RoleTypeEnum.ADMIN, RoleTypeEnum.COMPANY_ADMIN, RoleTypeEnum.APP_ADMIN})
    @PostMapping
    public BasicResponse create(@RequestBody @Valid ApplicationCreateRequest applicationCreateRequest) {
        MessageResponse response = applicationService.create(applicationCreateRequest);
        return new BasicResponse<>(200, response);
    }

    @Security
    @GetMapping("/{id}")
    public BasicResponse<ApplicationResponse> get(@PathVariable("id") Long id) {
        ApplicationResponse response = applicationService.getById(id);
        return new BasicResponse<>(200, response);
    }

    @Security(roles = {RoleTypeEnum.ADMIN, RoleTypeEnum.COMPANY_ADMIN, RoleTypeEnum.APP_ADMIN})
    @PatchMapping("/{id}")
    public BasicResponse<MessageResponse> disable(@PathVariable("id") Long id) {
        MessageResponse response = applicationService.disableApplication(id);
        return new BasicResponse<>(200, response);
    }

}
