package pl.jsql.api.service.admin

import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.lang3.RandomStringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pl.jsql.api.dto.request.UserRequest
import pl.jsql.api.enums.RoleTypeEnum
import pl.jsql.api.model.hashing.Application
import pl.jsql.api.model.hashing.ApplicationMembers
import pl.jsql.api.model.user.Company
import pl.jsql.api.model.user.User
import pl.jsql.api.repo.ApplicationMembersDao
import pl.jsql.api.repo.PlansDao
import pl.jsql.api.repo.RoleDao
import pl.jsql.api.repo.UserDao
import pl.jsql.api.security.service.SecurityService
import pl.jsql.api.service.AuthService

import static pl.jsql.api.enums.HttpMessageEnum.*

@Transactional
@Service
public class  AppDevService {

    @Autowired
    AuthService authService

    @Autowired
    UserDao userDao

    @Autowired
    RoleDao roleDao

    @Autowired
    ApplicationMembersDao applicationMembersDao

    @Autowired
    SecurityService securityService

    @Autowired
    PlansDao plansDao

    def getAll() {

        Company company = securityService.getCurrentAccount().company

        def list = userDao.findyByCompanyAndRoleWithoutFake(company, roleDao.findByAuthority(RoleTypeEnum.APP_DEV))
        def memberList = []

        for (User user : list) {

            memberList.add([id: user.id, email: user.email, firstName: user.firstName, lastName: user.lastName])

        }

        return [code: SUCCESS.getCode(), data: memberList]
    }

    def register(UserRequest userRequest) {

        User user = securityService.getCurrentAccount()

        int usersCount = userDao.countByCompany(user.company)
        int allowedUsers = plansDao.findFirstByCompany(user.company).plan.userQty

        if (allowedUsers <= usersCount) {

            return [code: DEVS_LIMIT_REACHED.getCode(), description: DEVS_LIMIT_REACHED.getDescription()]

        }

        userRequest.password = RandomStringUtils.randomAlphanumeric(10)

        userRequest.company = user.company.id

        userRequest.role = RoleTypeEnum.APP_DEV.toString()

        def result = authService.register(userRequest)

        if (result.code != 200) {
            return result
        }

        return [code: SUCCESS.getCode(), data: null]
    }

    def delete(Long id) {
        User user = userDao.findById(id).orElse(null)

        if (user == null) {

            return [code: ACCOUNT_NOT_EXIST.getCode(), description: ACCOUNT_NOT_EXIST.getDescription()]

        }

        applicationMembersDao.clearJoinsByUser(user)
        applicationMembersDao.deleteAllByUser(user)

        user.email = DigestUtils.md5Hex(user.email + System.currentTimeMillis())
        user.firstName = DigestUtils.md5Hex(user.firstName + System.currentTimeMillis())
        user.lastName = DigestUtils.md5Hex(user.lastName + System.currentTimeMillis())
        user.lastName = DigestUtils.md5Hex(user.password + System.currentTimeMillis())
        user.enabled = false
        user.activated = false
        user.company = null

        userDao.save(user)

        return [code: SUCCESS.getCode(), data: null]
    }

    def unassignMember(User user, Application app) {
        if (user == null || app == null) {
            return [code: NO_SUCH_APP_OR_MEMBER.getCode(), description: NO_SUCH_APP_OR_MEMBER.getDescription()]
        }

        ApplicationMembers appMember = applicationMembersDao.findByUserAndAppQuery(user, app)

        //this if clears record links, saves empty record and removes it from database
        if (appMember != null) {
            appMember.application = null
            appMember.member = null

            applicationMembersDao.save(appMember)
            applicationMembersDao.delete(appMember)
        }
        return [code: SUCCESS.getCode(), data: null]

    }

    def unassignAllAppsFromMember(User user) {
        List<Application> list = applicationMembersDao.findByUserQuery(user).application

        for (Application application : list) {
            unassignMember(user, application)
        }

    }
}
