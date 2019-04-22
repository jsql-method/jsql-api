package pl.jsql.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.jsql.api.controller.generic.ValidateController;
import pl.jsql.api.dto.request.AppAdminRequest;
import pl.jsql.api.dto.request.DemoteAppAdminRequest;
import pl.jsql.api.dto.request.UserRequest;
import pl.jsql.api.dto.response.AppAdminResponse;
import pl.jsql.api.dto.response.BasicResponse;
import pl.jsql.api.dto.response.MessageResponse;
import pl.jsql.api.enums.RoleTypeEnum;
import pl.jsql.api.security.annotation.Security;
import pl.jsql.api.service.admin.AppAdminService;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/app-admin")
public class AppAdminController extends ValidateController {

    @Autowired
    private AppAdminService appAdminService;

    @Security(roles = {RoleTypeEnum.ADMIN, RoleTypeEnum.COMPANY_ADMIN, RoleTypeEnum.APP_ADMIN})
    @GetMapping
    public BasicResponse<List<AppAdminResponse>> getAll() {
        List<AppAdminResponse> response = appAdminService.getAll();
        return new BasicResponse<>(200, response);
    }

    @Security(roles = {RoleTypeEnum.ADMIN, RoleTypeEnum.COMPANY_ADMIN})
    @PostMapping
    public BasicResponse<MessageResponse> create(@RequestBody @Valid AppAdminRequest appAdminRequest) {
        MessageResponse messageResponse = appAdminService.register(appAdminRequest);
        return new BasicResponse<>(200, messageResponse);
    }

    @Security(roles = {RoleTypeEnum.ADMIN, RoleTypeEnum.COMPANY_ADMIN})
    @PatchMapping
    public BasicResponse<MessageResponse> demote(@RequestBody @Valid DemoteAppAdminRequest demoteAppAdminRequest) {
        MessageResponse messageResponse = appAdminService.demote(demoteAppAdminRequest);
        return new BasicResponse<>(200, messageResponse);
    }

    @Security(roles = {RoleTypeEnum.ADMIN, RoleTypeEnum.COMPANY_ADMIN})
    @DeleteMapping("/{id}")
    public BasicResponse<MessageResponse> delete(@PathVariable("id") Long id) {
        MessageResponse response = appAdminService.delete(id);
        return new BasicResponse<>(200, response);
    }

}
