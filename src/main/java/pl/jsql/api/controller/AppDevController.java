package pl.jsql.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.jsql.api.controller.generic.ValidateController;
import pl.jsql.api.dto.request.UserRequest;
import pl.jsql.api.dto.response.BasicResponse;
import pl.jsql.api.enums.RoleTypeEnum;
import pl.jsql.api.security.annotation.Security;
import pl.jsql.api.service.admin.AppDevService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@CrossOrigin
@RestController
@RequestMapping("/api/app-dev")
public class  AppDevController extends ValidateController {

    @Autowired
    private AppDevService appDevService;

    @Security(roles = {RoleTypeEnum.ADMIN, RoleTypeEnum.COMPANY_ADMIN, RoleTypeEnum.APP_ADMIN})
    @GetMapping
    public BasicResponse<MemberListResponse> getAll() {
        MemberListResponse response = appDevService.getAll();
        return new BasicResponse<>(200, response);
    }

    @Security(roles = {RoleTypeEnum.ADMIN, RoleTypeEnum.COMPANY_ADMIN, RoleTypeEnum.APP_ADMIN})
    @PostMapping
    BasicResponse<MemberCreateResponse> create(
            @RequestBody @Valid UserRequest userRequest,
            HttpServletRequest request) {
        userRequest.origin = request.getHeader("origin");
        MemberListResponse response = appDevService.register(userRequest);
        return new BasicResponse<>(200, response);
    }

    @Security(roles = {RoleTypeEnum.ADMIN, RoleTypeEnum.COMPANY_ADMIN, RoleTypeEnum.APP_ADMIN})
    @DeleteMapping("/{id}")
    BasicResponse<MemberDeleteResponse> delete(@PathVariable("id") Long id) {
        MemberDeleteResponse response = appDevService.delete(id);
        return new BasicResponse<>(200, response);
    }
}
