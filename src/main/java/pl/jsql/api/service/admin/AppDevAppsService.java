package pl.jsql.api.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import pl.jsql.api.dto.request.MemberAssignRequest
import pl.jsql.api.model.hashing.Application
import pl.jsql.api.model.hashing.ApplicationDevelopers
import pl.jsql.api.model.user.User
import pl.jsql.api.repo.ApplicationDao
import pl.jsql.api.repo.ApplicationMembersDao
import pl.jsql.api.repo.UserDao

import javax.transaction.Transactional

import static pl.jsql.api.enums.HttpMessageEnum.*

@Transactional
@Service
public class  AppDevAppsService {

    @Autowired
    ApplicationDao applicationDao

    @Autowired
    UserDao userDao

    @Autowired
    ApplicationMembersDao applicationMembersDao

    def assign(MemberAssignRequest memberAssignRequest) {

        if (memberAssignRequest.member == null || memberAssignRequest.application == null) {

            return [code: BAD_REQUEST.getCode(), description: BAD_REQUEST.getDescription()]

        }

        Application application = applicationDao.findById(memberAssignRequest.application).orElse(null)
        User member = userDao.findById(memberAssignRequest.member).orElse(null)

        if (application == null || member == null) {

            return [code: NO_SUCH_APP_OR_MEMBER.getCode(), description: NO_SUCH_APP_OR_MEMBER.getDescription()]

        }

        ApplicationDevelopers applicationDevelopers = applicationMembersDao.findByUserAndAppQuery(member, application)

        if (applicationDevelopers == null) {

            applicationDevelopers = new ApplicationDevelopers()
            applicationDevelopers.application = application
            applicationDevelopers.member = member

            applicationMembersDao.save(applicationDevelopers)
        }

        return [code: SUCCESS.getCode(), data: null]
    }

    def getAll(Long id) {

        User user = userDao.findById(id).orElse(null)

        if (user == null) {

            return [code: ACCOUNT_NOT_EXIST.getCode(), description: ACCOUNT_NOT_EXIST.getDescription()]

        }

        return [code: SUCCESS.getCode(), data: [
                userEmail    : user.email,
                applicationId: applicationMembersDao.findByApplicationActive(user).application.id
        ]]
    }

    def unassign(MemberAssignRequest memberAssignRequest) {

        User user = userDao.findById(memberAssignRequest.member).orElse(null)
        Application app = applicationDao.findById(memberAssignRequest.application).orElse(null)

        if (user == null || app == null) {

            return [code: NO_SUCH_APP_OR_MEMBER.getCode(), description: NO_SUCH_APP_OR_MEMBER.getDescription()]

        }

        applicationMembersDao.clearJoinsByUserAndApp(user, app)
        applicationMembersDao.deleteByUserAndApp(user, app)

        return [code: SUCCESS.getCode(), data: null]
    }
}