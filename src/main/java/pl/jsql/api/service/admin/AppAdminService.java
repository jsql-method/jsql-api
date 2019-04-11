package pl.jsql.api.service.admin;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.jsql.api.dto.request.UserRequest;
import pl.jsql.api.dto.response.AppAdminResponse;
import pl.jsql.api.dto.response.MessageResponse;
import pl.jsql.api.enums.RoleTypeEnum;
import pl.jsql.api.model.hashing.ApplicationDevelopers;
import pl.jsql.api.model.user.Company;
import pl.jsql.api.model.user.Role;
import pl.jsql.api.model.user.User;
import pl.jsql.api.repo.ApplicationDevelopersDao;
import pl.jsql.api.repo.PlanDao;
import pl.jsql.api.repo.RoleDao;
import pl.jsql.api.repo.UserDao;
import pl.jsql.api.security.service.SecurityService;
import pl.jsql.api.service.AuthService;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
@Service
public class AppAdminService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private AuthService authService;

    @Autowired
    private ApplicationDevelopersDao applicationDevelopersDao;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private PlanDao planDao;

    public MessageResponse register(UserRequest userRequest) {

        User companyAdmin = securityService.getCurrentAccount();
        List<ApplicationDevelopers> list = applicationDevelopersDao.findByUserQuery(companyAdmin);

        User appAdmin = userDao.findByEmail(userRequest.email);

        if (appAdmin == null) {

            int usersCount = userDao.countByCompany(companyAdmin.company);
            int allowedUsers = planDao.findFirstByCompany(companyAdmin.company).plan.maxUsers;

            if (allowedUsers <= usersCount) {
                return new MessageResponse("developers_limit_reached");
            }

            userRequest.company = companyAdmin.company.id;
            userRequest.role = "APP_ADMIN";
            userRequest.password = RandomStringUtils.randomAlphanumeric(10);

            authService.register(userRequest);
            appAdmin = userDao.findByEmail(userRequest.email);

        } else if (appAdmin.role.authority == RoleTypeEnum.APP_ADMIN) {
            return new MessageResponse("user_already_exists");
        } else if (appAdmin.role.authority == RoleTypeEnum.COMPANY_ADMIN || appAdmin.role.authority == RoleTypeEnum.ADMIN) {
            return new MessageResponse("unauthorized");
        } else {
            appAdmin.role = roleDao.findByAuthority(RoleTypeEnum.APP_ADMIN);
        }

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

    public List<AppAdminResponse> getAll() {

        Company company = securityService.getCurrentAccount().company;

        Role adminRole = roleDao.findByAuthority(RoleTypeEnum.APP_ADMIN);
        List<AppAdminResponse> appAdminResponses = userDao.findAppAdminsByCompanyAndRole(company, adminRole);

        return appAdminResponses;

    }

    public MessageResponse demote(UserRequest userRequest) {

        User appAdmin = userDao.findByEmail(userRequest.email);

        if (appAdmin == null) {
            return new MessageResponse("no_such_admin");
        }

        appAdmin.role = roleDao.findByAuthority(RoleTypeEnum.APP_DEV);

        userDao.save(appAdmin);

        return new MessageResponse();
    }

}