package pl.jsql.api.controller

import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import pl.jsql.api.dto.request.LoginRequest
import pl.jsql.api.dto.request.UserRequest
import pl.jsql.api.enums.PlansEnum
import pl.jsql.api.enums.RoleTypeEnum
import pl.jsql.api.misc.IntegrationTest
import pl.jsql.api.model.hashing.Application
import pl.jsql.api.model.payment.Plans
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

class AppDevControllerTest extends IntegrationTest {

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

    @Autowired
    PlansDao plansDao

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

    @DisplayName("when GET api/app-dev with valid session then should return success data")
    @Test
    void test() {
        mockMvc.perform(get(
                "/api/app-dev").header("session", companyAdminSessionToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.code', is(200)))
                .andExpect(jsonPath('$.data', notNullValue()))
    }

    @DisplayName("when GET api/app-dev with invalid session then should return error data")
    @Test
    void test1() {
        mockMvc.perform(get(
                "/api/app-dev").header("session", "session"))
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.code', is(401)))
                .andExpect(jsonPath('$.description', is("Unauthorized")))
    }

    @DisplayName("when GET api/app-dev with valid app dev session then should return error data")
    @Test
    void test2() {
        mockMvc.perform(get(
                "/api/app-dev").header("session", appDevSessionToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.code', is(403)))
                .andExpect(jsonPath('$.description', is("Insufficient permissions")))
    }

    @DisplayName("when POST api/app-dev with valid session as company admin then should return success data")
    @Test
    void test3() {
        mockMvc.perform(post(
                "/api/app-dev").header("session", companyAdminSessionToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                "        \"email\":\"testing@appdev\",\n" +
                "        \"password\": \"test123\",\n" +
                "        \"firstName\": \"App\",\n" +
                "        \"lastName\": \"Dev\"\n" +
                "    }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.code', is(200)))
                .andExpect(jsonPath('$.data', nullValue()))
    }

    @DisplayName("when POST api/app-dev with valid session as app dev then should return error data")
    @Test
    void test4() {
        mockMvc.perform(post(
                "/api/app-dev").header("session", appDevSessionToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                "        \"email\":\"testing@appdev\",\n" +
                "        \"password\": \"test123\",\n" +
                "        \"firstName\": \"App\",\n" +
                "        \"lastName\": \"Dev\"\n" +
                "    }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.code', is(403)))
                .andExpect(jsonPath('$.description', is("Insufficient permissions")))
    }

    @DisplayName("when POST api/app-dev with invalid session then should return error data")
    @Test
    void test5() {
        mockMvc.perform(post(
                "/api/app-dev").header("session", "session")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                "        \"email\":\"testing@appdev\",\n" +
                "        \"password\": \"test123\",\n" +
                "        \"firstName\": \"App\",\n" +
                "        \"lastName\": \"Dev\"\n" +
                "    }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.code', is(401)))
                .andExpect(jsonPath('$.description', is("Unauthorized")))
    }

    @DisplayName("when DELETE api/app-dev/{id} with valid session as company admin then should return success data")
    @Test
    void test6() {
        mockMvc.perform(delete(
                "/api/app-dev/" + appDev.id).header("session", companyAdminSessionToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.code', is(200)))
                .andExpect(jsonPath('$.data', nullValue()))
    }

    @DisplayName("when DELETE api/app-dev/{id} with valid session as app dev then should return error data")
    @Test
    void test7() {
        mockMvc.perform(delete(
                "/api/app-dev/" + companyAdmin.id).header("session", appDevSessionToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.code', is(403)))
                .andExpect(jsonPath('$.description', is("Insufficient permissions")))
    }

    @DisplayName("when DELETE api/app-dev/{id} with invalid session then should return error data")
    @Test
    void test8() {
        mockMvc.perform(delete(
                "/api/app-dev/" + appDev.id).header("session", "session")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.code', is(401)))
                .andExpect(jsonPath('$.description', is("Unauthorized")))
    }

    private String loginUserAndSaveSession() {
        LoginRequest loginRequest = new LoginRequest("test@test", 'test123', 'test@test')
        String session = authService.login(loginRequest).data.sessionToken

        //Make sure that given account have role company admin
        companyAdmin = sessionDao.findBySessionHash(session).user
        companyAdmin.role = roleDao.findByAuthority(RoleTypeEnum.COMPANY_ADMIN)
        userDao.save(companyAdmin)

        Plans plan = plansDao.findFirstByCompany(companyAdmin.company)
        plan.plan = PlansEnum.LARGE
        plan.active = true
        plan.isTrial = false
        plansDao.save(plan)

        return session

    }

    private String registerMemberAndGetSession() {
        //register member
        UserRequest userRequest = new UserRequest("test123", 'app@dev', 'test', 'test')
        userRequest.company = companyAdmin.company.id

        userRequest.role = RoleTypeEnum.APP_DEV.toString()

        authService.register(userRequest)
        //activate members account
        appDev = userDao.findByEmail("app@dev")
        appDev.activated = true
        userDao.save(appDev)

        applicationService.assignUserToAppMember(appDev, application)

        LoginRequest loginRequest = new LoginRequest("app@dev", 'test123', 'test@test')
        String session = authService.login(loginRequest).data.sessionToken
        return session
    }


}

