package pl.jsql.api.service.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.jsql.api.dto.request.MemberAssignRequest;
import pl.jsql.api.dto.response.AppDeveloperApplicationsResponse;
import pl.jsql.api.dto.response.MessageResponse;
import pl.jsql.api.model.hashing.Application;
import pl.jsql.api.model.hashing.ApplicationDevelopers;
import pl.jsql.api.model.user.User;
import pl.jsql.api.repo.ApplicationDao;
import pl.jsql.api.repo.ApplicationDevelopersDao;
import pl.jsql.api.repo.UserDao;

import javax.transaction.Transactional;

@Transactional
@Service
public class AppDevAppsService {

    @Autowired
    private ApplicationDao applicationDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private ApplicationDevelopersDao applicationDevelopersDao;

    public MessageResponse assign(MemberAssignRequest memberAssignRequest) {

        Application application = applicationDao.findById(memberAssignRequest.application).orElse(null);
        User developer = userDao.findById(memberAssignRequest.member).orElse(null);

        if (application == null || developer == null) {
            return new MessageResponse("no_such_developer");
        }

        ApplicationDevelopers applicationDevelopers = applicationDevelopersDao.findByUserAndAppQuery(developer, application);

        if (applicationDevelopers == null) {

            applicationDevelopers = new ApplicationDevelopers();
            applicationDevelopers.application = application;
            applicationDevelopers.developer = developer;

            applicationDevelopersDao.save(applicationDevelopers);
        }

        return new MessageResponse();

    }

    public AppDeveloperApplicationsResponse getById(Long id) {

        User user = userDao.findById(id).orElse(null);

        if (user == null) {
            return null;
        }

        AppDeveloperApplicationsResponse appDeveloperApplicationsResponse = new AppDeveloperApplicationsResponse();
        appDeveloperApplicationsResponse.email = user.email;
        appDeveloperApplicationsResponse.applications = applicationDevelopersDao.findByApplicationActive(user);

        return appDeveloperApplicationsResponse;

    }

    public MessageResponse unassign(MemberAssignRequest memberAssignRequest) {

        User user = userDao.findById(memberAssignRequest.member).orElse(null);
        Application app = applicationDao.findById(memberAssignRequest.application).orElse(null);

        if (user == null || app == null) {
            return new MessageResponse("no_such_developer");
        }

        applicationDevelopersDao.clearJoinsByUserAndApp(user, app);
        applicationDevelopersDao.deleteByUserAndApp(user, app);

        return new MessageResponse();

    }
}