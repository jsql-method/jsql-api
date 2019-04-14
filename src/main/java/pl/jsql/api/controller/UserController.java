package pl.jsql.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.jsql.api.controller.generic.ValidateController;
import pl.jsql.api.dto.request.*;
import pl.jsql.api.dto.response.BasicResponse;
import pl.jsql.api.dto.response.MessageResponse;
import pl.jsql.api.dto.response.UserResponse;
import pl.jsql.api.enums.RoleTypeEnum;
import pl.jsql.api.security.annotation.Security;
import pl.jsql.api.service.UserService;

import javax.validation.Valid;

@CrossOrigin
@RestController
@RequestMapping("/api/user")
public class UserController extends ValidateController {

    @Autowired
    private UserService userService;

    @Security
    @PatchMapping
    public BasicResponse<MessageResponse> update(@RequestBody @Valid UpdateUserRequest updateUserRequest) {
        MessageResponse response = userService.update(updateUserRequest);
        return new BasicResponse<>(200, response);
    }

    @Security(requireActiveSession = false)
    @GetMapping("/activate/{token}")
    public BasicResponse<MessageResponse> activate(@PathVariable("token") String token) {
        MessageResponse response = userService.activateAccount(token);
        return new BasicResponse<>(200, response);
    }

    @Security(requireActiveSession = false)
    @PostMapping("/forgot-password")
    public BasicResponse<MessageResponse> forgotPassword(@RequestBody @Valid ForgotPasswordRequest forgotPasswordRequest) {
        MessageResponse response = userService.forgotPassword(forgotPasswordRequest);
        return new BasicResponse<>(200, response);
    }

    @Security(requireActiveSession = false)
    @PostMapping("/reset-password/{token}")
    public BasicResponse<MessageResponse> resetPassword(@PathVariable("token") String token, @RequestBody @Valid ResetPasswordRequest resetPasswordRequest) {
        MessageResponse response = userService.resetPassword(token, resetPasswordRequest);
        return new BasicResponse<>(200, response);
    }

    @Security
    @PostMapping("/change-password")
    public BasicResponse changePassword(@RequestBody @Valid ChangePasswordRequest changePasswordRequest) {
        MessageResponse response = userService.changePassword(changePasswordRequest);
        return new BasicResponse<>(200, response);
    }

    @Security
    @GetMapping
    public BasicResponse<UserResponse> get() {
        UserResponse response = userService.getUser();
        return new BasicResponse<>(200, response);
    }

    @Security(role = RoleTypeEnum.COMPANY_ADMIN)
    @DeleteMapping
    public BasicResponse<MessageResponse> deactivate() {
        MessageResponse response = userService.disableCurrentAccount();
        return new BasicResponse<>(200, response);
    }

    @Security(roles = {RoleTypeEnum.ADMIN, RoleTypeEnum.COMPANY_ADMIN, RoleTypeEnum.APP_ADMIN})
    @DeleteMapping("/{id}")
    public BasicResponse<MessageResponse> deactivate(@PathVariable("id") Long id) {
        MessageResponse response = userService.disableDeveloperAccount(id);
        return new BasicResponse<>(200, response);
    }

}
