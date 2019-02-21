package pl.jsql.api.service

import org.apache.commons.lang3.RandomStringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pl.jsql.api.dto.UserRequest
import pl.jsql.api.enums.RoleTypeEnum
import pl.jsql.api.model.hashing.Application
import pl.jsql.api.model.hashing.ApplicationMembers
import pl.jsql.api.model.hashing.MemberKey
import pl.jsql.api.model.hashing.Options
import pl.jsql.api.model.user.User
import pl.jsql.api.repo.*
import pl.jsql.api.security.service.SecurityService
import pl.jsql.api.utils.TokenUtil

import static pl.jsql.api.enums.HttpMessageEnum.APPS_LIMIT_REACHED
import static pl.jsql.api.enums.HttpMessageEnum.APP_ALREADY_EXISTS
import static pl.jsql.api.enums.HttpMessageEnum.NO_SUCH_APP_OR_MEMBER
import static pl.jsql.api.enums.HttpMessageEnum.SUCCESS

@Transactional
@Service
class ApplicationService {

    @Autowired
    ApplicationDao applicationDao

    @Autowired
    OptionsDao optionsDao

    @Autowired
    UserDao userDao

    @Autowired
    DatabaseDialectDictDao databaseDialectDao

    @Autowired
    ApplicationLanguageDictDao applicationLanguageDao

    @Autowired
    EncodingDictDao encodingEnumDao

    @Autowired
    ApplicationMembersDao applicationMembersDao

    @Autowired
    RoleDao roleDao

    @Autowired
    SecurityService securityService

    @Autowired
    PlansDao plansDao

    @Autowired
    AuthService authService

    @Autowired
    MemberKeyDao memberKeyDao

    def getAll() {
        User currentUser = securityService.getCurrentAccount()
        def data = []
        //Pobieranie danych w zależności od roli
        if (currentUser.role.authority == RoleTypeEnum.APP_DEV) {

            List<ApplicationMembers> list = applicationMembersDao.findByUserQuery(currentUser)

            for (ApplicationMembers appMember : list) {
                MemberKey key = memberKeyDao.findByUser(appMember.application.developer)
                data << [
                        id          : appMember.application.id,
                        apiKey      : appMember.application.apiKey,
                        name        : appMember.application.name,
                        developerKey: key != null ? key.key : null
                ]

            }
        } else if (currentUser.role.authority == RoleTypeEnum.ADMIN) {
            data = applicationDao.findAll()
        } else {

            User companyAdmin = currentUser

            if (currentUser.role.authority == RoleTypeEnum.APP_ADMIN) {

                companyAdmin = userDao.findByCompanyAndRole(currentUser.company, roleDao.findByAuthority(RoleTypeEnum.COMPANY_ADMIN)).first()

            }
            List<Application> list = applicationDao.findByUserQuery(companyAdmin)
            for (Application app : list) {
                MemberKey key = memberKeyDao.findByUser(app.developer)
                data << [
                        id          : app.id,
                        apiKey      : app.apiKey,
                        name        : app.name,
                        developerKey: key != null ? key.key : null
                ]

            }
        }

        return [code: SUCCESS.getCode(), data: data]
    }

    def getById(Long id) {

        if (!applicationDao.existsById(id)) {

            return [code: NO_SUCH_APP_OR_MEMBER.getCode(), description: NO_SUCH_APP_OR_MEMBER.getDescription()]

        }

        User currentUser = securityService.getCurrentAccount()
        Application currentApp = applicationDao.findById(id).get()
        MemberKey key = memberKeyDao.findByUser(currentApp.developer)
        def data = []

        if (currentUser.role.authority == RoleTypeEnum.APP_DEV) {

            ApplicationMembers appMember = applicationMembersDao.findByUserAndAppQuery(currentUser, currentApp)

            if (appMember == null) {

                return [code: NO_SUCH_APP_OR_MEMBER.getCode(), description: NO_SUCH_APP_OR_MEMBER.getDescription()]

            } else {

                data << [
                        id          : appMember.application.id,
                        apiKey      : appMember.application.apiKey,
                        name        : appMember.application.name,
                        developerKey: key != null ? key.key : null
                ]

            }

        } else {

            if (currentApp.user.company != currentUser.company) {

                return [code: NO_SUCH_APP_OR_MEMBER.getCode(), description: NO_SUCH_APP_OR_MEMBER.getDescription()]

            }

            data << [
                    id          : currentApp.id,
                    apiKey      : currentApp.apiKey,
                    name        : currentApp.name,
                    developerKey: key != null ? key.key : null
            ]

        }

        return [code: SUCCESS.getCode(), data: data]
    }

    def create(String name) {

        User currentUser = securityService.getCurrentAccount()
        User companyAdmin = currentUser
        Application app = applicationDao.findByNameAndCompany(name, currentUser.company)
        if (app != null && !app.active) {
            app.active = true
            User applicationDeveloper = createFakeDeveloper(name, companyAdmin)
            app.developer = applicationDeveloper

            applicationDao.save(app)
            
            return [code: SUCCESS.getCode(), data: null]
        } else if (app != null) {
            return [code: APP_ALREADY_EXISTS.getCode(), description: APP_ALREADY_EXISTS.getDescription()]
        }


        if (currentUser.role.authority == RoleTypeEnum.APP_ADMIN) {

            companyAdmin = userDao.findByCompanyAndRole(currentUser.company, roleDao.findByAuthority(RoleTypeEnum.COMPANY_ADMIN)).get(0)
        }
        int activeApplications = applicationDao.countByUser(companyAdmin)
        int allowedApplications = plansDao.findFirstByCompany(companyAdmin.company).plan.appQty

        if (allowedApplications <= activeApplications) {

            return [code: APPS_LIMIT_REACHED.getCode(), description: APPS_LIMIT_REACHED.getDescription()]

        }

        User applicationDeveloper = createFakeDeveloper(name, companyAdmin)

        String apiKey = "==" + this.generateApplication(name)

        Application application = createApplication(apiKey, companyAdmin, name, applicationDeveloper)

        assignUserToAppMember(companyAdmin, application)

        assignNewAppsToAppAdmins(currentUser, application)

        initializeOptionsToApp(application)

        return [code: SUCCESS.getCode(), data: null]

    }

    private User createFakeDeveloper(String name, User companyAdmin) {
        UserRequest userRequest = new UserRequest()
        userRequest.email = name + "@applicationDeveloper"
        userRequest.firstName = "application"
        userRequest.lastName = "developer"
        userRequest.password = RandomStringUtils.randomAlphanumeric(10)
        userRequest.company = companyAdmin.company.id
        userRequest.role = RoleTypeEnum.APP_DEV.toString()
        authService.register(userRequest)

        User applicationDeveloper = userDao.findByEmail(name + "@applicationDeveloper")
        applicationDeveloper.isFakeDeveloper = true
        applicationDeveloper.activated = true
        applicationDeveloper = userDao.save(applicationDeveloper)
        applicationDeveloper
    }

    def disable(Long id) {

        if (!applicationDao.existsById(id)) {

            return [code: NO_SUCH_APP_OR_MEMBER.getCode(), description: NO_SUCH_APP_OR_MEMBER.getDescription()]

        }

        Application application = applicationDao.findById(id).get()

        application.active = false

        memberKeyDao.deleteByUser(application.developer)
        applicationMembersDao.deleteAllByUser(application.developer)
        userDao.delete(application.developer)
        application.developer = null
        applicationDao.save(application)

        return [code: SUCCESS.getCode(), data: null]

    }

    void assignNewAppsToAppAdmins(User user, Application newApp) {

        List<User> appAdmins = userDao.findByCompanyAndRole(user.company, roleDao.findByAuthority(RoleTypeEnum.APP_ADMIN))

        for (User appadmin : appAdmins) {

            assignUserToAppMember(appadmin, newApp)

        }
    }

    void assignUserToAppMember(User user, Application app) {
        ApplicationMembers applicationMembers = applicationMembersDao.findByUserAndAppQuery(user, app)
        if (applicationMembers == null) {
            applicationMembers = new ApplicationMembers()
            applicationMembers.application = app
            applicationMembers.member = user

            applicationMembersDao.save(applicationMembers)
        }
    }

    def generateApplication(String name) {

        int iterationCount = 0
        for (; ;) {

            String apiKey = TokenUtil.generateToken(name)

            Application application = applicationDao.findByApiKey(apiKey)

            if (application == null) {
                return apiKey
            }

            iterationCount++

            if (iterationCount > 100) {
                throw new Exception('UNABLE_GENERATE_API_KEY')
            }

        }
    }

    void initializeOptionsToApp(Application application) {
        Options options = new Options()
        options.application = application
        options.encodeQuery = true
        options.encodingAlgorithm = encodingEnumDao.findByName('MD5')
        options.isSalt = false
        options.salt = ""
        options.saltBefore = true
        options.saltAfter = false
        options.saltRandomize = false
        options.hashLengthLikeQuery = true
        options.hashMinLength = 10
        options.hashMaxLength = 50
        options.removeQueriesAfterBuild = true

        options.databaseDialect = databaseDialectDao.findByName('POSTGRES')
        options.applicationLanguage = applicationLanguageDao.findByName('JAVA')

        optionsDao.save(options)
    }

    Application createApplication(String apiKey, User user, String name, User dev) {
        Application application = applicationDao.findByNameAndCompany(name, user.company)

        if (application == null) {

            application = new Application()
            application.apiKey = apiKey
            application.user = user
            application.name = name
            application.developer = dev

        }

        application.active = true
        applicationDao.save(application)
        return application
    }

}
