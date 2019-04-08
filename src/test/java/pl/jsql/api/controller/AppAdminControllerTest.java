package pl.jsql.api.controller

import org.apache.commons.lang3.RandomStringUtils
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
import pl.jsql.api.model.payment.Plan
import pl.jsql.api.model.user.Company
import pl.jsql.api.model.user.User
import pl.jsql.api.repo.CompanyDao
import pl.jsql.api.repo.PlansDao
import pl.jsql.api.repo.RoleDao
import pl.jsql.api.repo.SessionDao
import pl.jsql.api.repo.UserDao
import pl.jsql.api.service.AuthService
import pl.jsql.api.service.UserService

import static org.hamcrest.Matchers.*
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class AppAdminControllerTest extends IntegrationTest {

    @Autowired
    MockMvc mockMvc

    @Autowired
    AuthService authService

    @Autowired
    SessionDao sessionDao

    @Autowired
    UserService userService

    @Autowired
    CompanyDao companyDao

    @Autowired
    UserDao userDao

    @Autowired
    RoleDao roleDao

    @Autowired
    PlansDao plansDao

    private String companyAdminSessionToken
    private User companyAdmin
    private User anotherUser


    @BeforeAll
    void initiate() {
        companyAdminSessionToken = loginUserAndSaveSession()
        companyAdmin = sessionDao.findBySessionHash(companyAdminSessionToken).user
    }

    @DisplayName("when GET api/app-admin with valid session then should return success data")
    @Test
    void test() {
        mockMvc.perform(get(
                "/api/app-admin")
                .header("session", companyAdminSessionToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.code', is(200)))
                .andExpect(jsonPath('$', hasKey("data")))
    }

    @DisplayName("when GET api/app-admin with valid session as app dev then should return error data")
    @Test
    void test1() {
        mockMvc.perform(get(
                "/api/app-admin")
                .header("session", registerAndGetSession('test123@test123', RoleTypeEnum.APP_DEV)))
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.code', is(403)))
                .andExpect(jsonPath('$.description', is("Insufficient permissions")))
    }

    @DisplayName("when GET api/app-admin with invalid session then should return error data")
    @Test
    void test2() {
        mockMvc.perform(get(
                "/api/app-admin")
                .header("session", "session"))
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.code', is(401)))
                .andExpect(jsonPath('$.description', is("Unauthorized")))
    }

    @DisplayName("when POST /api/app-admin with valid data, then return success data")
    @Test
    void test3() {
        mockMvc.perform(post("/api/app-admin")
                .header("session", registerAndGetSession('test123@test123', RoleTypeEnum.COMPANY_ADMIN))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                "        \"email\": \"abcde@abcde\",\n" +
                "        \"firstName\": \"abdec\",\n" +
                "        \"lastName\":\"abdec\"\n" +
                "    }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.code', is(200)))
                .andExpect(jsonPath('$.description', is("Success")))
    }

    @DisplayName("when POST /api/app-admin with valid data as app dev, then return error data")
    @Test
    void test4() {
        mockMvc.perform(post("/api/app-admin")
                .header("session", registerAndGetSession('test123@test123', RoleTypeEnum.APP_DEV))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                "        \"email\": \"abc@abc\",\n" +
                "        \"firstName\": \"abc\",\n" +
                "        \"lastName\":\"abc\"\n" +
                "    }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.code', is(403)))
                .andExpect(jsonPath('$.description', is("Insufficient permissions")))
    }

    @DisplayName("when POST /api/app-admin with invalid session, then return error data")
    @Test
    void test5() {
        mockMvc.perform(post("/api/app-admin")
                .header("session", "session")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                "        \"email\": \"abc@abc\",\n" +
                "        \"firstName\": \"abc\",\n" +
                "        \"lastName\":\"abc\"\n" +
                "    }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.code', is(401)))
                .andExpect(jsonPath('$.description', is("Unauthorized")))
    }

    @DisplayName("when POST /api/app-admin with valid session and existing app dev email, then return error data")
    @Test
    void test6() {
        User u = sessionDao.findBySessionHash(registerAndGetSession('test123@test123', RoleTypeEnum.APP_ADMIN)).user
        mockMvc.perform(post("/api/app-admin")
                .header("session", companyAdminSessionToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                "        \"email\": \"" + u.email + "\",\n" +
                "        \"firstName\": \"abc\",\n" +
                "        \"lastName\":\"abc\"\n" +
                "    }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.code', is(619)))
                .andExpect(jsonPath('$.description', is("User with given role and email already exists")))
        anotherUser.role = roleDao.findByAuthority(RoleTypeEnum.COMPANY_ADMIN)
        userDao.save(anotherUser)
    }

    @DisplayName("when POST /api/app-admin with valid session and existing company admin email, then return error data")
    @Test
    void test7() {
        mockMvc.perform(post("/api/app-admin")
                .header("session", companyAdminSessionToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                "        \"email\": \"" + anotherUser.email + "\",\n" +
                "        \"firstName\": \"abc\",\n" +
                "        \"lastName\":\"abc\"\n" +
                "    }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.code', is(403)))
                .andExpect(jsonPath('$.description', is("Insufficient permissions")))
    }

    @DisplayName("when PATCH /api/app-admin with valid data, then return success data")
    @Test
    void test8() {
        User u = sessionDao.findBySessionHash(registerAndGetSession('test123@test123', RoleTypeEnum.APP_ADMIN)).user

        mockMvc.perform(patch("/api/app-admin")
                .header("session", companyAdminSessionToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                "        \"email\": \"" + u.email + "\",\n" +
                "        \"firstName\": \"abc\",\n" +
                "        \"lastName\":\"abc\"\n" +
                "    }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.code', is(200)))
                .andExpect(jsonPath('$.data', nullValue()))
        anotherUser.role = roleDao.findByAuthority(RoleTypeEnum.COMPANY_ADMIN)
        userDao.save(anotherUser)
    }

    @DisplayName("when PATCH /api/app-admin with invalid session, then return error data")
    @Test
    void test9() {
        mockMvc.perform(patch("/api/app-admin")
                .header("session", "session")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                "        \"email\": \"abc@abc\",\n" +
                "        \"firstName\": \"abc\",\n" +
                "        \"lastName\":\"abc\"\n" +
                "    }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.code', is(401)))
                .andExpect(jsonPath('$.description', is("Unauthorized")))
    }

    @DisplayName("when PATCH /api/app-admin with valid data as app dev, then return error data")
    @Test
    void test10() {
        mockMvc.perform(patch("/api/app-admin")
                .header("session", registerAndGetSession('test123@test123', RoleTypeEnum.APP_DEV))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                "        \"email\": \"abc@abc\",\n" +
                "        \"firstName\": \"abc\",\n" +
                "        \"lastName\":\"abc\"\n" +
                "    }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.code', is(403)))
                .andExpect(jsonPath('$.description', is("Insufficient permissions")))
        anotherUser.role = roleDao.findByAuthority(RoleTypeEnum.COMPANY_ADMIN)
        userDao.save(anotherUser)
    }

    @DisplayName("when PATCH /api/app-admin with valid data and no existing email, then return error data")
    @Test
    void test11() {
        mockMvc.perform(patch("/api/app-admin")
                .header("session", companyAdminSessionToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                "        \"email\": \"" + RandomStringUtils.randomAlphabetic(12) + "@abc\",\n" +
                "        \"firstName\": \"abc\",\n" +
                "        \"lastName\":\"abc\"\n" +
                "    }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.code', is(611)))
                .andExpect(jsonPath('$.description', is("No such application or member")))
    }


    private String registerAndGetSession(String email, RoleTypeEnum role) {
        UserRequest registerRequest = new UserRequest('test123', email, 'test@test', 'test@test')
        if (userDao.findByEmail(email) == null)
            authService.register(registerRequest)
        anotherUser = userService.userDao.findByEmail(email)
        anotherUser.activated = true
        anotherUser.blocked = false
        anotherUser.role = roleDao.findByAuthority(role)
        userService.userDao.save(anotherUser)
        Company company = anotherUser.company
        company.isLicensed = true
        companyDao.save(company)
        Plan plan = plansDao.findFirstByCompany(company)
        plan.plan = PlansEnum.LARGE
        plan.active = true
        plan.isTrial = false
        plansDao.save(plan)
        LoginRequest loginRequest = new LoginRequest(email, 'test123', 'test@test')
        String session = authService.login(loginRequest).data.sessionToken
        return session
    }

    private String loginUserAndSaveSession() {
        LoginRequest loginRequest = new LoginRequest("test@test", 'test123', 'test@test')
        String session = authService.login(loginRequest).data.sessionToken
        return session

    }


}

