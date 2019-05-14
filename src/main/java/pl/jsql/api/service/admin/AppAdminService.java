package pl.jsql.api.service.admin;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.jsql.api.dto.request.AppAdminRequest;
import pl.jsql.api.dto.request.DemoteAppAdminRequest;
import pl.jsql.api.dto.request.UserRequest;
import pl.jsql.api.dto.response.AppAdminResponse;
import pl.jsql.api.dto.response.MessageResponse;
import pl.jsql.api.dto.response.UserResponse;
import pl.jsql.api.enums.RoleTypeEnum;
import pl.jsql.api.exceptions.UnauthorizedException;
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
import pl.jsql.api.utils.TokenUtil;

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

    public MessageResponse register(AppAdminRequest appAdminRequest) {

        User companyAdmin = securityService.getCurrentAccount();
        List<ApplicationDevelopers> list = applicationDevelopersDao.findByUserQuery(companyAdmin);

        User appAdmin = userDao.findByEmail(appAdminRequest.email);

        if (appAdmin == null) {

            int usersCount = userDao.countActiveUsersByCompany(companyAdmin.company);
            int allowedUsers = planDao.findFirstByCompany(companyAdmin.company).plan.maxUsers;

            if (usersCount >= allowedUsers) {
                return new MessageResponse(true,"developers_limit_reached");
            }

            UserRequest userRequest = new UserRequest();
            userRequest.email = appAdminRequest.email;
            userRequest.firstName = appAdminRequest.firstName;
            userRequest.lastName = appAdminRequest.lastName;
            userRequest.company = companyAdmin.company.id;
            userRequest.role = RoleTypeEnum.APP_ADMIN;
            userRequest.password = RandomStringUtils.randomAlphanumeric(10);

            authService.register(userRequest);
            appAdmin = userDao.findByEmail(userRequest.email);

        } else if (appAdmin.role.authority == RoleTypeEnum.APP_ADMIN) {
            return new MessageResponse(true,"user_already_exists");
        } else if (appAdmin.role.authority == RoleTypeEnum.COMPANY_ADMIN || appAdmin.role.authority == RoleTypeEnum.ADMIN) {
            return new MessageResponse(true,"unauthorized");
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

        User currentUser = securityService.getCurrentAccount();

        Role adminRole = roleDao.findByAuthority(RoleTypeEnum.APP_ADMIN);
        List<AppAdminResponse> appAdminResponses = userDao.findAppAdminsByCompanyAndRole(currentUser.company, adminRole);

        if(securityService.getCurrentRole() == RoleTypeEnum.COMPANY_ADMIN){
            appAdminResponses.add(new AppAdminResponse(currentUser.id, currentUser.email, currentUser.firstName, currentUser.lastName, currentUser.enabled, true));
        }

        return appAdminResponses;

    }

    public MessageResponse demote(DemoteAppAdminRequest demoteAppAdminRequest) {

        User appAdmin = userDao.findByEmail(demoteAppAdminRequest.email);

        if (appAdmin == null) {
            return new MessageResponse(true,"no_such_admin");
        }

        appAdmin.role = roleDao.findByAuthority(RoleTypeEnum.APP_DEV);

        userDao.save(appAdmin);

        return new MessageResponse();
    }

    public MessageResponse delete(Long id) {
        User user = userDao.findById(id).orElse(null);

        if (user == null) {
            return new MessageResponse(true,"admin_does_not_exists");
        }

        if(user.role.authority != RoleTypeEnum.APP_ADMIN){
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

}