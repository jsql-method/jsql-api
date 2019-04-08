package pl.jsql.api.service

import com.fasterxml.jackson.core.type.TypeReference
import org.apache.commons.codec.digest.DigestUtils
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import pl.jsql.api.dto.request.LoginRequest
import pl.jsql.api.dto.request.UserRequest
import pl.jsql.api.enums.PlansEnum
import pl.jsql.api.enums.RoleTypeEnum
import pl.jsql.api.model.hashing.DeveloperKey
import pl.jsql.api.model.payment.Plan
import pl.jsql.api.model.user.Company
import pl.jsql.api.model.user.Session
import pl.jsql.api.model.user.User
import pl.jsql.api.repo.*
import pl.jsql.api.security.service.SecurityService
import pl.jsql.api.utils.HashingUtil

import javax.transaction.Transactional

import static pl.jsql.api.enums.HttpMessageEnum.*

@Transactional
@Service
public class  AuthService {

    @Autowired
    UserDao userDao

    @Autowired
    SessionDao sessionDao

    @Autowired
    MemberKeyDao memberKeyDao

    @Autowired
    PlansDao plansDao

    @Autowired
    CompanyDao companyDao

    @Autowired
    RoleDao roleDao

    @Autowired
    SettingsDao settingsDao

    @Autowired
    SecurityService securityService

    def register(UserRequest request) {

        def validation = validateRegister(request)

        if (validation.code != 200) {
            return validation
        }

        User user = createUser(request)

        if (request.role && request.role != "COMPANY_ADMIN" && request.role != "ADMIN") {

            user.role = roleDao.findByAuthority(RoleTypeEnum.valueOf(request.role))

        } else {

            user.role = roleDao.findByAuthority(RoleTypeEnum.COMPANY_ADMIN)

        }

        Company company

        if (request.company) {

            company = companyDao.findById(request.company).orElse(null)

            if (company == null) {

                return [code: COMPANY_NOT_FOUND.getCode(), description: COMPANY_NOT_FOUND.getDescription()]

            }


        } else {

            company = new Company()
            company.isLicensed = false
            company.creationDate = new Date()

            companyDao.save(company)
        }

        user.company = company

        userDao.save(user)

        this.createMemberKey(user)

        if (user.role.authority != RoleTypeEnum.COMPANY_ADMIN) {

            sendActivationMail(user, request.password, request.origin)

        } else {

            sendActivationMail(user, request.origin)
            createPlan(user, request)

        }

        return validation
    }

    def login(LoginRequest loginRequest) {

        def validateData = this.validateLogin(loginRequest)

        if (validateData != null) {
            return validateData
        }

        Session session = this.createSession(loginRequest)

        User user = session.user

        String memberKey = memberKeyDao.findByUser(user).key
        String fullName = user.firstName + " " + user.lastName
        String companyName = user.company.name

        def responseData = [
                role        : user.role.authority,
                sessionToken: session.sessionHash,
                memberKey   : memberKey,
                fullName    : fullName,
                companyName : companyName
        ]

        return [code: SUCCESS.getCode(), data: responseData]
    }

    Session createSession(LoginRequest loginRequest) {

        Session session = new Session()
        session.user = userDao.findByEmail(loginRequest.email)
        session.sessionHash = DigestUtils.sha256Hex(loginRequest.email + new Date().getTime())
        session.createdDate = new Date()
        session.ipAddress = loginRequest.ipAddress

        return sessionDao.save(session)

    }

    def validateLogin(LoginRequest loginRequest) {

        User user = userDao.findByEmail(loginRequest.email)


        if (loginRequest.email == null) {
            return [code: EMPTY_EMAIL.getCode(), description: EMPTY_EMAIL.getDescription()]
        }

        if (loginRequest.password == null) {
            return [code: EMPTY_PASSWORD.getCode(), description: EMPTY_PASSWORD.getDescription()]
        }

        if (user == null) {
            return [code: ACCOUNT_NOT_EXIST.getCode(), description: ACCOUNT_NOT_EXIST.getDescription()]
        }

        if (!DigestUtils.sha256Hex(loginRequest.password).equals(user.password)) {
            return [code: PASSWORD_NOT_MATCH.getCode(), description: PASSWORD_NOT_MATCH.getDescription()]
        }

        if (!user.enabled) {
            return [code: ACCOUNT_DISABLED.getCode(), description: ACCOUNT_DISABLED.getDescription()]
        }

        if (!user.activated) {
            return [code: ACCOUNT_NOT_ACTIVATED.getCode(), description: ACCOUNT_NOT_ACTIVATED.getDescription()]
        }

        if (user.blocked) {
            return [code: ACCOUNT_BLOCKED.getCode(), description: ACCOUNT_BLOCKED.getDescription()]
        }
        Plan plan = plansDao.findFirstByCompany(user.company)
        if (!plan.active) {
            return [code: NO_ACTIVE_PLAN.getCode(), description: NO_ACTIVE_PLAN.getDescription()]
        }

        return null

    }

    def validateRegister(UserRequest request) {

        if (request.password == null) {
            return [code: EMPTY_PASSWORD.getCode(), description: EMPTY_PASSWORD.getDescription()]
        }

        if (request.password.length() < 5) {
            return [code: PASSWORD_MIN_LENGTH.getCode(), description: PASSWORD_MIN_LENGTH.getDescription()]
        }
        if (request.email == null) {
            return [code: EMPTY_EMAIL.getCode(), description: EMPTY_EMAIL.getDescription()]
        }
        if (request.email.length() > 255) {
            return [code: EMAIL_TOO_LONG.getCode(), description: EMAIL_TOO_LONG.getDescription()]
        }
        if (userDao.findByEmail(request.email) != null) {
            return [code: EMAIL_USED.getCode(), description: EMAIL_USED.getDescription()]
        }

        return [code: SUCCESS.getCode(), description: "Success"]
    }

    def createMemberKey(User member) {

        DeveloperKey developerKey = new DeveloperKey()
        developerKey.user = member
        developerKey.key = HashingUtil.encode(member.role.toString() + member.email + member.firstName)

        memberKeyDao.save(developerKey)

    }

    private User createUser(UserRequest request) {
        User user = new User()
        user.password = DigestUtils.sha256Hex(request.password)
        user.email = request.email
        user.firstName = request.firstName
        user.lastName = request.lastName
        user.accountExpired = false
        user.accountLocked = false
        user.isProductionDeveloper = false
        user.passwordExpired = false
        user.registerDate = new Date()
        user.enabled = true
        user
    }

    def sendActivationMail(User user, String origin) {

        user.activationToken = DigestUtils.sha256Hex(user.email + new Date())
        user.activationDate = new Date()
        userDao.save(user)

        InputStream is = TypeReference.class .getResourceAsStream("/templates/verify.html")
        Document template = Jsoup.parse(is, "UTF-8", "")

        Element usrNickname = template.getElementById("usr_nickname")
        Element usrActivateBtn = template.getElementById("usr_activatebtn")

        usrNickname.text(user.firstName + " " + user.lastName)
        usrActivateBtn.attr("href", origin + "/auth/activate/" + user.activationToken)

        EmailService.sendEmail("Verify your account at JSQL!", template.html(), user.email)
    }

    def sendActivationMail(User user, String password, String origin) {

        user.activationToken = DigestUtils.sha256Hex(user.email + new Date())
        user.activationDate = new Date()
        userDao.save(user)

        InputStream is = TypeReference.class .getResourceAsStream("/templates/welcome_member.html")
        Document template = Jsoup.parse(is, "UTF-8", "")

        String titleEmail = "Welcome at JSQL!"

        Element usr_email = template.getElementById("usr_email")
        Element usr_password = template.getElementById("usr_password")
        Element usr_nickname = template.getElementById("usr_nickname")
        Element usrActivateBtn = template.getElementById("usr_activatebtn")

        usr_email.text("Your login: " + user.email)
        usr_password.text("Your password: " + password)
        usr_nickname.text(user.firstName + " " + user.lastName)
        usrActivateBtn.attr("href", origin + "/auth/activate/" + user.activationToken)

        EmailService.sendEmail(titleEmail, template.html(), user.email)
    }

    def createPlan(User user, UserRequest request) {
        Plan plan = new Plan()

        plan.company = user.company
        plan.activationDate = new Date()
        plan.active = true

        if (request.plan == null || request.plan.equalsIgnoreCase("STARTER")) {

            plan.plan = PlansEnum.STARTER
            plan.isTrial = false

        } else if (request.plan.equalsIgnoreCase("BUSINESS")) {

            plan.plan = PlansEnum.BUSINESS

        } else if (request.plan.equalsIgnoreCase("LARGE")) {

            plan.plan = PlansEnum.LARGE

        }

        plansDao.save(plan)

    }

    def logout() {
        securityService.removeSession();
    }

}
