package pl.jsql.api.service.admin;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.jsql.api.dto.request.AdvanceAppDevRequest;
import pl.jsql.api.dto.request.AppDeveloperRequest;
import pl.jsql.api.dto.request.UserRequest;
import pl.jsql.api.dto.response.AppDeveloperResponse;
import pl.jsql.api.dto.response.MessageResponse;
import pl.jsql.api.enums.RoleTypeEnum;
import pl.jsql.api.exceptions.UnauthorizedException;
import pl.jsql.api.model.hashing.Application;
import pl.jsql.api.model.hashing.ApplicationDevelopers;
import pl.jsql.api.model.user.Company;
import pl.jsql.api.model.user.User;
import pl.jsql.api.repo.*;
import pl.jsql.api.security.service.SecurityService;
import pl.jsql.api.service.AuthService;
import pl.jsql.api.utils.TokenUtil;

import javax.validation.Valid;
import java.util.List;

@Transactional
@Service
public class AppDevService {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserDao userDao;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private ApplicationDao applicationDao;

    @Autowired
    private ApplicationDevelopersDao applicationDevelopersDao;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private PlanDao planDao;

    public List<AppDeveloperResponse> list() {

        Company company = securityService.getCurrentAccount().company;
        return userDao.findyByCompanyAndRoleWithoutFake(company, roleDao.findByAuthority(RoleTypeEnum.APP_DEV));

    }

    public MessageResponse register(AppDeveloperRequest appDeveloperRequest) {

        User user = securityService.getCurrentAccount();

        int usersCount = userDao.countByCompany(user.company);
        int allowedUsers = planDao.findFirstByCompany(user.company).plan.maxUsers;

        if (allowedUsers <= usersCount) {
            return new MessageResponse(true,"developers_limit_reached");
        }

        UserRequest userRequest = new UserRequest();
        userRequest.email = appDeveloperRequest.email;
        userRequest.firstName = appDeveloperRequest.firstName;
        userRequest.lastName = appDeveloperRequest.lastName;
        userRequest.password = RandomStringUtils.randomAlphanumeric(10);
        userRequest.company = user.company.id;
        userRequest.role = RoleTypeEnum.APP_DEV;

        return authService.register(userRequest);

    }

    public MessageResponse delete(Long id) {
        User user = userDao.findById(id).orElse(null);

        if (user == null) {
            return new MessageResponse(true,"developer_does_not_exists");
        }

        if(user.role.authority != RoleTypeEnum.APP_DEV){
            throw new UnauthorizedException();
        }

        applicationDevelopersDao.clearJoinsByUser(user);
        applicationDevelopersDao.deleteAllByUser(user);

        user.email = TokenUtil.hash(user.email);
        user.firstName = TokenUtil.hash(user.firstName);
        user.lastName = TokenUtil.hash(user.lastName);
        user.lastName = TokenUtil.hash(user.password);
        user.enabled = false;
        user.isDeleted = true;
        user.company = null;

        userDao.save(user);

        return new MessageResponse();

    }

    public MessageResponse unassignMember(User user, Application app) {

        if (user == null || app == null) {
            return new MessageResponse(true,"developer_or_application_does_not_exists");
        }

        ApplicationDevelopers appMember = applicationDevelopersDao.findByUserAndAppQuery(user, app);

        if (appMember != null) {

            appMember.application = null;
            appMember.developer = null;
            applicationDevelopersDao.save(appMember);
            applicationDevelopersDao.delete(appMember);

        }

        return new MessageResponse();

    }

    public MessageResponse unassignAllAppsFromMember(User user) {

        List<Application> list = applicationDao.findByUserQuery(user);

        for (Application application : list) {
            unassignMember(user, application);
        }

        return new MessageResponse();

    }

    public MessageResponse advance(@Valid AdvanceAppDevRequest advanceAppDevRequest) {

        User appAdmin = userDao.findByEmail(advanceAppDevRequest.email);

        if (appAdmin == null) {
            return new MessageResponse(true,"no_such_developer");
        }

        appAdmin.role = roleDao.findByAuthority(RoleTypeEnum.APP_ADMIN);

        userDao.save(appAdmin);

        User companyAdmin = securityService.getCurrentAccount();
        List<ApplicationDevelopers> list = applicationDevelopersDao.findByUserQuery(companyAdmin);

        for (ApplicationDevelopers applicationDevelopers : list) {

            ApplicationDevelopers applicationDeveloper = applicationDevelopersDao.findByUserAndAppQuery(appAdmin, applicationDevelopers.application);

            if (applicationDeveloper == null) {

                applicationDeveloper = new ApplicationDevelopers();
                applicationDeveloper.application = applicationDevelopers.application;
                applicationDeveloper.developer = appAdmin;

                applicationDevelopersDao.save(applicationDeveloper);

            }
        }

        return new MessageResponse();

    }

}
