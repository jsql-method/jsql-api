package pl.jsql.api.controller.user

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pl.jsql.api.controller.generic.ValidateController
import pl.jsql.api.dto.request.ActivateTokenRequest
import pl.jsql.api.dto.request.ChangePasswordRequest
import pl.jsql.api.dto.request.ForgotPasswordRequest
import pl.jsql.api.dto.request.ResetPasswordRequest
import pl.jsql.api.dto.request.UserRequest
import pl.jsql.api.dto.response.BasicResponse
import pl.jsql.api.enums.RoleTypeEnum
import pl.jsql.api.security.annotation.Security
import pl.jsql.api.service.UserService

import javax.servlet.http.HttpServletRequest
import javax.validation.Valid

@CrossOrigin
@RestController
@RequestMapping("/api/user")
class UserController extends ValidateController {

    @Autowired
    UserService userService

    @Security
    @PatchMapping("/{id}")
    BasicResponse update(@PathVariable("id") Long id, @RequestBody @Valid UserRequest userRequest) {
        def response = userService.update(id, userRequest)
        return new BasicResponse(status: 200, data: response)
    }

    @Security(requireActiveSession = false)
    @GetMapping("/activate/{token}")
    BasicResponse activate(@PathVariable("token") String token) {
        def response = userService.activateAccount(token)
        return new BasicResponse(status: 200, data: response)
    }

    @Security(requireActiveSession = false)
    @PostMapping("/activate")
    BasicResponse reActivate(@RequestBody @Valid ActivateTokenRequest token, HttpServletRequest request) {
        String origin = request.getHeader('origin')
        def response = userService.reActivate(token.token, origin)
        return new BasicResponse(status: 200, data: response)
    }

    @Security(requireActiveSession = false)
    @PostMapping("/forgot-password")
    BasicResponse forgotPassword(@RequestBody @Valid ForgotPasswordRequest forgotPasswordRequest, HttpServletRequest request) {
        String origin = request.getHeader('origin')
        def response = userService.forgotPassword(forgotPasswordRequest.email, origin)
        return new BasicResponse(status: 200, data: response)
    }

    @Security(requireActiveSession = false)
    @PostMapping("/reset-password/{token}")
    BasicResponse resetPassword(@PathVariable("token") String token, @RequestBody @Valid ResetPasswordRequest resetPasswordRequest) {
        def response = userService.resetPassword(token, resetPasswordRequest.newPassword)
        return new BasicResponse(status: 200, data: response)
    }

    @Security
    @PostMapping("/change-password")
    BasicResponse changePassword(@RequestBody @Valid ChangePasswordRequest changePasswordRequest) {
        def response = userService.changePassword(changePasswordRequest)
        return new BasicResponse(status: 200, data: response)
    }

    @Security
    @GetMapping
    BasicResponse get() {
        def response = userService.getUser()
        return new BasicResponse(status: 200, data: response)
    }

    @Security
    @DeleteMapping
    BasicResponse deactivate() {
        def response = userService.disableAccount()
        return new BasicResponse(status: 200, data: response)
    }

    @Security(roles = [RoleTypeEnum.ADMIN, RoleTypeEnum.COMPANY_ADMIN, RoleTypeEnum.APP_ADMIN])
    @DeleteMapping("/{id}")
    BasicResponse deactivate(@PathVariable("id") Long id) {
        def response = userService.disableAccount(id)
        return new BasicResponse(status: 200, data: response)
    }

}
