package pl.jsql.api.misc

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import pl.jsql.api.enums.RoleTypeEnum
import pl.jsql.api.model.hashing.Application
import pl.jsql.api.model.user.User
import pl.jsql.api.repo.RoleDao
import pl.jsql.api.repo.UserDao
import pl.jsql.api.service.ApplicationService

@Service
class TestUtils {
    @Autowired
    ApplicationService applicationService

    @Autowired
    UserDao userDao

    @Autowired
    RoleDao roleDao

    void createApplication(String name, User user) {
        User currentUser = user

        String apiKey = "==" + applicationService.generateApplication(name)
        User companyAdmin = currentUser

        if (currentUser.role.authority == RoleTypeEnum.APP_ADMIN) {

            companyAdmin = userDao.findByCompanyAndRole(currentUser.company, roleDao.findByAuthority(RoleTypeEnum.COMPANY_ADMIN)).get(0)
        }

        Application application = applicationService.createApplication(apiKey, companyAdmin, name)

        applicationService.assignUserToAppMember(companyAdmin, application)

        applicationService.assignNewAppsToAppAdmins(currentUser, application)

        applicationService.initializeOptionsToApp(application)
    }
}
