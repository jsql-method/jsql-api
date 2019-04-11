package pl.jsql.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.jsql.api.enums.RoleTypeEnum;
import pl.jsql.api.enums.SettingEnum;
import pl.jsql.api.model.dict.Setting;
import pl.jsql.api.model.user.Role;
import pl.jsql.api.repo.*;
import pl.jsql.api.service.ApplicationService;
import pl.jsql.api.service.AuthService;
import pl.jsql.api.service.UserService;

import javax.annotation.PostConstruct;

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
    private CompanyDao companyDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private PlanDao planDao;

    public void initRoles() {

        if (roleDao.count() > 0) {
            return;
        }

        for (RoleTypeEnum roleType : RoleTypeEnum.values()) {
            Role role = new Role();
            role.authority = roleType;
            roleDao.save(role);
        }

    }

    public void initSettings() {

        for (SettingEnum s : SettingEnum.values()) {
            if (settingDao.findByType(s) == null) {
                Setting setting = new Setting();
                setting.type = s;
                setting.value = s.defaultValue;
                settingDao.save(setting);
            }
        }

    }

    public void createFullCompanyAdmin() {

//        String email = "test@test";
//        String password = "test123";
//        User user = userService.findByEmail(email);
//        if (user != null) {
//            Plan plan = plansDao.findFirstByCompany(user.company)
//            plan.isTrial = false
//            plan.active = true
//            plan.plan = PlansEnum.LARGE
//            plansDao.save(plan)
//            return user
//        }
//        authService.register(new UserRequest(password, email, "John", "Doe"))
//        user = userService.userDao.findByEmail(email)
//        userService.activateAccount(user.activationToken)
//
//        def loginResult = authService.login(new LoginRequest(email, password, "0.0.0.0"))
//        Company company = user.company
//        company.isLicensed = true
//        companyDao.save(company)
//
//        Plan plan = plansDao.findFirstByCompany(company)
//        plan.isTrial = false
//        plan.active = true
//        plan.plan = PlansEnum.LARGE
//        plansDao.save(plan)
//
//        createApplication("Test application", user)
//
//        return user

    }

    @PostConstruct
    public void init() {

        initRoles();
        initSettings();
        createFullCompanyAdmin();

    }

//    void createApplication(String name, User user) {
//        User currentUser = user
//
//        String apiKey = "==" + applicationService.generateApplication(name)
//        User companyAdmin = currentUser
//
//        if (currentUser.role.authority == RoleTypeEnum.APP_ADMIN) {
//
//            companyAdmin = userDao.findByCompanyAndRole(currentUser.company, roleDao.findByAuthority(RoleTypeEnum.COMPANY_ADMIN)).get(0)
//        }
//
//
//        UserRequest userRequest = new UserRequest()
//        userRequest.email = name + "@applicationDeveloper"
//        userRequest.firstName = "application"
//        userRequest.lastName = "developer"
//        userRequest.password = RandomStringUtils.randomAlphanumeric(10)
//        userRequest.company = companyAdmin.company.id
//        userRequest.role = RoleTypeEnum.APP_DEV.toString()
//        authService.register(userRequest)
//
//        User applicationDeveloper = userDao.findByEmail(name + "@applicationDeveloper")
//        applicationDeveloper.isProductionDeveloper = true
//        applicationDeveloper.activated = true
//        applicationDeveloper = userDao.save(applicationDeveloper)
//
//        Application application = applicationService.createApplication(apiKey, companyAdmin, name, applicationDeveloper)
//
//        applicationService.assignUserToAppMember(companyAdmin, application);
//
//        applicationService.assignNewAppsToAppAdmins(currentUser, application);
//
//        applicationService.initializeOptionsToApp(application);
//    }


}
