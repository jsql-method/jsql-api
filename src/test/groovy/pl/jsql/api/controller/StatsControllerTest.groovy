package pl.jsql.api.controller

import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.MockMvc
import pl.jsql.api.dto.LoginRequest
import pl.jsql.api.misc.IntegrationTest
import pl.jsql.api.service.AuthService

import static org.hamcrest.Matchers.hasKey
import static org.hamcrest.Matchers.is
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class StatsControllerTest extends IntegrationTest {

    @Autowired
    MockMvc mockMvc

    @Autowired
    AuthService loginService

    private String companyAdminSessionToken

    @BeforeAll
    void initiate() {
        companyAdminSessionToken = loginUserAndSaveSession()
    }

    //TODO tests!!!

    private String loginUserAndSaveSession() {
        LoginRequest loginRequest = new LoginRequest("test@test", 'test123', 'test@test')
        String session = loginService.login(loginRequest).data.sessionToken
        return session

    }
}