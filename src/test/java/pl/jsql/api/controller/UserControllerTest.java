package pl.jsql.api.controller;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pl.jsql.api.dto.request.LoginRequest;
import pl.jsql.api.dto.request.UserRequest;
import pl.jsql.api.enums.RoleTypeEnum;
import pl.jsql.api.misc.IntegrationTest;
import pl.jsql.api.model.user.Company;
import pl.jsql.api.model.user.User;
import pl.jsql.api.repo.CompanyDao;
import pl.jsql.api.repo.RoleDao;
import pl.jsql.api.repo.SessionDao;
import pl.jsql.api.repo.UserDao;
import pl.jsql.api.service.AuthService;
import pl.jsql.api.service.UserService;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserControllerTest extends IntegrationTest {

//    @Autowired
//    MockMvc mockMvc
//
//    @Autowired
//    AuthService authService
//
//    @Autowired
//    SessionDao sessionDao
//
//    @Autowired
//    UserService userService
//
//    @Autowired
//    CompanyDao companyDao
//
//    @Autowired
//    UserDao userDao
//
//    @Autowired
//    RoleDao roleDao
//
//    private String companyAdminSessionToken
//    private User companyAdmin
//    private User anotherUser
//    private String anotherUserSessionToken
//
//
//    @BeforeAll
//    void initiate() {
//        companyAdminSessionToken = loginUserAndSaveSession()
//        companyAdmin = sessionDao.findBySessionHash(companyAdminSessionToken).user
//    }
//
//    @DisplayName("when PATCH /api/user/{id} with valid session and id, then return success data")
//    @Test
//    void test() {
//        mockMvc.perform(patch("/api/user/" + companyAdmin.id)
//                .header("session", companyAdminSessionToken)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content("{\"firstName\":\"Jane\",\"lastName\":\"Smith\"}"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath('$.code', is(200)))
//                .andExpect(jsonPath('$.description', is("Success")))
//    }
//
//    @DisplayName("when PATCH /api/user/{id} with invalid session and valid id, then return error data")
//    @Test
//    void test1() {
//        mockMvc.perform(patch("/api/user/" + companyAdmin.id)
//                .header("session", "session")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content("{\"firstName\":\"Jane\",\"lastName\":\"Smith\"}"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath('$.code', is(401)))
//                .andExpect(jsonPath('$.description', is("Unauthorized")))
//    }
//
//    @DisplayName("when PATCH /api/user/{id} with valid session and invalid id, then return error data")
//    @Test
//    void test2() {
//        mockMvc.perform(patch("/api/user/" + RandomStringUtils.randomNumeric(12))
//                .header("session", companyAdminSessionToken)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content("{\"firstName\":\"Jane\",\"lastName\":\"Smith\"}"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath('$.code', is(606)))
//                .andExpect(jsonPath('$.description', is("Account with given data not exist")))
//    }
//
//    @DisplayName("when GET /api/user/activate/{token} with valid token, then return success")
//    @Test
//    void test3() {
//        companyAdmin.activationDate = new Date()
//        userDao.save(companyAdmin)
//        mockMvc.perform(get("/api/user/activate/" + companyAdmin.activationToken))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath('$.code', is(200)))
//                .andExpect(jsonPath('$.data', is("Activated successfully. Now sign in.")))
//    }
//
//    @DisplayName("when GET /api/user/activate/{token} with valid token and old date, then return error data")
//    @Test
//    void test4() {
//        companyAdmin.activationDate = new Date(117, 01, 01)
//        userDao.save(companyAdmin)
//        mockMvc.perform(get("/api/user/activate/" + companyAdmin.activationToken))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath('$.code', is(616)))
//                .andExpect(jsonPath('$.data', is("Activation URL has expired")))
//    }
//
//    @DisplayName("when GET /api/user/activate/{token} with invalid token, then return error data")
//    @Test
//    void test5() {
//        companyAdmin.activationDate = new Date()
//        userDao.save(companyAdmin)
//        mockMvc.perform(get("/api/user/activate/" + RandomStringUtils.randomAlphanumeric(6)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath('$.code', is(601)))
//                .andExpect(jsonPath('$.data', is("User with that token does not exist")))
//    }
//
//    @DisplayName("when POST /api/user/activate with valid token, then return success data")
//    @Test
//    void test6() {
//        mockMvc.perform(post("/api/user/activate")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content("{\"token\": \"" + companyAdmin.activationToken + "\"}"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath('$.code', is(200)))
//                .andExpect(jsonPath('$.data', nullValue()))
//    }
//
//    @DisplayName("when POST /api/user/activate with invalid token, then return error data")
//    @Test
//    void test7() {
//        mockMvc.perform(post("/api/user/activate")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content("{\"token\": \"" + RandomStringUtils.randomAlphanumeric(6) + "\"}"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath('$.code', is(601)))
//                .andExpect(jsonPath('$.data', is("User with that token does not exist")))
//    }
//
//    @DisplayName("when POST /api/user/forgot-password with valid data, then return success data")
//    @Test
//    void test8() {
//        User u = sessionDao.findBySessionHash(registerAndGetSession('test123@test123', RoleTypeEnum.COMPANY_ADMIN)).user
//        mockMvc.perform(post("/api/user/forgot-password")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content("{\"email\": \"" + u.email + "\"}"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath('$.code', is(200)))
//                .andExpect(jsonPath('$.description', is("Check your inbox and follow the instruction")))
//    }
//
//    @DisplayName("when POST /api/user/forgot-password with invalid data, then return error data")
//    @Test
//    void test9() {
//        mockMvc.perform(post("/api/user/forgot-password")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content("{\"email\": \"" + RandomStringUtils.randomAlphanumeric(6) + "\"}"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath('$.code', is(615)))
//                .andExpect(jsonPath('$.description', is("User with given email not found")))
//    }
//
//    @DisplayName("when POST /api/user/reset-password with valid data, then return success data")
//    @Test
//    void test10() {
//        anotherUser = sessionDao.findBySessionHash(registerAndGetSession("test7656@645", RoleTypeEnum.COMPANY_ADMIN)).user
//        anotherUser.forgotToken = "1234"
//        anotherUser.changePasswordDate = new Date()
//        userDao.save(anotherUser)
//        mockMvc.perform(post("/api/user/reset-password/" + anotherUser.forgotToken)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content("{\"newPassword\": \"" + RandomStringUtils.randomAlphanumeric(6) + "\"}"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath('$.code', is(200)))
//                .andExpect(jsonPath('$.description', is("Password changed. Now sign in using new password.")))
//        anotherUser.password = DigestUtils.sha256Hex("test123")
//        userDao.save(anotherUser)
//    }
//
//    @DisplayName("when POST /api/user/reset-password with invalid token, then return error data")
//    @Test
//    void test11() {
//        mockMvc.perform(post("/api/user/reset-password/" + RandomStringUtils.randomAlphanumeric(6))
//                .contentType(MediaType.APPLICATION_JSON)
//                .content("{\"newPassword\": \"" + RandomStringUtils.randomAlphanumeric(6) + "\"}"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath('$.code', is(606)))
//                .andExpect(jsonPath('$.description', is("Account with given data not exist")))
//    }
//
//    @DisplayName("when POST /api/user/reset-password with old date, then return error data")
//    @Test
//    void test12() {
//        anotherUser = sessionDao.findBySessionHash(registerAndGetSession("test7656@645", RoleTypeEnum.COMPANY_ADMIN)).user
//        anotherUser.forgotToken = "1234"
//        anotherUser.changePasswordDate = new Date(117, 01, 01)
//        userDao.save(anotherUser)
//        mockMvc.perform(post("/api/user/reset-password/" + anotherUser.forgotToken)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content("{\"newPassword\": \"" + RandomStringUtils.randomAlphanumeric(6) + "\"}"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath('$.code', is(617)))
//                .andExpect(jsonPath('$.description', is("Reset password URL has expired")))
//        anotherUser.password = DigestUtils.sha256Hex("test123")
//        userDao.save(anotherUser)
//    }
//
//    @DisplayName("when POST /api/user/change-password with valid data, then return success data")
//    @Test
//    void test13() {
//        anotherUser = sessionDao.findBySessionHash(registerAndGetSession("test7656@645", RoleTypeEnum.COMPANY_ADMIN)).user
//        mockMvc.perform(post("/api/user/change-password")
//                .header("session", registerAndGetSession('test123@test123', RoleTypeEnum.COMPANY_ADMIN))
//                .contentType(MediaType.APPLICATION_JSON)
//                .content("{\"oldPassword\":\"test123\",\"newPassword\": \"" + RandomStringUtils.randomAlphanumeric(6) + "\"}"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath('$.code', is(200)))
//                .andExpect(jsonPath('$.description', is("Password changed successfully.")))
//        anotherUser.password = DigestUtils.sha256Hex("test123")
//        userDao.save(anotherUser)
//    }
//
//    @DisplayName("when POST /api/user/change-password with invalid session, then return error data")
//    @Test
//    void test14() {
//        mockMvc.perform(post("/api/user/change-password")
//                .header("session", "session")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content("{\"oldPassword\":\"test123\",\"newPassword\": \"" + RandomStringUtils.randomAlphanumeric(6) + "\"}"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath('$.code', is(401)))
//                .andExpect(jsonPath('$.description', is("Unauthorized")))
//    }
//
//    @DisplayName("when POST /api/user/change-password with invalid old password, then return error data")
//    @Test
//    void test15() {
//        mockMvc.perform(post("/api/user/change-password")
//                .header("session", registerAndGetSession('test123@test123', RoleTypeEnum.COMPANY_ADMIN))
//                .contentType(MediaType.APPLICATION_JSON)
//                .content("{\"oldPassword\":\"" + RandomStringUtils.randomAlphanumeric(6) + "\",\"newPassword\": \"" + RandomStringUtils.randomAlphanumeric(6) + "\"}"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath('$.code', is(618)))
//                .andExpect(jsonPath('$.description', is("Old password is incorrect")))
//    }
//
//    @DisplayName("when GET /api/user with valid session, then return success data")
//    @Test
//    void test16() {
//        mockMvc.perform(get("/api/user")
//                .header("session", companyAdminSessionToken))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath('$.code', is(200)))
//                .andExpect(jsonPath('$.data', hasKey("id")))
//                .andExpect(jsonPath('$.data', hasKey("email")))
//                .andExpect(jsonPath('$.data', hasKey("firstName")))
//                .andExpect(jsonPath('$.data', hasKey("lastName")))
//    }
//
//    @DisplayName("when GET /api/user with invalid session, then return error data")
//    @Test
//    void test17() {
//        mockMvc.perform(get("/api/user")
//                .header("session", "session"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath('$.code', is(401)))
//                .andExpect(jsonPath('$.description', is("Unauthorized")))
//    }
//
//    @DisplayName("when DELETE /api/user with valid session, then return success data")
//    @Test
//    void test18() {
//        String session = registerAndGetSession("abc@abc", RoleTypeEnum.COMPANY_ADMIN)
//        mockMvc.perform(delete("/api/user")
//                .header("session", session))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath('$.code', is(200)))
//                .andExpect(jsonPath('$.data', nullValue()))
//    }
//
//    @DisplayName("when DELETE /api/user with invalid session, then return error data")
//    @Test
//    void test19() {
//        mockMvc.perform(delete("/api/user")
//                .header("session", "session"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath('$.code', is(401)))
//                .andExpect(jsonPath('$.description', is("Unauthorized")))
//    }
//
//    @DisplayName("when DELETE /api/user/{id} with valid data, then return success data")
//    @Test
//    void test20() {
//        String session = registerAndGetSession("abc1234@abc1234", RoleTypeEnum.APP_DEV)
//        User beingDeleted = sessionDao.findBySessionHash(session).user
//        beingDeleted.company = companyAdmin.company
//        userDao.save(beingDeleted)
//        mockMvc.perform(delete("/api/user/" + beingDeleted.id)
//                .header("session", companyAdminSessionToken))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath('$.code', is(200)))
//                .andExpect(jsonPath('$.data', nullValue()))
//    }
//
//    @DisplayName("when DELETE /api/user/{id} with invalid session, then return error data")
//    @Test
//    void test21() {
//        String session = registerAndGetSession("abc@abc", RoleTypeEnum.COMPANY_ADMIN)
//        User beingDeleted = sessionDao.findBySessionHash(session).user
//        mockMvc.perform(delete("/api/user/" + beingDeleted.id)
//                .header("session", "session"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath('$.code', is(401)))
//                .andExpect(jsonPath('$.description', is("Unauthorized")))
//    }
//
//    @DisplayName("when DELETE /api/user/{id} with invalid id, then return error data")
//    @Test
//    void test22() {
//        mockMvc.perform(delete("/api/user/" + RandomStringUtils.randomNumeric(10))
//                .header("session", companyAdminSessionToken))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath('$.code', is(606)))
//                .andExpect(jsonPath('$.description', is("Account with given data not exist")))
//    }
//
//
//    private String registerAndGetSession(String email, RoleTypeEnum role) {
//        UserRequest registerRequest = new UserRequest('test123', email, 'test@test', 'test@test')
//        if (userDao.findByEmail(email) == null)
//            authService.register(registerRequest)
//        anotherUser = userService.userDao.findByEmail(registerRequest.email)
//        anotherUser.activated = true
//        anotherUser.blocked = false
//        anotherUser.role = roleDao.findByAuthority(role)
//        userService.userDao.save(anotherUser)
//        Company company = anotherUser.company
//        company.isLicensed = true
//        companyDao.save(company)
//        LoginRequest loginRequest = new LoginRequest(email, 'test123', 'test@test')
//        String session = authService.login(loginRequest).data.sessionToken
//        return session
//    }
//
//    private String loginUserAndSaveSession() {
//        LoginRequest loginRequest = new LoginRequest("test@test", 'test123', 'test@test')
//        String session = authService.login(loginRequest).data.sessionToken
//        return session
//
//    }

}