package pl.jsql.api.controller

import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import pl.jsql.api.dto.request.LoginRequest
import pl.jsql.api.enums.PlansEnum
import pl.jsql.api.misc.IntegrationTest
import pl.jsql.api.misc.TestUtils
import pl.jsql.api.model.hashing.Application
import pl.jsql.api.model.payment.Plan
import pl.jsql.api.repo.ApplicationDao
import pl.jsql.api.repo.PlansDao
import pl.jsql.api.repo.SessionDao
import pl.jsql.api.service.ApplicationService
import pl.jsql.api.service.AuthService

import static org.hamcrest.Matchers.*
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class ApplicationControllerTest extends IntegrationTest {
    @Autowired
    MockMvc mockMvc

    @Autowired
    AuthService loginService

    @Autowired
    ApplicationDao applicationDao

    @Autowired
    ApplicationService applicationService

    @Autowired
    TestUtils testUtils

    @Autowired
    SessionDao sessionDao

    @Autowired
    PlansDao plansDao

    private String session
    private Long id
    private Application application

    @BeforeAll
    void initiate() {
        session = loginAndGetSession()
        testUtils.createApplication("Test", sessionDao.findBySessionHash(session).user)
        application = applicationDao.findByNameAndCompany("Test", sessionDao.findBySessionHash(session).user.company)
        id = application.getId()

    }

    @DisplayName("when GET /api/application with valid session, then return application data")
    @Test
    void test() {
        mockMvc.perform(get("/api/application").header("session", session))
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.code', is(200)))
                .andExpect(jsonPath('$.data', notNullValue()))
    }

    @DisplayName("when GET /api/application with invalid session, then return error description")
    @Test
    void test1() {
        mockMvc.perform(get("/api/application").header("session", "session"))
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.code', is(401)))
                .andExpect(jsonPath('$.description', is("Unauthorized")))
    }

    @DisplayName("when GET /api/application/{id} with valid session, then return application data")
    @Test
    void test2() {
        mockMvc.perform(get("/api/application/" + id).header("session", session))
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.code', is(200)))
                .andExpect(jsonPath('$.data', notNullValue()))
    }

    @DisplayName("when GET /api/application/{id} with valid session but wrong id, then return error description")
    @Test
    void test3() {
        mockMvc.perform(get("/api/application/" + id + "1").header("session", session))
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.code', is(611)))
                .andExpect(jsonPath('$.description', is("No such application or member")))
    }

    @DisplayName("when POST /api/application with valid session , then new application has been added")
    @Test
    void test4() {
        mockMvc.perform(post("/api/application")
                .header("session", session)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"New Application\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.code', is(200)))
    }

    @DisplayName("when POST /api/application with invalid session, then return error description")
    @Test
    void test5() {
        mockMvc.perform(post("/api/application")
                .header("session", "session")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"New Application\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.code', is(401)))
                .andExpect(jsonPath('$.description', is("Unauthorized")))
    }

    @DisplayName("when POST /api/application without content type, then return error")
    @Test
    void test6() {
        mockMvc.perform(post("/api/application")
                .header("session", session)
                .content("{\"name\":\"New Application\"}"))
                .andExpect(status().is4xxClientError())
    }

    @DisplayName("when POST /api/application without request body, then return error")
    @Test
    void test7() {
        mockMvc.perform(post("/api/application")
                .header("session", session)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
    }

    @DisplayName("when PATCH /api/application with valid session, then return success code")
    @Test
    void test8() {

        mockMvc.perform(patch("/api/application/" + id)
                .header("session", session))
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.code', is(200)))
                .andExpect(jsonPath('$.data', nullValue()))
    }

    @DisplayName("when POST /api/application with invalid id, then return error")
    @Test
    void test9() {
        mockMvc.perform(patch("/api/application/" + id + "123")
                .header("session", session))
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.code', is(611)))
                .andExpect(jsonPath('$.description', is("No such application or member")))
    }

    private String loginAndGetSession() {
        LoginRequest loginRequest = new LoginRequest('test@test', 'test123', 'test@test')
        String session = loginService.login(loginRequest).data.sessionToken
        Plan plan = plansDao.findFirstByCompany(sessionDao.findBySessionHash(session).user.company)
        plan.plan = PlansEnum.LARGE
        plan.active = true
        plan.isTrial = false
        plansDao.save(plan)
        return session
    }
}
