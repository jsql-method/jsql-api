package pl.jsql.api.controller.user

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pl.jsql.api.dto.UserRequest
import pl.jsql.api.enums.RoleTypeEnum
import pl.jsql.api.security.annotation.Security
import pl.jsql.api.service.UserService

import javax.servlet.http.HttpServletRequest

@CrossOrigin
@RestController
@RequestMapping("/api/user")
class UserController {

    @Autowired
    UserService userService

    @Security
    @PatchMapping("/{id}")
    def update(
            @PathVariable("id") Long id,
            @RequestBody UserRequest userRequest,
            @RequestHeader(value = "Session", required = false) String session
    ) {

        def response = userService.update(id, userRequest)

        return new ResponseEntity(response, HttpStatus.OK)
    }

    @Security(requireActiveSession = false)
    @GetMapping("/activate/{token}")
    def activate(
            @PathVariable("token") String token) {

        def response = userService.activateAccount(token)

        return new ResponseEntity(response, HttpStatus.OK)
    }

    @Security(requireActiveSession = false)
    @PostMapping("/activate")
    def reActivate(@RequestBody def token, HttpServletRequest request) {

        String origin = request.getHeader('origin')

        def response = userService.reActivate(token.token, origin)

        return new ResponseEntity(response, HttpStatus.OK)
    }

    @Security(requireActiveSession = false)
    @PostMapping("/forgot-password")
    def forgotPassword(
            @RequestBody def forgotPasswordRequest, HttpServletRequest request) {

        String origin = request.getHeader('origin')

        def response = userService.forgotPassword(forgotPasswordRequest.email, origin)

        return new ResponseEntity(response, HttpStatus.OK)
    }

    @Security(requireActiveSession = false)
    @PostMapping("/reset-password/{token}")
    def resetPassword(
            @PathVariable("token") String token,
            @RequestBody def resetPasswordRequest) {

        def response = userService.resetPassword(token, resetPasswordRequest.newPassword)

        return new ResponseEntity(response, HttpStatus.OK)
    }

    @Security
    @PostMapping("/change-password")
    def changePassword(
            @RequestBody def changePasswordRequest,
            @RequestHeader(value = "Session", required = false) String session) {

        def response = userService.changePassword(changePasswordRequest)

        return new ResponseEntity(response, HttpStatus.OK)
    }

    @Security
    @GetMapping
    def get(@RequestHeader(value = "Session", required = false) String session) {

        def response = userService.getUser()

        return new ResponseEntity(response, HttpStatus.OK)
    }

    @Security
    @DeleteMapping
    def deactivate(@RequestHeader(value = "Session", required = false) String session) {

        def response = userService.disableAccount()

        return new ResponseEntity(response, HttpStatus.OK)
    }

    @Security(roles = [RoleTypeEnum.ADMIN, RoleTypeEnum.COMPANY_ADMIN, RoleTypeEnum.APP_ADMIN])
    @DeleteMapping("/{id}")
    def deactivate(@PathVariable("id") Long id, @RequestHeader(value = "Session", required = false) String session) {

        def response = userService.disableAccount(id)

        return new ResponseEntity(response, HttpStatus.OK)
    }

}
