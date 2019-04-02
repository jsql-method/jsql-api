package pl.jsql.api

import org.apache.commons.lang3.RandomStringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import pl.jsql.api.dto.request.LoginRequest
import pl.jsql.api.dto.request.UserRequest
import pl.jsql.api.enums.*
import pl.jsql.api.model.dict.DatabaseDialectDict
import pl.jsql.api.model.dict.EncodingDict
import pl.jsql.api.model.dict.Settings
import pl.jsql.api.model.hashing.Application
import pl.jsql.api.model.payment.Plans
import pl.jsql.api.model.user.Company
import pl.jsql.api.model.user.Role
import pl.jsql.api.model.user.User
import pl.jsql.api.repo.*
import pl.jsql.api.service.ApplicationService
import pl.jsql.api.service.AuthService
import pl.jsql.api.service.UserService

import javax.annotation.PostConstruct

@Component
class InitializeData {

    @Autowired
    AuthService authService

    @Autowired
    UserService userService

    @Autowired
    SettingsDao settingsDao

    @Autowired
    RoleDao roleDao

    @Autowired
    DatabaseDialectDictDao databaseDialectDao

    @Autowired
    EncodingDictDao encodingEnumDao

    @Autowired
    ApplicationService applicationService

    @Autowired
    CompanyDao companyDao

    @Autowired
    UserDao userDao

    @Autowired
    PlansDao plansDao

    def initRoles() {

        if (roleDao.count() > 0) {
            return
        }

        for (RoleTypeEnum roleType : RoleTypeEnum.values()) {
            Role role = new Role()
            role.authority = roleType
            roleDao.save(role)
        }

    }

    def initDatabaseDialects() {

        if (databaseDialectDao.count() > 0) {
            return
        }

        for (DatabaseDialectEnum dialect : DatabaseDialectEnum.values()) {
            DatabaseDialectDict databaseDialect = new DatabaseDialectDict()
            databaseDialect.name = dialect.toString()
            databaseDialect.value = dialect.toString()
            databaseDialectDao.save(databaseDialect)
        }

    }

    def initEncodings() {

        if (encodingEnumDao.count() > 0) {
            return
        }

        for (EncodingEnum encoding : EncodingEnum.values()) {
            EncodingDict encodingDict = new EncodingDict()
            encodingDict.name = encoding.toString()
            encodingDict.value = encoding.toString()
            encodingEnumDao.save(encodingDict)
        }

    }

    def initSettings() {

        for (SettingEnum s : SettingEnum.values()) {
            if (settingsDao.findByType(s) == null) {
                Settings setting = new Settings()
                setting.name = s.toString()
                setting.type = s
                setting.value = s.defaultValue
                settingsDao.save(setting)
            }
        }

    }

    def createFullCompanyAdmin() {

        String email = "test@test"
        String password = "test123"
        User user = userService.userDao.findByEmail(email)
        if (user != null) {
            Plans plan = plansDao.findFirstByCompany(user.company)
            plan.isTrial = false
            plan.active = true
            plan.plan = PlansEnum.LARGE
            plansDao.save(plan)
            return user
        }
        authService.register(new UserRequest(password, email, "John", "Doe"))
        user = userService.userDao.findByEmail(email)
        userService.activateAccount(user.activationToken)

        def loginResult = authService.login(new LoginRequest(email, password, "0.0.0.0"))
        Company company = user.company
        company.isLicensed = true
        companyDao.save(company)

        Plans plan = plansDao.findFirstByCompany(company)
        plan.isTrial = false
        plan.active = true
        plan.plan = PlansEnum.LARGE
        plansDao.save(plan)

        createApplication("Test application", user)

        return user

    }

    @PostConstruct
    def init() {

        initRoles()
        initDatabaseDialects()
        initEncodings()
        initSettings()
        createFullCompanyAdmin()

    }

    void createApplication(String name, User user) {
        User currentUser = user

        String apiKey = "==" + applicationService.generateApplication(name)
        User companyAdmin = currentUser

        if (currentUser.role.authority == RoleTypeEnum.APP_ADMIN) {

            companyAdmin = userDao.findByCompanyAndRole(currentUser.company, roleDao.findByAuthority(RoleTypeEnum.COMPANY_ADMIN)).get(0)
        }


        UserRequest userRequest = new UserRequest()
        userRequest.email = name + "@applicationDeveloper"
        userRequest.firstName = "application"
        userRequest.lastName = "developer"
        userRequest.password = RandomStringUtils.randomAlphanumeric(10)
        userRequest.company = companyAdmin.company.id
        userRequest.role = RoleTypeEnum.APP_DEV.toString()
        authService.register(userRequest)

        User applicationDeveloper = userDao.findByEmail(name + "@applicationDeveloper")
        applicationDeveloper.isProductionDeveloper = true
        applicationDeveloper.activated = true
        applicationDeveloper = userDao.save(applicationDeveloper)

        Application application = applicationService.createApplication(apiKey, companyAdmin, name, applicationDeveloper)

        applicationService.assignUserToAppMember(companyAdmin, application)

        applicationService.assignNewAppsToAppAdmins(currentUser, application)

        applicationService.initializeOptionsToApp(application)
    }


}
