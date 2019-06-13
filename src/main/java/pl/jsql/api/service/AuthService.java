package pl.jsql.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.jsql.api.dto.request.LoginRequest;
import pl.jsql.api.dto.request.UserRequest;
import pl.jsql.api.dto.response.MessageResponse;
import pl.jsql.api.dto.response.SessionResponse;
import pl.jsql.api.enums.RoleTypeEnum;
import pl.jsql.api.model.hashing.DeveloperKey;
import pl.jsql.api.model.user.Company;
import pl.jsql.api.model.user.Session;
import pl.jsql.api.model.user.User;
import pl.jsql.api.repo.*;
import pl.jsql.api.security.service.SecurityService;
import pl.jsql.api.utils.HashingUtil;

import javax.transaction.Transactional;
import java.util.Date;

@Transactional
@Service
public class AuthService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private SessionDao sessionDao;

    @Autowired
    private DeveloperKeyDao developerKeyDao;

    @Autowired
    private PlanDao planDao;

    @Autowired
    private CompanyDao companyDao;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private SettingDao settingDao;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private PlanService planService;

    @Transactional
    public MessageResponse register(UserRequest userRequest) {

        User user = createUser(userRequest);

        if (userRequest.role != null && !userRequest.role.equals(RoleTypeEnum.COMPANY_ADMIN) && !userRequest.role.equals(RoleTypeEnum.ADMIN)) {
            user.role = roleDao.findByAuthority(userRequest.role);
        } else {
            user.role = roleDao.findByAuthority(RoleTypeEnum.COMPANY_ADMIN);
        }

        Company company;

        if (userRequest.company != null) {

            company = companyDao.findById(userRequest.company).orElse(null);

            if (company == null) {
                return new MessageResponse(true,"company_not_found");
            }

        } else {

            company = new Company();
            company.name = userRequest.companyName;
            company.isLicensed = true;
            company.creationDate = new Date();
            company.pabblyCustomerId = userRequest.pabblyCustomerId;

            company = companyDao.save(company);

        }

        user.company = company;
        user.token = HashingUtil.encode(user.email + new Date());
        user.activationDate = new Date();
        user.enabled = false;
        user = userDao.save(user);

        this.createDeveloperKey(user, userRequest.testKeyPrefix);

        if(userRequest.isFakeDeveloper){
            return new MessageResponse();
        }

        if (user.role.authority != RoleTypeEnum.COMPANY_ADMIN) {
            emailService.sendActivationDeveloperMail(user, userRequest.password);
        } else {
            emailService.sendActivationCompanyAdminMail(user);
            planService.createPlan(user, userRequest);
        }

        return new MessageResponse();

    }


    private DeveloperKey createDeveloperKey(User developer, String testKeyPrefix) {

        DeveloperKey developerKey = new DeveloperKey();
        developerKey.user = developer;

        if(testKeyPrefix != null){
            developerKey.key = testKeyPrefix;
        }else{
            developerKey.key = HashingUtil.encode(developer.role.toString() + developer.email + developer.firstName);
        }



        return developerKeyDao.save(developerKey);

    }

    private User createUser(UserRequest userRequest) {

        User user = new User();
        user.password = HashingUtil.encode(userRequest.password);
        user.email = userRequest.email;
        user.firstName = userRequest.firstName.trim();
        user.lastName = userRequest.lastName.trim();
        user.isProductionDeveloper = false;
        user.isDevelopmentDeveloper = false;
        user.registerDate = new Date();
        user.enabled = true;

        return user;

    }

    public SessionResponse login(LoginRequest loginRequest) {

        Session session = sessionService.createSession(loginRequest);
        User user = session.user;

        if(!user.enabled){
            throw new SecurityException();
        }

        SessionResponse sessionResponse = new SessionResponse();

        sessionResponse.sessionToken = session.sessionHash;
        sessionResponse.developerKey = developerKeyDao.findByUser(user).key;
        sessionResponse.fullName = user.firstName + " " + user.lastName;
        sessionResponse.companyName = user.company.name;
        sessionResponse.role = user.role.authority.toString();

        return sessionResponse;

    }

    public SessionResponse getSession(){

        Session session = sessionService.getSession();
        User user = session.user;

        SessionResponse sessionResponse = new SessionResponse();

        sessionResponse.id = user.id;
        sessionResponse.sessionToken = session.sessionHash;
        sessionResponse.developerKey = developerKeyDao.findByUser(user).key;
        sessionResponse.fullName = user.firstName + " " + user.lastName;
        sessionResponse.companyName = user.company.name;
        sessionResponse.role = user.role.authority.toString();

        return sessionResponse;

    }

    public MessageResponse logout() {
        securityService.removeSession();
        return new MessageResponse();
    }

}
