package pl.jsql.api.controller;

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
import pl.jsql.api.misc.TestUtils;
import pl.jsql.api.model.hashing.Application;
import pl.jsql.api.model.user.User;
import pl.jsql.api.repo.*;
import pl.jsql.api.service.ApiService;
import pl.jsql.api.service.ApplicationService;
import pl.jsql.api.service.AuthService;
import pl.jsql.api.service.admin.AppDevService;
import pl.jsql.api.service.UserService;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class HashingControllerTest extends IntegrationTest {

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
//    ApplicationDao applicationDao
//
//    @Autowired
//    MemberKeyDao memberKeyDao
//
//    @Autowired
//    ApplicationService applicationService
//
//    @Autowired
//    ApiService apiService
//
//    @Autowired
//    AppDevService memberService
//
//    @Autowired
//    UserDao userDao
//
//    @Autowired
//    RoleDao roleDao
//
//    @Autowired
//    UserService userService
//
//    @Autowired
//    TestUtils testUtils
//
//    private String session
//    private String apiKey
//    private Application application
//    private String memberKey
//    private User user
//    private String memberSession
//
//
//    @BeforeAll
//    void initiate() {
//        session = loginAndGetSession()
//        user = sessionDao.findBySessionHash(session).user
//        testUtils.createApplication("testAPP", user)
//        application = applicationDao.findByNameAndCompany("testAPP", user.company)
//        apiKey = application.apiKey
//        memberKey = memberKeyDao.findByUser(user).key
//        memberSession = createMemberAndGetSession()
//    }
//
//    @DisplayName("when GET api/request/options with valid memberKey and valid apiKey, then return success data")
//    @Test
//    void test() {
//
//        //execute request
//        mockMvc.perform(get(
//                "/api/request/options").header("apiKey", apiKey).header("memberKey", memberKey))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath('$.code', is(200)))
//                .andExpect(jsonPath('$.data', hasKey("databaseDialect")))
//                .andExpect(jsonPath('$.data', hasKey("applicationLanguage")))
//
//    }
//
//    @DisplayName("when GET api/request/options with valid memberKey and invalid apiKey, then return error data")
//    @Test
//    void test1() {
//
//        //execute request
//        mockMvc.perform(get(
//                "/api/request/options").header("apiKey", "apiKey").header("memberKey", memberKey))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath('$.code', is(611)))
//                .andExpect(jsonPath('$.description', is("No such application or member")))
//
//    }
//
//    @DisplayName("when GET api/request/options with invalid memberKey and valid apiKey, then return error data")
//    @Test
//    void test2() {
//
//        //execute request
//        mockMvc.perform(get(
//                "/api/request/options").header("apiKey", apiKey).header("memberKey", "memberKey"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath('$.code', is(611)))
//                .andExpect(jsonPath('$.description', is("No such application or member")))
//
//    }
//
//    @DisplayName("when GET api/request/options/all with valid memberKey and valid apiKey, then return success data")
//    @Test
//    void test3() {
//
//        //execute request
//        mockMvc.perform(get(
//                "/api/request/options/all").header("apiKey", apiKey).header("memberKey", memberKey))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath('$.code', is(200)))
//                .andExpect(jsonPath('$.data', hasKey("databaseDialect")))
//                .andExpect(jsonPath('$.data', hasKey("salt")))
//                .andExpect(jsonPath('$.data', hasKey("applicationLanguage")))
//
//    }
//
//    @DisplayName("when GET api/request/options/all with valid memberKey and invalid apiKey, then return error data")
//    @Test
//    void test4() {
//
//        //execute request
//        mockMvc.perform(get(
//                "/api/request/options/all").header("apiKey", "apiKey").header("memberKey", memberKey))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath('$.code', is(611)))
//                .andExpect(jsonPath('$.description', is("No such application or member")))
//
//    }
//
//    @DisplayName("when GET api/request/options/all with invalid memberKey and valid apiKey, then return error data")
//    @Test
//    void test5() {
//
//        //execute request
//        mockMvc.perform(get(
//                "/api/request/options/all").header("apiKey", apiKey).header("memberKey", "memberKey"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath('$.code', is(611)))
//                .andExpect(jsonPath('$.description', is("No such application or member")))
//
//    }
//
//    @DisplayName("when POST api/request/hashes with valid memberKey and valid apiKey, then return success data")
//    @Test
//    void test6() {
//
//        //execute request
//        mockMvc.perform(post(
//                "/api/request/hashes").header("apiKey", apiKey).header("memberKey", memberKey)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content("[\"select * from public.Application\"]"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath('$.code', is(200)))
//                .andExpect(jsonPath('$.data[0]', hasKey("token")))
//                .andExpect(jsonPath('$.data[0]', hasKey("query")))
//
//    }
//
//    @DisplayName("when POST api/request/hashes with valid memberKey and invalid apiKey, then return error data")
//    @Test
//    void test7() {
//
//        //execute request
//        mockMvc.perform(post(
//                "/api/request/hashes").header("apiKey", "apiKey").header("memberKey", memberKey)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content("[\"select * from public.Application\"]"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath('$.code', is(611)))
//                .andExpect(jsonPath('$.description', is("No such application or member")))
//
//    }
//
//    @DisplayName("when POST api/request/hashes with invalid memberKey and valid apiKey, then return error data")
//    @Test
//    void test8() {
//
//        //execute request
//        mockMvc.perform(post(
//                "/api/request/hashes").header("apiKey", apiKey).header("memberKey", "memberKey")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content("[\"select * from public.Application\"]"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath('$.code', is(611)))
//                .andExpect(jsonPath('$.description', is("No such application or member")))
//
//    }
//
//    @DisplayName("when POST api/request/queries with valid memberKey and valid apiKey, then return success data")
//    @Test
//    void test9() {
//
//        String token = apiService.getRequestHashesResult(memberKey, apiKey, ["select * from public.person"]).data[0].token
//        mockMvc.perform(post(
//                "/api/request/queries").header("apiKey", apiKey).header("memberKey", memberKey)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content("[\"" + token + "\"]"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath('$.[0]', hasKey("token")))
//                .andExpect(jsonPath('$.[0]', hasKey("query")))
//
//    }
//
//    @DisplayName("when POST api/request/queries with valid memberKey and invalid apiKey, then return error data")
//    @Test
//    void test10() {
//
//        String token = apiService.getRequestHashesResult(memberKey, apiKey, ["select * from public.person"]).data[0].token
//        mockMvc.perform(post(
//                "/api/request/queries").header("apiKey", "apiKey").header("memberKey", memberKey)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content("[\"" + token + "\"]"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath('$.code', is(611)))
//                .andExpect(jsonPath('$.description', is("No such application or member")))
//
//
//    }
//
//    @DisplayName("when POST api/request/queries with invalid memberKey and valid apiKey, then return error data")
//    @Test
//    void test11() {
//
//        String token = apiService.getRequestHashesResult(memberKey, apiKey, ["select * from public.person"]).data[0].token
//        mockMvc.perform(post(
//                "/api/request/queries").header("apiKey", apiKey).header("memberKey", "memberKey")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content("[\"" + token + "\"]"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath('$.code', is(611)))
//                .andExpect(jsonPath('$.description', is("No such application or member")))
//
//    }
//
//    @DisplayName("when PATCH api/request/query/{id} with valid session and valid apiKey and valid id as company admin, then return error data")
//    @Test
//    void test15() {
//        String token = apiService.getRequestHashesResult(memberKey, apiKey, ["select * from public.person"]).data[0].token
//        Long queryId = apiService.queryDao.findByApplicationAndUserAndHash(application, user, token).id
//        user.role = roleDao.findByAuthority(RoleTypeEnum.COMPANY_ADMIN)
//        userDao.save(user)
//        mockMvc.perform(patch(
//                "/api/request/query/" + queryId)
//                .header("session", session)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content("{\n" +
//                "                    \"query\":\"select name from public.person\",\n" +
//                "                    \"apiKey\": \"" + apiKey + "\"\n" +
//                "                }"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath('$.code', is(403)))
//                .andExpect(jsonPath('$.description', is("Insufficient permissions")))
//
//
//    }
//
//    @DisplayName("when PATCH api/request/query/{id} with valid session and valid apiKey and valid id as app dev, then return success data")
//    @Test
//    void test16() {
//        String token = apiService.getRequestHashesResult(memberKey, apiKey, ["select * from public.person"]).data[0].token
//        Long queryId = apiService.queryDao.findByApplicationAndUserAndHash(application, user, token).id
//        mockMvc.perform(patch(
//                "/api/request/query/" + queryId)
//                .header("session", memberSession)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content("{\n" +
//                "                    \"query\":\"select name from public.person\",\n" +
//                "                    \"apiKey\": \"" + apiKey + "\"\n" +
//                "                }"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath('$.code', is(200)))
//                .andExpect(jsonPath('$.data', nullValue()))
//
//    }
//
//    @DisplayName("when PATCH api/request/query/{id} with valid session and valid apiKey and valid id as app dev with another query type, then return error data")
//    @Test
//    void test17() {
//        String token = apiService.getRequestHashesResult(memberKey, apiKey, ["select * from public.person"]).data[0].token
//        Long queryId = apiService.queryDao.findByApplicationAndUserAndHash(application, user, token).id
//        mockMvc.perform(patch(
//                "/api/request/query/" + queryId)
//                .header("session", memberSession)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content("{\n" +
//                "                    \"query\":\"insert into public.person(name, surname) values('jane','doe')\",\n" +
//                "                    \"apiKey\": \"" + apiKey + "\"\n" +
//                "                }"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath('$.code', is(602)))
//                .andExpect(jsonPath('$.data', is("You can not change the type of query")))
//
//    }
//
//    @DisplayName("when PATCH api/request/query/{id} with invalid session and valid apiKey and valid id as app dev, then return error data")
//    @Test
//    void test18() {
//        String token = apiService.getRequestHashesResult(memberKey, apiKey, ["select * from public.person"]).data[0].token
//        Long queryId = apiService.queryDao.findByApplicationAndUserAndHash(application, user, token).id
//        mockMvc.perform(patch(
//                "/api/request/query/" + queryId)
//                .header("session", "session")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content("{\n" +
//                "                    \"query\":\"select name from public.person\",\n" +
//                "                    \"apiKey\": \"" + apiKey + "\"\n" +
//                "                }"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath('$.code', is(401)))
//                .andExpect(jsonPath('$.description', is("Unauthorized")))
//
//    }
//
//    @DisplayName("when PATCH api/request/query/{id} with valid session and invalid apiKey and valid id as app dev, then return error data")
//    @Test
//    void test19() {
//        String token = apiService.getRequestHashesResult(memberKey, apiKey, ["select * from public.person"]).data[0].token
//        Long queryId = apiService.queryDao.findByApplicationAndUserAndHash(application, user, token).id
//        mockMvc.perform(patch(
//                "/api/request/query/" + queryId)
//                .header("session", memberSession)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content("{\n" +
//                "                    \"query\":\"select name from public.person\",\n" +
//                "                    \"apiKey\": \"apikey\"\n" +
//                "                }"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath('$.code', is(603)))
//                .andExpect(jsonPath('$.data', is("Your application key does not match")))
//
//    }
//
//    @DisplayName("when PATCH api/request/query/{id} with valid session and valid apiKey and invalid id as app dev, then return error data")
//    @Test
//    void test20() {
//        String token = apiService.getRequestHashesResult(memberKey, apiKey, ["select * from public.person"]).data[0].token
//        Long queryId = apiService.queryDao.findByApplicationAndUserAndHash(application, user, token).id
//        mockMvc.perform(patch(
//                "/api/request/query/" + queryId + 1)
//                .header("session", memberSession)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content("{\n" +
//                "                    \"query\":\"select name from public.person\",\n" +
//                "                    \"apiKey\": \"" + apiKey + "\"\n" +
//                "                }"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath('$.code', is(620)))
//                .andExpect(jsonPath('$.data', is("Query with given id does not exist")))
//
//    }
//
//    @DisplayName("when POST api/request/queries/grouped with valid memberKey and valid apiKey, then return success data")
//    @Test
//    void test21() {
//        List<String> token = apiService.getRequestHashesResult(memberKey, apiKey, ["select * from public.person", "where example = false"]).data
//        mockMvc.perform(post(
//                "/api/request/queries/grouped").header("apiKey", apiKey).header("memberKey", memberKey)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content("[\"" + token.get(0).token + "\", \"" + token.get(1).token + "\"]"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath('$.[0]', hasKey("token")))
//                .andExpect(jsonPath('$.[0]', hasKey("query")))
//
//    }
//
//    @DisplayName("when POST api/request/queries/grouped with valid memberKey and invalid apiKey, then return error data")
//    @Test
//    void test22() {
//        List<String> token = apiService.getRequestHashesResult(memberKey, apiKey, ["select * from public.person", "where example = false"]).data
//        mockMvc.perform(post(
//                "/api/request/queries/grouped").header("apiKey", "apiKey").header("memberKey", memberKey)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content("[\"" + token.get(0).token + "\", \"" + token.get(1).token + "\"]"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath('$.code', is(611)))
//                .andExpect(jsonPath('$.description', is("No such application or member")))
//
//
//    }
//
//    @DisplayName("when POST api/request/queries/grouped with invalid memberKey and valid apiKey, then return error data")
//    @Test
//    void test23() {
//        List<String> token = apiService.getRequestHashesResult(memberKey, apiKey, ["select * from public.person", "where example = false"]).data
//        mockMvc.perform(post(
//                "/api/request/queries/grouped").header("apiKey", apiKey).header("memberKey", "memberKey")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content("[\"" + token.get(0).token + "\", \"" + token.get(1).token + "\"]"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath('$.code', is(611)))
//                .andExpect(jsonPath('$.description', is("No such application or member")))
//
//    }
//
//    private String loginAndGetSession() {
//        LoginRequest loginRequest = new LoginRequest("test@test", 'test123', 'test@test')
//        String session = authService.login(loginRequest).data.sessionToken
//        return session
//    }
//
//    private String createMemberAndGetSession() {
//        UserRequest memberRequest = new UserRequest("member123", 'member@member', 'Jane', 'Smith')
//        memberRequest.company = user.company.id
//
//        memberRequest.role = RoleTypeEnum.APP_DEV.toString()
//
//        authService.register(memberRequest)
//        User member = userDao.findByEmail("member@member")
//        member.activated = true
//        userDao.save(member)
//        applicationService.assignUserToAppMember(member, application)
//        LoginRequest loginRequest = new LoginRequest("member@member", 'member123', 'test@test')
//        String session = authService.login(loginRequest).data.sessionToken
//        return session
//    }

}

