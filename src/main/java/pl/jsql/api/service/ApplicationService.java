package pl.jsql.api.service;

import jdk.nashorn.internal.parser.Token;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.jsql.api.dto.request.ApplicationCreateRequest;
import pl.jsql.api.dto.request.UserRequest;
import pl.jsql.api.dto.response.ApplicationResponse;
import pl.jsql.api.dto.response.MessageResponse;
import pl.jsql.api.enums.DatabaseDialectEnum;
import pl.jsql.api.enums.EncodingEnum;
import pl.jsql.api.enums.RoleTypeEnum;
import pl.jsql.api.model.hashing.Application;
import pl.jsql.api.model.hashing.ApplicationDevelopers;
import pl.jsql.api.model.hashing.Options;
import pl.jsql.api.model.user.Company;
import pl.jsql.api.model.user.User;
import pl.jsql.api.repo.*;
import pl.jsql.api.security.service.SecurityService;
import pl.jsql.api.utils.TokenUtil;

import java.util.ArrayList;
import java.util.List;

@Transactional
@Service
public class ApplicationService {

    @Autowired
    private ApplicationDao applicationDao;

    @Autowired
    private OptionsDao optionsDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private ApplicationDevelopersDao applicationDevelopersDao;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private PlanDao planDao;

    @Autowired
    private AuthService authService;

    @Autowired
    private DeveloperKeyDao developerKeyDao;

    public List<ApplicationResponse> list() {

        User currentUser = securityService.getCurrentAccount();
        List<ApplicationResponse> applicationResponses = new ArrayList<>();

        switch (securityService.getCurrentRole()) {
            case ADMIN:
                applicationResponses = applicationDao.selectAllApplicationsForAdmin();
                break;
            case APP_DEV:
                applicationResponses = applicationDao.selectAllApplicationsForAppDeveloper(currentUser);
                break;
            case APP_ADMIN:
                applicationResponses = applicationDao.selectAllApplicationsForAppAdmin(currentUser);
                break;
            case COMPANY_ADMIN:
                applicationResponses = applicationDao.selectAllApplicationsForCompanyAdmin(currentUser);
                break;
        }

        return applicationResponses;

    }

    public ApplicationResponse getById(Long id) {


        User currentUser = securityService.getCurrentAccount();
        ApplicationResponse applicationResponse = null;

        switch (securityService.getCurrentRole()) {
            case ADMIN:
                applicationResponse = applicationDao.selectApplicationForAdminById(id);
                break;
            case APP_DEV:
                applicationResponse = applicationDao.selectApplicationForAppDeveloper(id, currentUser);
                break;
            case APP_ADMIN:
                applicationResponse = applicationDao.selectApplicationForAppAdmin(id, currentUser);
                break;
            case COMPANY_ADMIN:
                applicationResponse = applicationDao.selectApplicationForCompanyAdmin(id, currentUser);
                break;
        }

        if (applicationResponse == null) {
            throw new SecurityException();
        }

        return applicationResponse;

    }

    public Boolean canCreateApplication(User companyAdmin) {

        int activeApplications = applicationDao.countByCompanyAdmin(companyAdmin);
        int allowedApplications = planDao.findFirstByCompany(companyAdmin.company).plan.maxApps;

        return allowedApplications > activeApplications;

    }

    public MessageResponse create(ApplicationCreateRequest applicationCreateRequest) {

        User companyAdmin = securityService.getCompanyAdmin();
        return this.create(companyAdmin, applicationCreateRequest, false);

    }

    public MessageResponse create(User companyAdmin, ApplicationCreateRequest applicationCreateRequest) {
        return this.create(companyAdmin, applicationCreateRequest, false);
    }

    public MessageResponse create(User companyAdmin, ApplicationCreateRequest applicationCreateRequest, Boolean testApiKey) {

        if (!this.canCreateApplication(companyAdmin)) {
            return new MessageResponse(true, "applications_limit_reached");
        }

        Application app = applicationDao.findByNameAndCompany(applicationCreateRequest.name, companyAdmin.company);

        if (app != null && !app.active) {
            app.active = true;
            //Nowy developer bo przy wyłączaniu aplikacji (usuwaniu) po prostu są usuwane wiązania wraz z tym developerem
            app.productionDeveloper = this.createFakeDeveloper(applicationCreateRequest.name, companyAdmin.company);

            applicationDao.save(app);

            return new MessageResponse(app.id.toString());

        } else if (app != null) {
            return new MessageResponse(true,"application_already_exists");
        }


        Application application = this.createApplication(companyAdmin, applicationCreateRequest);
        assignUserToAppMember(companyAdmin, application);
        assignNewAppsToAppAdmins(companyAdmin, application);
        initializeApplicationOptions(application);

        if(testApiKey){
            application.apiKey = companyAdmin.email;
            applicationDao.save(application);
        }

        return new MessageResponse(application.id.toString());

    }

    private User createFakeDeveloper(String name, Company company) {

        String email = name + "@applicationDeveloper"+company.id;
        UserRequest userRequest = new UserRequest();
        userRequest.email = email;
        userRequest.firstName = "application";
        userRequest.lastName = "developer";
        userRequest.password = RandomStringUtils.randomAlphanumeric(10);
        userRequest.company = company.id;
        userRequest.role = RoleTypeEnum.APP_DEV;
        userRequest.isFakeDeveloper = true;

        authService.register(userRequest);

        User applicationDeveloper = userDao.findByEmail(email);

        applicationDeveloper.isProductionDeveloper = true;
        applicationDeveloper.enabled = true;

        return userDao.save(applicationDeveloper);

    }

    public MessageResponse disableApplication(Long id) {

        Application application = applicationDao.findById(id).orElse(null);

        if(application == null){
            return new MessageResponse(true,"application_not_found");
        }

        application.active = false;

        developerKeyDao.deleteByUser(application.productionDeveloper);
        applicationDevelopersDao.deleteAllByApplication(application);
        userDao.delete(application.productionDeveloper);

        application.name = application.name+"=="+ TokenUtil.randomSalt();
        application.productionDeveloper = null;
        applicationDao.save(application);

        return new MessageResponse();

    }

    public void assignNewAppsToAppAdmins(User companyAdmin, Application application) {

        List<User> appAdmins = userDao.findByCompanyAndRole(companyAdmin.company, roleDao.findByAuthority(RoleTypeEnum.APP_ADMIN));

        for (User appAdmin : appAdmins) {
            assignUserToAppMember(appAdmin, application);
        }

    }

    public void assignUserToAppMember(User user, Application application) {

        ApplicationDevelopers applicationDevelopers = applicationDevelopersDao.findByUserAndAppQuery(user, application);

        if (applicationDevelopers == null) {
            applicationDevelopers = new ApplicationDevelopers();
            applicationDevelopers.application = application;
            applicationDevelopers.developer = user;

            applicationDevelopersDao.save(applicationDevelopers);
        }

    }

    public String generateApiKey(String name) {

        String apiKey = "==" + TokenUtil.generateToken(name);

        Application application = applicationDao.findByApiKey(apiKey);

        if (application != null) {
            throw new RuntimeException("unable_to_generate_api_key");
        }

        return apiKey;


    }

    public void initializeApplicationOptions(Application application) {

        Options options = new Options();
        options.application = application;
        options.encodingAlgorithm = EncodingEnum.SHA256;
        options.isSalt = true;
        options.salt = "";
        options.saltBefore = true;
        options.saltAfter = true;
        options.saltRandomize = true;
        options.hashLengthLikeQuery = false;
        options.hashMinLength = 20;
        options.hashMaxLength = 50;
        options.removeQueriesAfterBuild = true;
        options.databaseDialect = DatabaseDialectEnum.POSTGRES;
        options.allowedPlainQueries = false;

        options.prodDatabaseConnectionUrl = "";
        options.prodDatabaseConnectionUsername = "";
        options.prodDatabaseConnectionPassword = "";
        options.prodDatabaseConnectionTimeout = 10;

        options.devDatabaseConnectionUrl = "";
        options.devDatabaseConnectionUsername = "";
        options.devDatabaseConnectionPassword = "";
        options.devDatabaseConnectionTimeout = 10;

        optionsDao.save(options);

    }

    public Application createApplication(User companyAdmin, ApplicationCreateRequest applicationCreateRequest) {

        String apiKey = this.generateApiKey(applicationCreateRequest.name);

        Application application = new Application();
        application.apiKey = apiKey;
        application.companyAdmin = companyAdmin;
        application.name = applicationCreateRequest.name;

        application.productionDeveloper = this.createFakeDeveloper(applicationCreateRequest.name, companyAdmin.company);
        application.active = true;

        return applicationDao.save(application);

    }

}
