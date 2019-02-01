package pl.jsql.api.controller

import org.apache.commons.lang3.RandomStringUtils
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import pl.jsql.api.dto.LoginRequest
import pl.jsql.api.dto.MemberAssignRequest
import pl.jsql.api.dto.UserRequest
import pl.jsql.api.enums.RoleTypeEnum
import pl.jsql.api.misc.IntegrationTest
import pl.jsql.api.model.hashing.Application
import pl.jsql.api.model.user.User
import pl.jsql.api.repo.*
import pl.jsql.api.service.ApplicationService
import pl.jsql.api.service.AuthService
import pl.jsql.api.service.AppDevService
import pl.jsql.api.service.UserService

import static org.hamcrest.Matchers.*
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class AppDevAppsControllerTest extends IntegrationTest {

    @Autowired
    MockMvc mockMvc

    @Autowired
    AuthService authService

    @Autowired
    AppDevService memberService

    @Autowired
    SessionDao sessionDao

    @Autowired
    UserDao userDao

    @Autowired
    RoleDao roleDao

    @Autowired
    ApplicationDao applicationDao

    @Autowired
    MemberKeyDao memberKeyDao

    @Autowired
    UserService userService

    @Autowired
    ApplicationService applicationService

    private String companyAdminSessionToken
    private User companyAdmin
    private String appDevSessionToken
    private User appDev
    private Application application


    @BeforeAll
    void initiate() {
        companyAdminSessionToken = loginUserAndSaveSession()
        appDevSessionToken = registerMemberAndGetSession()
        application = applicationDao.findByUserQuery(companyAdmin).get(0)
    }

    @DisplayName("when POST api/app-dev/application with valid session as company admin then should return success data")
    @Test
    void test() {
        Application exampleApp = applicationDao.findByUserQuery(companyAdmin).get(0)
        MemberAssignRequest memberAssignRequest = new MemberAssignRequest(appDev.id, exampleApp.id)
        mockMvc.perform(post(
                "/api/app-dev/application").header("session", companyAdminSessionToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"member\": " + appDev.id + ", \"application\": " + exampleApp.id + "}"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath('$.code', is(200)))
                .andExpect(jsonPath('$.data', nullValue()))
    }

    @DisplayName("when POST api/app-dev/application with invalid session then should return error data")
    @Test
    void test1() {
        Application exampleApp = applicationDao.findByUserQuery(companyAdmin).get(0)
        MemberAssignRequest memberAssignRequest = new MemberAssignRequest(appDev.id, exampleApp.id)
        mockMvc.perform(post(
                "/api/app-dev/application").header("session", "session")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"member\": " + appDev.id + ", \"application\": " + exampleApp.id + "}"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath('$.code', is(401)))
                .andExpect(MockMvcResultMatchers.jsonPath('$.description', is("Unauthorized")))
    }

    @DisplayName("when POST api/app-dev/application with valid app dev session then should return error data")
    @Test
    void test2() {
        Application exampleApp = applicationDao.findByUserQuery(companyAdmin).get(0)
        MemberAssignRequest memberAssignRequest = new MemberAssignRequest(appDev.id, exampleApp.id)
        mockMvc.perform(post(
                "/api/app-dev/application").header("session", appDevSessionToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"member\": " + appDev.id + ", \"application\": " + exampleApp.id + "}"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath('$.code', is(403)))
                .andExpect(MockMvcResultMatchers.jsonPath('$.description', is("Insufficient permissions")))
    }

    @DisplayName("when POST api/app-dev/application with valid company admin session with bad request then should return error data")
    @Test
    void test3() {
        Application exampleApp = applicationDao.findByUserQuery(companyAdmin).get(0)
        mockMvc.perform(post(
                "/api/app-dev/application").header("session", companyAdminSessionToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"member\": " + appDev.id + "}"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath('$.code', is(400)))
                .andExpect(MockMvcResultMatchers.jsonPath('$.description', is("Bad request")))
    }

    @DisplayName("when POST api/app-dev/application with valid company admin session with invalid app id then should return error data")
    @Test
    void test4() {
        Application exampleApp = applicationDao.findByUserQuery(companyAdmin).get(0)
        mockMvc.perform(post(
                "/api/app-dev/application").header("session", companyAdminSessionToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"member\": " + appDev.id + ", \"application\": 99999999}"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath('$.code', is(611)))
                .andExpect(MockMvcResultMatchers.jsonPath('$.description', is("No such application or member")))
    }

    @DisplayName("when POST api/app-dev/application with valid company admin session with invalid member id then should return error data")
    @Test
    void test5() {
        Application exampleApp = applicationDao.findByUserQuery(companyAdmin).get(0)
        mockMvc.perform(post(
                "/api/app-dev/application").header("session", companyAdminSessionToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"member\": 99999999, \"application\": " + exampleApp.id + "}"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath('$.code', is(611)))
                .andExpect(MockMvcResultMatchers.jsonPath('$.description', is("No such application or member")))
    }

    @DisplayName("when GET api/app-dev/application/{id} with valid session as company admin then should return success data")
    @Test
    void test6() {
        appDevSessionToken = registerMemberAndGetSession()
        mockMvc.perform(get(
                "/api/app-dev/application/" + appDev.id).header("session", companyAdminSessionToken))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath('$.code', is(200)))
                .andExpect(jsonPath('$.data', hasKey("userEmail")))
                .andExpect(jsonPath('$.data', hasKey("applicationId")))
    }

    @DisplayName("when GET api/app-dev/application/{id} with invalid session as company admin then should return error data")
    @Test
    void test7() {
        mockMvc.perform(get(
                "/api/app-dev/application/" + appDev.id).header("session", "session"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath('$.code', is(401)))
                .andExpect(MockMvcResultMatchers.jsonPath('$.description', is("Unauthorized")))
    }

    @DisplayName("when GET api/app-dev/application/{id} with valid session and invalid id then should return error data")
    @Test
    void test8() {
        mockMvc.perform(get(

                "/api/app-dev/application/" + RandomStringUtils.randomNumeric(15))
                .header("session", companyAdminSessionToken))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath('$.code', is(606)))
                .andExpect(MockMvcResultMatchers.jsonPath('$.description', is("Account with given data not exist")))
    }

    @DisplayName("when DELETE api/app-dev/application/unassign with valid session then should return success data")
    @Test
    void test9() {
        mockMvc.perform(delete(
                "/api/app-dev/application/unassign")
                .header("session", companyAdminSessionToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                "            \"member\": " + appDev.id + ",\n" +
                "            \"application\": " + application.id + "\n" +
                "        }"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath('$.code', is(200)))
                .andExpect(jsonPath('$.data', nullValue()))
    }

    @DisplayName("when DELETE api/app-dev/application/unassign with invalid session then should return error data")
    @Test
    void test10() {
        mockMvc.perform(delete(
                "/api/app-dev/application/unassign")
                .header("session", "session")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                "            \"member\": " + appDev.id + ",\n" +
                "            \"application\": " + application.id + "\n" +
                "        }"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath('$.code', is(401)))
                .andExpect(MockMvcResultMatchers.jsonPath('$.description', is("Unauthorized")))
    }

    @DisplayName("when DELETE api/app-dev/application/unassign with valid session and invalid app id then should return error data")
    @Test
    void test11() {
        companyAdminSessionToken = loginUserAndSaveSession()
        mockMvc.perform(delete(
                "/api/app-dev/application/unassign")
                .header("session", companyAdminSessionToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                "            \"member\": " + appDev.id + ",\n" +
                "            \"application\": " + RandomStringUtils.randomNumeric(12) + "\n" +
                "        }"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath('$.code', is(611)))
                .andExpect(MockMvcResultMatchers.jsonPath('$.description', is("No such application or member")))
    }

    @DisplayName("when DELETE api/app-dev/application/unassign with valid session and invalid member id then should return error data")
    @Test
    void test12() {
        mockMvc.perform(delete(
                "/api/app-dev/application/unassign")
                .header("session", loginUserAndSaveSession())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                "            \"member\": " + RandomStringUtils.randomNumeric(5) + ",\n" +
                "            \"application\": " + application.id + "\n" +
                "        }"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath('$.code', is(611)))
                .andExpect(MockMvcResultMatchers.jsonPath('$.description', is("No such application or member")))
    }

    private String loginUserAndSaveSession() {
        LoginRequest loginRequest = new LoginRequest("test@test", 'test123', 'test@test')
        String session = authService.login(loginRequest).data.sessionToken

        //Make sure that given account have role company admin
        companyAdmin = sessionDao.findBySessionHash(session).user
        companyAdmin.role = roleDao.findByAuthority(RoleTypeEnum.COMPANY_ADMIN)
        userDao.save(companyAdmin)

        return session

    }

    private String registerMemberAndGetSession() {
        //register member
        UserRequest userRequest = new UserRequest("test123", 'app@dev', 'test', 'test')

        userRequest.company = companyAdmin.company.id

        userRequest.role = RoleTypeEnum.APP_DEV.toString()

        authService.register(userRequest)

        appDev = userDao.findByEmail("app@dev")
        appDev.activated = true
        userDao.save(appDev)

        applicationService.assignUserToAppMember(appDev, application)

        LoginRequest loginRequest = new LoginRequest("app@dev", 'test123', 'test@test')
        String session = authService.login(loginRequest).data.sessionToken
        return session
    }


}

