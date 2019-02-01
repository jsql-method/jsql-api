package pl.jsql.api.service

import org.apache.commons.lang3.RandomStringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import pl.jsql.api.dto.UserRequest
import pl.jsql.api.enums.RoleTypeEnum
import pl.jsql.api.model.hashing.ApplicationMembers
import pl.jsql.api.model.user.Company
import pl.jsql.api.model.user.User
import pl.jsql.api.repo.ApplicationMembersDao
import pl.jsql.api.repo.PlansDao
import pl.jsql.api.repo.RoleDao
import pl.jsql.api.repo.UserDao
import pl.jsql.api.security.service.SecurityService

import javax.transaction.Transactional

import static pl.jsql.api.enums.HttpMessageEnum.*

@Transactional
@Service
class AppAdminService {

    @Autowired
    UserDao userDao

    @Autowired
    RoleDao roleDao

    @Autowired
    AuthService authService

    @Autowired
    ApplicationMembersDao applicationMembersDao

    @Autowired
    SecurityService securityService

    @Autowired
    PlansDao plansDao

    def register(UserRequest userRequest) {

        User companyAdmin = securityService.getCurrentAccount()
        List<ApplicationMembers> list = applicationMembersDao.findByUserQuery(companyAdmin)

        User appAdmin = userDao.findByEmail(userRequest.email)

        if (appAdmin == null) {

            int usersCount = userDao.countByCompany(companyAdmin.company)
            int allowedUsers = plansDao.findFirstByCompany(companyAdmin.company).plan.userQty

            if (allowedUsers <= usersCount) {

                return [code: DEVS_LIMIT_REACHED.getCode(), description: DEVS_LIMIT_REACHED.getDescription()]

            }

            userRequest.company = companyAdmin.company.id
            userRequest.role = "APP_ADMIN"
            userRequest.password = RandomStringUtils.randomAlphanumeric(10)

            authService.register(userRequest)
            appAdmin = userDao.findByEmail(userRequest.email)

        } else if (appAdmin.role.authority == RoleTypeEnum.APP_ADMIN) {

            return [code: USER_AND_ROLE_ALREADY_EXISTS.getCode(), description: USER_AND_ROLE_ALREADY_EXISTS.getDescription()]

        } else if (appAdmin.role.authority == RoleTypeEnum.COMPANY_ADMIN || appAdmin.role.authority == RoleTypeEnum.ADMIN) {

            return [code: FORBIDDEN.getCode(), description: FORBIDDEN.getDescription()]

        } else {

            appAdmin.role = roleDao.findByAuthority(RoleTypeEnum.APP_ADMIN)

        }

        for (ApplicationMembers applicationMembers : list) {

            ApplicationMembers appM = applicationMembersDao.findByUserAndAppQuery(appAdmin, applicationMembers.application)

            if (appM == null) {

                appM = new ApplicationMembers()
                appM.application = applicationMembers.application
                appM.member = appAdmin

                applicationMembersDao.save(appM)

            }
        }

        return [code: SUCCESS.getCode(), description: "Success"]
    }

    def getAll() {

        Company company = securityService.getCurrentAccount().company

        def list = userDao.findByCompanyAndRole(company, roleDao.findByAuthority(RoleTypeEnum.APP_ADMIN))
        def memberList = []

        for (User user : list) {

            memberList.add([id: user.id, email: user.email, firstName: user.firstName, lastName: user.lastName])

        }

        return [code: SUCCESS.getCode(), data: memberList]
    }

    def demote(UserRequest userRequest) {

        User appAdmin = userDao.findByEmail(userRequest.email)

        if (appAdmin == null) {
            return [code: NO_SUCH_APP_OR_MEMBER.getCode(), description: NO_SUCH_APP_OR_MEMBER.getDescription()]
        }

        appAdmin.role = roleDao.findByAuthority(RoleTypeEnum.APP_DEV)

        userDao.save(appAdmin)

        return [code: SUCCESS.getCode(), data: null]
    }

}