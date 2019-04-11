package pl.jsql.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.jsql.api.dto.request.LoginRequest;
import pl.jsql.api.model.user.Session;
import pl.jsql.api.model.user.User;
import pl.jsql.api.repo.SessionDao;
import pl.jsql.api.repo.UserDao;
import pl.jsql.api.utils.TokenUtil;

import javax.transaction.Transactional;
import java.util.Date;

@Transactional
@Service
public class SessionService {

    @Autowired
    private SessionDao sessionDao;

    @Autowired
    private UserDao userDao;

    public Boolean isLogged(String sessionHash) {
        Session session = sessionDao.findBySessionHash(sessionHash);
        return session != null && session.closedDate == null;

    }

    public void removeSession(String sessionHash) {
        sessionDao.updateSessionClosedDate(sessionHash, new Date());
    }


    public Session createSession(LoginRequest loginRequest) {

        User user = userDao.findByEmail(loginRequest.email);

        if (user == null) {
            throw new SecurityException();
        }

        Session session = new Session();
        session.user = user;
        session.sessionHash = TokenUtil.hash(loginRequest.email + new Date().getTime());
        session.createdDate = new Date();
        session.ipAddress = loginRequest.ipAddress;

        return sessionDao.save(session);

    }

}
