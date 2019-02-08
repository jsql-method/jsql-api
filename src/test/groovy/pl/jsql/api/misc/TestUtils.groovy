package pl.jsql.api.misc

import org.apache.commons.lang3.RandomStringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import pl.jsql.api.dto.UserRequest
import pl.jsql.api.enums.RoleTypeEnum
import pl.jsql.api.model.hashing.Application
import pl.jsql.api.model.user.User
import pl.jsql.api.repo.RoleDao
import pl.jsql.api.repo.UserDao
import pl.jsql.api.service.ApplicationService
import pl.jsql.api.service.AuthService

@Service
class TestUtils {
    @Autowired
    ApplicationService applicationService

    @Autowired
    UserDao userDao

    @Autowired
    RoleDao roleDao

    @Autowired
    AuthService authService

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
        applicationDeveloper.isFakeDeveloper = true
        applicationDeveloper.activated = true
        applicationDeveloper = userDao.save(applicationDeveloper)
        Application application = applicationService.createApplication(apiKey, companyAdmin, name, applicationDeveloper)

        applicationService.assignUserToAppMember(companyAdmin, application)

        applicationService.assignNewAppsToAppAdmins(currentUser, application)

        applicationService.initializeOptionsToApp(application)
    }
}
