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
import pl.jsql.api.repo.SessionDao;
import pl.jsql.api.repo.UserDao;
import pl.jsql.api.service.AuthService;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Login controller")
public class AuthorizationControllerTest extends IntegrationTest {

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
//    private String session
//
//    @BeforeAll
//    void initiate() {
//        session = loginAndGetSession()
//    }
//
//    @DisplayName("when POST api/login then should successfullly login")
//    @Test
//    void test() {
//        mockMvc.perform(post(
//                "/api/login").header("session", "session")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content("{\"email\":\"test@test\",\"password\":\"test123\"}"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath('$.code', is(200)))
//                .andExpect(jsonPath('$.data', hasKey("sessionToken")))
//                .andExpect(jsonPath('$.data', hasKey("role")))
//    }
//
//    @DisplayName("when POST api/login with authorized session then should return message")
//    @Test
//    void test1() {
//        mockMvc.perform(post(
//                "/api/login").header("session", session)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content("{\"email\":\"test@test\",\"password\":\"test123\"}"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath('$.code', is(600)))
//                .andExpect(jsonPath('$.description', is("User already authorized")))
//    }
//
//    @DisplayName("when DELETE api/logout then should return success data")
//    @Test
//    void test2() {
//        mockMvc.perform(delete(
//                "/api/logout").header("session", session))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath('$.code', is(200)))
//                .andExpect(jsonPath('$.data', nullValue()))
//    }
//
//    @DisplayName("when DELETE api/logout with invalid session then should return error message")
//    @Test
//    void test3() {
//        mockMvc.perform(delete(
//                "/api/logout").header("session", "session"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath('$.code', is(401)))
//                .andExpect(jsonPath('$.description', is("Unauthorized")))
//    }
//
//    @DisplayName("when POST /api/register with valid session, then return error data")
//    @Test
//    void test4() {
//        mockMvc.perform(post("/api/register")
//                .header("session", loginAndGetSession())
//                .contentType(MediaType.APPLICATION_JSON)
//                .content("{\"email\":\"testing@test123\",\n" +
//                "          \"password\":\"test123\",\n" +
//                "          \"firstName\":\"test\",\n" +
//                "          \"lastName\":\"test\"\n" +
//                "        }"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath('$.code', is(600)))
//                .andExpect(jsonPath('$.description', is("User already authorized")))
//    }
//
//    @DisplayName("when POST /api/register with invalid session, then return success data")
//    @Test
//    void test5() {
//        mockMvc.perform(post("/api/register")
//                .header("session", "session")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content("{\"email\":\"testing123221@test123\",\n" +
//                "          \"password\":\"test123\",\n" +
//                "          \"firstName\":\"test\",\n" +
//                "          \"lastName\":\"test\"\n" +
//                "        }"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath('$.code', is(200)))
//                .andExpect(jsonPath('$.description', is("Success")))
//    }
//
//    @DisplayName("when POST /api/register with too short password, then return error data")
//    @Test
//    void test6() {
//        mockMvc.perform(post("/api/register")
//                .header("session", "session")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content("{\"email\":\"testing@test123\",\n" +
//                "          \"password\":\"123\",\n" +
//                "          \"firstName\":\"test\",\n" +
//                "          \"lastName\":\"test\"\n" +
//                "        }"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath('$.code', is(612)))
//                .andExpect(jsonPath('$.description', is("Password is too short")))
//    }
//
//    @DisplayName("when POST /api/register with no password, then return error data")
//    @Test
//    void test7() {
//        mockMvc.perform(post("/api/register")
//                .header("session", "session")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content("{\"email\":\"testing@test123\",\n" +
//                "          \"firstName\":\"test\",\n" +
//                "          \"lastName\":\"test\"\n" +
//                "        }"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath('$.code', is(605)))
//                .andExpect(jsonPath('$.description', is("Empty password field")))
//    }
//
//    @DisplayName("when POST /api/register with too long email, then return error data")
//    @Test
//    void test8() {
//        mockMvc.perform(post("/api/register")
//                .header("session", "session")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content("{\"email\":\"" + RandomStringUtils.randomAlphabetic(255) + "@test123\",\n" +
//                "          \"password\":\"test123\",\n" +
//                "          \"firstName\":\"test\",\n" +
//                "          \"lastName\":\"test\"\n" +
//                "        }"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath('$.code', is(613)))
//                .andExpect(jsonPath('$.description', is("Given email is too long")))
//    }
//
//    @DisplayName("when POST /api/register with empty email, then return error data")
//    @Test
//    void test9() {
//        mockMvc.perform(post("/api/register")
//                .header("session", "session")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content("{" +
//                "          \"password\":\"test123\",\n" +
//                "          \"firstName\":\"test\",\n" +
//                "          \"lastName\":\"test\"\n" +
//                "        }"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath('$.code', is(604)))
//                .andExpect(jsonPath('$.description', is("Empty email field")))
//    }
//
//    @DisplayName("when POST /api/register with used email, then return error data")
//    @Test
//    void test10() {
//        mockMvc.perform(post("/api/register")
//                .header("session", "session")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content("{\"email\":\"test@test\",\n" +
//                "          \"password\":\"test123\",\n" +
//                "          \"firstName\":\"test\",\n" +
//                "          \"lastName\":\"test\"\n" +
//                "        }"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath('$.code', is(614)))
//                .andExpect(jsonPath('$.description', is("The given e-mail is already registered in the system. Use the Forgot Password option")))
//    }
//
//
//    private String loginAndGetSession() {
//        LoginRequest loginRequest = new LoginRequest("test@test", 'test123', 'test@test')
//        String session = loginService.login(loginRequest).data.sessionToken
//        return session
//    }


}

