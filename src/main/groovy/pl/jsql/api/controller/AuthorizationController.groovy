package pl.jsql.api.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pl.jsql.api.controller.generic.ValidateController
import pl.jsql.api.dto.request.LoginRequest
import pl.jsql.api.dto.request.UserRequest
import pl.jsql.api.dto.response.BasicResponse
import pl.jsql.api.security.annotation.Security
import pl.jsql.api.service.AuthService
import pl.jsql.api.service.SessionService

import javax.servlet.http.HttpServletRequest
import javax.validation.Valid

import static pl.jsql.api.enums.HttpMessageEnum.ALREADY_AUTHORIZED
import static pl.jsql.api.enums.HttpMessageEnum.SUCCESS

@CrossOrigin
@RestController
@RequestMapping("/api")
class AuthorizationController extends ValidateController {

    @Autowired
    AuthService authService

    @Autowired
    SessionService sessionService

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
        sessionService.removeSession()
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
