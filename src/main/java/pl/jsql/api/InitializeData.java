package pl.jsql.api;

import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.jsql.api.dto.request.ApplicationCreateRequest;
import pl.jsql.api.dto.request.ResetPasswordRequest;
import pl.jsql.api.dto.request.UserRequest;
import pl.jsql.api.enums.PlansEnum;
import pl.jsql.api.enums.RoleTypeEnum;
import pl.jsql.api.enums.SettingEnum;
import pl.jsql.api.model.dict.Setting;
import pl.jsql.api.model.hashing.Application;
import pl.jsql.api.model.hashing.DeveloperKey;
import pl.jsql.api.model.hashing.Query;
import pl.jsql.api.model.stats.Build;
import pl.jsql.api.model.stats.Request;
import pl.jsql.api.model.user.Role;
import pl.jsql.api.model.user.User;
import pl.jsql.api.repo.*;
import pl.jsql.api.service.ApplicationService;
import pl.jsql.api.service.AuthService;
import pl.jsql.api.service.UserService;
import pl.jsql.api.utils.TokenUtil;

import javax.annotation.PostConstruct;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

@Component
public class InitializeData {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    @Autowired
    private SettingDao settingDao;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private UserDao userDao;

    private void initRoles() {

        if (roleDao.count() > 0) {
            return;
        }

        for (RoleTypeEnum roleType : RoleTypeEnum.values()) {
            Role role = new Role();
            role.authority = roleType;
            roleDao.save(role);
        }

    }

    private void initSettings() {

        for (SettingEnum s : SettingEnum.values()) {
            if (settingDao.findByType(s) == null) {
                Setting setting = new Setting();
                setting.type = s;
                setting.value = s.defaultValue;
                settingDao.save(setting);
            }
        }

    }

    @Autowired
    private DeveloperKeyDao developerKeyDao;

    private void createFullCompanyAdmin(String email, String name, String surname) {

        authService.register(new UserRequest(email, "x", name, surname, "JSQL Sp.z.o.o.", PlansEnum.BUSINESS, "123", "123"));

        User user = userDao.findByEmail(email);
        userService.resetPassword(user.token, new ResetPasswordRequest("test1234"));
        user = userDao.findByEmail(email);

        applicationService.create(user, new ApplicationCreateRequest("Test application"), true);

        DeveloperKey developerKey = developerKeyDao.findByUser(user);
        developerKey.key = email;

        developerKeyDao.save(developerKey);

    }

    @Autowired
    private BuildDao buildDao;

    @Autowired
    private ApplicationDao applicationDao;

    private void testBuildsData(String email) throws ParseException {

        User user = userDao.findByEmail(email);
        Application application = applicationDao.selectByCompanyAdmin(user.company);

        for(int i = 0; i < 50; i++){

            Build build = new Build();
            build.user = user;
            build.application = application;
            build.hashingDate = new SimpleDateFormat("dd/MM/yyyy hh/mm").parse("18/04/2019 12/"+i);
            build.queriesCount = RandomUtils.nextInt(10, 50);

            buildDao.save(build);

        }

        for(int i = 0; i < 10; i++){

            Build build = new Build();
            build.user = user;
            build.application = application;
            build.hashingDate = new SimpleDateFormat("dd/MM/yyyy hh/mm").parse("18/04/2019 "+i+"/20");
            build.queriesCount = RandomUtils.nextInt(10, 50);

            buildDao.save(build);

        }


        for(int i = 0; i < 25; i++){

            Build build = new Build();
            build.user = user;
            build.application = application;
            build.hashingDate = new SimpleDateFormat("dd/MM/yyyy").parse(i+"/04/2019");
            build.queriesCount = RandomUtils.nextInt(10, 50);

            buildDao.save(build);

        }


    }

    @Autowired
    private RequestDao requestDao;

    private void testRequestsData(String email) throws ParseException {

        User user = userDao.findByEmail(email);
        Application application = applicationDao.selectByCompanyAdmin(user.company);

        for(int i = 0; i < 50; i++){

            Request request = new Request();
            request.user = user;
            request.application = application;
            request.requestDate = new SimpleDateFormat("dd/MM/yyyy hh/mm").parse("18/04/2019 12/"+i);
            request.queryHash = TokenUtil.generateToken(i, 50);
            request.query = "select * from user";

            requestDao.save(request);

        }

        for(int i = 0; i < 10; i++){

            Request request = new Request();
            request.user = user;
            request.application = application;
            request.requestDate = new SimpleDateFormat("dd/MM/yyyy hh/mm").parse("18/04/2019 "+i+"/20");
            request.queryHash = TokenUtil.generateToken(i, 50);
            request.query = "select * from user where id = :id";

            requestDao.save(request);

        }


        for(int i = 0; i < 25; i++){

            Request request = new Request();
            request.user = user;
            request.application = application;
            request.requestDate = new SimpleDateFormat("dd/MM/yyyy").parse(i+"/04/2019");
            request.queryHash = TokenUtil.generateToken(i, 50);
            request.query = "delete from user where id = :id";

            requestDao.save(request);

        }


    }

    @Autowired
    private QueryDao queryDao;

    private void testQueriesData(String email) throws ParseException {

        User user = userDao.findByEmail(email);
        Application application = applicationDao.selectByCompanyAdmin(user.company);

        for(int i = 0; i < 50; i++){

            Query query = new Query();
            query.user = user;
            query.application = application;
            query.queryDate = new SimpleDateFormat("dd/MM/yyyy hh/mm").parse("18/04/2019 12/"+i);
            query.hash = TokenUtil.generateToken(i, 50);
            query.query = "select * from user";
            query.used = (i % 2) == 0;
            query.dynamic = (i % 2) == 0;

            queryDao.save(query);

        }

        for(int i = 0; i < 10; i++){

            Query query = new Query();
            query.user = user;
            query.application = application;
            query.queryDate = new SimpleDateFormat("dd/MM/yyyy hh/mm").parse("18/04/2019 "+i+"/20");
            query.hash = TokenUtil.generateToken(i, 50);
            query.query = "select * from user where id = :id";
            query.used = (i % 2) == 0;
            query.dynamic = (i % 2) == 0;

            queryDao.save(query);


        }


        for(int i = 0; i < 25; i++){

            Query query = new Query();
            query.user = user;
            query.application = application;
            query.queryDate = new SimpleDateFormat("dd/MM/yyyy").parse(i+"/04/2019");
            query.hash = TokenUtil.generateToken(i, 50);
            query.query = "delete from user where id = :id";
            query.used = (i % 2) == 0;
            query.dynamic = (i % 2) == 0;

            queryDao.save(query);


        }


    }

    public void createTestData(String email, String name, String surname) throws ParseException {

        if(userDao.findByEmail(email) == null){

            createFullCompanyAdmin(email, name, surname);
            //  testBuildsData(email);
            //  testRequestsData(email);
            //    testQueriesData(email);

        }

    }

    @PostConstruct
    public void init() throws ParseException {
        initRoles();
        initSettings();

        createTestData("pawel.stachurski@jsql.it", "Paweł", "Stachurski");
        createTestData("dawid.senko@jsql.it", "Dawid", "Senko");

    }

}
