package pl.jsql.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.jsql.api.controller.generic.ValidateController;
import pl.jsql.api.dto.request.LoginRequest;
import pl.jsql.api.dto.request.UserRequest;
import pl.jsql.api.dto.response.BasicResponse;
import pl.jsql.api.dto.response.MessageResponse;
import pl.jsql.api.dto.response.SessionResponse;
import pl.jsql.api.security.annotation.Security;
import pl.jsql.api.service.AuthService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@CrossOrigin
@RestController
@RequestMapping("/api")
public class AuthorizationController extends ValidateController {

    @Autowired
    private AuthService authService;

    @Security(requireActiveSession = false)
    @PostMapping("/login")
    public BasicResponse<SessionResponse> login(@RequestBody @Valid LoginRequest loginRequest, HttpServletRequest request) {

        loginRequest.ipAddress = request.getRemoteAddr();
        SessionResponse response = authService.login(loginRequest);
        return new BasicResponse<>(200, response);
    }

    @Security
    @GetMapping("/session")
    public BasicResponse<SessionResponse> session() {

        SessionResponse response = authService.getSession();
        return new BasicResponse<>(200, response);
    }

    @Security
    @DeleteMapping("/logout")
    public BasicResponse<MessageResponse> logout() {
        MessageResponse response = authService.logout();
        return new BasicResponse<>(200, response);
    }

    @Security(requireActiveSession = false)
    @PostMapping("/register")
    public BasicResponse<MessageResponse> register(@RequestBody @Valid UserRequest userRequest) {
        MessageResponse response = authService.register(userRequest);
        return new BasicResponse<>(200, response);
    }

}
