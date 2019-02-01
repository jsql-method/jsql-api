package pl.jsql.api.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pl.jsql.api.dto.LoginRequest
import pl.jsql.api.dto.UserRequest
import pl.jsql.api.security.annotation.Security
import pl.jsql.api.service.AuthService
import pl.jsql.api.service.SessionService

import javax.servlet.http.HttpServletRequest

import static pl.jsql.api.enums.HttpMessageEnum.ALREADY_AUTHORIZED
import static pl.jsql.api.enums.HttpMessageEnum.SUCCESS

@CrossOrigin
@RestController
@RequestMapping("/api")
class AuthorizationController {

    @Autowired
    AuthService authService

    @Autowired
    SessionService sessionService


    @Security(requireActiveSession = false)
    @PostMapping("/login")
    def login(
            @RequestBody LoginRequest loginRequest,
            @RequestHeader(value = "Session", required = false) String session, HttpServletRequest request) {

        if (sessionService.isLogged(session)) {
            return new ResponseEntity([code: ALREADY_AUTHORIZED.getCode(), description: ALREADY_AUTHORIZED.getDescription()], HttpStatus.OK)
        }

        loginRequest.ipAddress = request.getRemoteAddr()

        def response = authService.login(loginRequest)

        response['origin'] = request.getHeader('origin')

        return new ResponseEntity(response, HttpStatus.OK)
    }

    @Security
    @DeleteMapping("/logout")
    def logout(@RequestHeader(value = "Session", required = false) String session) {

        sessionService.removeSession(session)

        return new ResponseEntity([code: SUCCESS.getCode(), data: null], HttpStatus.OK)
    }

    @Security(requireActiveSession = false)
    @PostMapping("/register")
    def register(
            @RequestBody UserRequest userRequest,
            @RequestHeader(value = "Session", required = false) String session, HttpServletRequest request) {

        if (sessionService.isLogged(session)) {
            return new ResponseEntity([code: ALREADY_AUTHORIZED.getCode(), description: ALREADY_AUTHORIZED.getDescription()], HttpStatus.OK)
        }

        userRequest.origin = request.getHeader('origin')
        def response = authService.register(userRequest)

        return new ResponseEntity(response, HttpStatus.OK)
    }


}
