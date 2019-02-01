package pl.jsql.api.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pl.jsql.api.dto.OptionsRequest
import pl.jsql.api.enums.RoleTypeEnum
import pl.jsql.api.model.hashing.Application
import pl.jsql.api.model.hashing.ApplicationMembers
import pl.jsql.api.model.hashing.Options
import pl.jsql.api.model.user.User
import pl.jsql.api.repo.*
import pl.jsql.api.security.service.SecurityService

import static pl.jsql.api.enums.HttpMessageEnum.*

@Transactional
@Service
class OptionsService {

    @Autowired
    ApplicationDao applicationDao

    @Autowired
    OptionsDao optionsDao

    @Autowired
    DatabaseDialectDictDao databaseDialectDao

    @Autowired
    ApplicationLanguageDictDao applicationLanguageDao

    @Autowired
    EncodingDictDao encodingEnumDao

    @Autowired
    ApplicationMembersDao applicationMembersDao

    @Autowired
    UserDao userDao

    @Autowired
    RoleDao roleDao

    @Autowired
    SecurityService securityService

    def getAll() {

        User currentUser = securityService.getCurrentAccount()

        def list = []
        List<Application> applicationList = new ArrayList<>()

        if (currentUser.role.authority == RoleTypeEnum.APP_DEV) {

            List<ApplicationMembers> appMembers = applicationMembersDao.findByUserQuery(currentUser)

            for (ApplicationMembers am : appMembers) {

                applicationList.add(am.application)

            }

        } else {

            User companyAdmin = currentUser

            if (currentUser.role.authority == RoleTypeEnum.APP_ADMIN) {

                companyAdmin = userDao.findByCompanyAndRole(currentUser.company, roleDao.findByAuthority(RoleTypeEnum.COMPANY_ADMIN)).get(0)

            }

            applicationList = applicationDao.findByUserQuery(companyAdmin)
        }

        for (Application application : applicationList) {

            Options appOptions = optionsDao.findByApplication(application)

            list << [
                    application: application,
                    options    : [
                            applicationId          : application.id,
                            encodeQuery            : appOptions.encodeQuery,
                            encodingAlgorithm      : appOptions.encodingAlgorithm.value,
                            isSalt                 : appOptions.isSalt,
                            salt                   : appOptions.salt,
                            saltBefore             : appOptions.saltBefore,
                            saltAfter              : appOptions.saltAfter,
                            saltRandomize          : appOptions.saltRandomize,
                            hashLengthLikeQuery    : appOptions.hashLengthLikeQuery,
                            hashMinLength          : appOptions.hashMinLength,
                            hashMaxLenght          : appOptions.hashMaxLength,
                            removeQueriesAfterBuild: appOptions.removeQueriesAfterBuild,
                            databaseDialect        : appOptions.databaseDialect.value,
                            applicationLanguage    : appOptions.applicationLanguage.value,
                            allowedPlainQueries    : appOptions.allowedPlainQueries,
                            prod                   : appOptions.prod
                    ]
            ]

        }

        return [code: SUCCESS.getCode(), data: list]
    }

    def getByAppId(Long id) {

        Application currentApp = applicationDao.findById(id).orElse(null)

        if (currentApp == null) {

            return [code: NO_SUCH_APP_OR_MEMBER.getCode(), description: NO_SUCH_APP_OR_MEMBER.getDescription()]

        }

        User currentUser = securityService.getCurrentAccount()

        Application application

        if (currentUser.role.authority == RoleTypeEnum.APP_DEV) {

            ApplicationMembers appMember = applicationMembersDao.findByUserAndAppQuery(currentUser, currentApp)

            if (appMember == null) {

                return [code: NO_SUCH_APP_OR_MEMBER.getCode(), description: NO_SUCH_APP_OR_MEMBER.getDescription()]

            }

            application = appMember.application

        } else {

            User companyAdmin = currentUser

            if (currentUser.role.authority == RoleTypeEnum.APP_ADMIN) {

                companyAdmin = userDao.findByCompanyAndRole(currentUser.company, roleDao.findByAuthority(RoleTypeEnum.COMPANY_ADMIN)).get(0)

            }

            application = applicationDao.findByUserAndIdQuery(companyAdmin, id)

        }


        Options appOptions = optionsDao.findByApplication(application)

        return [
                encodeQuery            : appOptions.encodeQuery,
                encodingAlgorithm      : appOptions.encodingAlgorithm.value,
                isSalt                 : appOptions.isSalt,
                salt                   : appOptions.salt,
                saltBefore             : appOptions.saltBefore,
                saltAfter              : appOptions.saltAfter,
                saltRandomize          : appOptions.saltRandomize,
                hashLengthLikeQuery    : appOptions.hashLengthLikeQuery,
                hashMinLength          : appOptions.hashMinLength,
                hashMaxLenght          : appOptions.hashMaxLength,
                removeQueriesAfterBuild: appOptions.removeQueriesAfterBuild,
                databaseDialect        : appOptions.databaseDialect.value,
                applicationLanguage    : appOptions.applicationLanguage.value,
                allowedPlainQueries    : appOptions.allowedPlainQueries,
                prod                   : appOptions.prod
        ]
    }

    def update(Long id, OptionsRequest optionsRequest) {

        User currentUser = securityService.getCurrentAccount()
        Application application = applicationDao.findById(id).orElse(null)

        if (application == null || !application.active) {

            return [code: NO_SUCH_APP_OR_MEMBER.getCode(), description: NO_SUCH_APP_OR_MEMBER.getDescription()]

        }

        if (application.user.company != currentUser.company) {

            return [code: FORBIDDEN.getCode(), description: FORBIDDEN.getDescription()]

        }

        Options appOptions = optionsDao.findByApplication(application)

        appOptions.encodeQuery = optionsRequest.encodeQuery == null ? appOptions.encodeQuery : optionsRequest.encodeQuery

        def encodingAlgorithmValue = encodingEnumDao.findByValue(optionsRequest.encodingAlgorithm)

        if (encodingAlgorithmValue) {
            appOptions.encodingAlgorithm = encodingAlgorithmValue
        }

        appOptions.isSalt = optionsRequest.isSalt == null ? appOptions.isSalt : optionsRequest.isSalt
        appOptions.salt = optionsRequest.salt == null ? appOptions.salt : optionsRequest.salt
        appOptions.saltBefore = optionsRequest.saltBefore == null ? appOptions.saltBefore : optionsRequest.saltBefore
        appOptions.saltAfter = optionsRequest.saltAfter == null ? appOptions.saltAfter : optionsRequest.saltAfter
        appOptions.saltRandomize = optionsRequest.saltRandomize == null ? appOptions.saltRandomize : optionsRequest.saltRandomize
        appOptions.hashLengthLikeQuery = optionsRequest.hashLengthLikeQuery == null ? appOptions.hashLengthLikeQuery : optionsRequest.hashLengthLikeQuery
        appOptions.hashMinLength = optionsRequest.hashMinLength == null ? appOptions.hashMinLength : optionsRequest.hashMinLength
        appOptions.hashMaxLength = optionsRequest.hashMaxLength == null ? appOptions.hashMaxLength : optionsRequest.hashMaxLength
        appOptions.removeQueriesAfterBuild = optionsRequest.removeQueriesAfterBuild == null ? appOptions.removeQueriesAfterBuild : optionsRequest.removeQueriesAfterBuild
        appOptions.allowedPlainQueries = optionsRequest.allowedPlainQueries == null ? appOptions.allowedPlainQueries : optionsRequest.allowedPlainQueries
        appOptions.prod = optionsRequest.prod == null ? appOptions.prod : optionsRequest.prod

        def databaseDialectValue = databaseDialectDao.findByValue(optionsRequest.databaseDialect)

        if (databaseDialectValue) {
            appOptions.setDatabaseDialect(databaseDialectValue)
        }

        def applicationLanguageValue = applicationLanguageDao.findByValue(optionsRequest.applicationLanguage)

        if (applicationLanguageValue) {
            appOptions.setApplicationLanguage(applicationLanguageValue)
        }

        return [code: SUCCESS.getCode(), data: null]
    }

    def getValues() {

        def list = []
        list << [
                encodingAlgorithmValues  : encodingEnumDao.findAll(),
                databaseDialectValues    : databaseDialectDao.findAll(),
                applicationLanguageValues: applicationLanguageDao.findAll()
        ]

        return [code: SUCCESS.getCode(), data: list]
    }

}
