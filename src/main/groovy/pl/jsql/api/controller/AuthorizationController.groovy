package pl.jsql.api.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import pl.jsql.api.controller.generic.ValidateController
import pl.jsql.api.dto.request.LoginRequest
import pl.jsql.api.dto.request.UserRequest
import pl.jsql.api.dto.response.BasicResponse
import pl.jsql.api.security.annotation.Security
import pl.jsql.api.service.AuthService

import javax.servlet.http.HttpServletRequest
import javax.validation.Valid

@CrossOrigin
@RestController
@RequestMapping("/api")
class AuthorizationController extends ValidateController {

    @Autowired
    AuthService authService

    @Security(requireActiveSession = false)
    @PostMapping("/login")
    BasicResponse login(@RequestBody @Valid LoginRequest loginRequest, HttpServletRequest request) {
        loginRequest.ipAddress = request.getRemoteAddr()
        def response = authService.login(loginRequest)
        response['origin'] = request.getHeader('origin')
        return new BasicResponse(status: 200, data: response)
    }

    @Security
    @DeleteMapping("/logout")
    BasicResponse logout() {
        authService.logout()
        return new BasicResponse(status: 200, data: null)
    }

    @Security(requireActiveSession = false)
    @PostMapping("/register")
    BasicResponse register(@RequestBody @Valid UserRequest userRequest, HttpServletRequest request) {
        userRequest.origin = request.getHeader('origin')
        def response = authService.register(userRequest)
        return new BasicResponse(status: 200, data: response)
    }


}
