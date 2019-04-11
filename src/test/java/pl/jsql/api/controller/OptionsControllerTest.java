package pl.jsql.api.controller;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pl.jsql.api.dto.request.LoginRequest;
import pl.jsql.api.misc.IntegrationTest;
import pl.jsql.api.model.hashing.Application;
import pl.jsql.api.model.user.User;
import pl.jsql.api.repo.*;
import pl.jsql.api.service.ApplicationService;
import pl.jsql.api.service.AuthService;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class OptionsControllerTest extends IntegrationTest {
//
//    @Autowired
//    MockMvc mockMvc
//
//    @Autowired
//    AuthService loginService
//
//    @Autowired
//    SessionDao sessionDao
//
//    @Autowired
//    UserDao userDao
//
//    @Autowired
//    MemberKeyDao memberKeyDao
//
//    @Autowired
//    RoleDao roleDao
//
//    @Autowired
//    OptionsDao optionsDao
//
//    @Autowired
//    ApplicationService applicationService
//
//    @Autowired
//    ApplicationDao applicationDao
//
//
//    private String companyAdminSessionToken
//    private Application application
//    private User companyAdmin
//
//    @BeforeAll
//    void initiate() {
//        companyAdminSessionToken = loginUserAndSaveSession()
//        application = applicationDao.findByUserQuery(companyAdmin).get(0)
//    }
//
//    @DisplayName("when GET /api/options with valid session, then return success data")
//    @Test
//    void test() {
//        mockMvc.perform(get("/api/options").header("session", companyAdminSessionToken))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath('$.code', is(200)))
//                .andExpect(jsonPath('$.data[0]', hasKey("application")))
//                .andExpect(jsonPath('$.data[0]', hasKey("options")))
//    }
//
//    @DisplayName("when GET /api/options with invalid session, then return error data")
//    @Test
//    void test1() {
//        mockMvc.perform(get("/api/options").header("session", "session"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath('$.code', is(401)))
//                .andExpect(jsonPath('$.description', is("Unauthorized")))
//    }
//
//    @DisplayName("when GET /api/options/{id} with valid session and valid app id, then return success data")
//    @Test
//    void test2() {
//        mockMvc.perform(get("/api/options/" + application.id).header("session", companyAdminSessionToken))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath('$', isA(HashMap.class)))
//                .andExpect(jsonPath('$', hasKey("databaseDialect")))
//    }
//
//    @DisplayName("when GET /api/options/{id} with invalid session and valid app id, then return error data")
//    @Test
//    void test3() {
//        mockMvc.perform(get("/api/options/" + application.id).header("session", "session"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath('$.code', is(401)))
//                .andExpect(jsonPath('$.description', is("Unauthorized")))
//    }
//
//    @DisplayName("when GET /api/options/{id} with valid session and invalid app id, then return error data")
//    @Test
//    void test4() {
//        mockMvc.perform(get("/api/options/" + RandomStringUtils.randomNumeric(11)).header("session", companyAdminSessionToken))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath('$.code', is(611)))
//                .andExpect(jsonPath('$.description', is("No such application or member")))
//    }
//
//    @DisplayName("when GET /api/options/values with valid session, then return success data")
//    @Test
//    void test5() {
//        mockMvc.perform(get("/api/options/values").header("session", companyAdminSessionToken))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath('$.code', is(200)))
//                .andExpect(jsonPath('$.data', isA(List.class)))
//    }
//
//    @DisplayName("when GET /api/options/values with invalid session, then return error data")
//    @Test
//    void test6() {
//        mockMvc.perform(get("/api/options/values").header("session", "session"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath('$.code', is(401)))
//                .andExpect(jsonPath('$.description', is("Unauthorized")))
//    }
//
//    @DisplayName("when PATCH /api/options/{id} with valid session, then return success data")
//    @Test
//    void test7() {
//        mockMvc.perform(patch("/api/options/" + application.id).header("session", companyAdminSessionToken)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content("{\n" +
//                "            \"applicationLanguage\": \"PHP\"\n" +
//                "        }"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath('$.code', is(200)))
//                .andExpect(jsonPath('$.data', nullValue()))
//    }
//
//    @DisplayName("when PATCH /api/options/{id} with invalid session, then return error data")
//    @Test
//    void test8() {
//        mockMvc.perform(patch("/api/options/" + application.id).header("session", "session")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content("{\n" +
//                "            \"applicationLanguage\": \"PHP\"\n" +
//                "        }"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath('$.code', is(401)))
//                .andExpect(jsonPath('$.description', is("Unauthorized")))
//    }
//
//    @DisplayName("when PATCH /api/options/{id} with valid session and invalid app id, then return error data")
//    @Test
//    void test9() {
//        mockMvc.perform(patch("/api/options/" + RandomStringUtils.randomNumeric(11)).header("session", companyAdminSessionToken)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content("{\n" +
//                "            \"applicationLanguage\": \"PHP\"\n" +
//                "        }"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath('$.code', is(611)))
//                .andExpect(jsonPath('$.description', is("No such application or member")))
//    }
//
//    private String loginUserAndSaveSession() {
//        LoginRequest loginRequest = new LoginRequest("test@test", 'test123', 'test@test')
//        String session = loginService.login(loginRequest).data.sessionToken
//        companyAdmin = sessionDao.findBySessionHash(session).user
//        return session
//
//    }

}