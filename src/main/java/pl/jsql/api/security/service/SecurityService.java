package pl.jsql.api.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.jsql.api.enums.RoleTypeEnum;
import pl.jsql.api.exceptions.UnauthorizedException;
import pl.jsql.api.model.user.Session;
import pl.jsql.api.model.user.User;
import pl.jsql.api.repo.RoleDao;
import pl.jsql.api.repo.SessionDao;
import pl.jsql.api.repo.UserDao;
import pl.jsql.api.security.interceptor.HashingSecurityInterceptor;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * Usługa działająca w zakresie requesta, dostarczająca informacji o aktualnej autoryzacji
 *
 * @author Dawid
 */
@Service
@Transactional
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class SecurityService {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private SessionDao sessionDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private RoleDao roleDao;

    /**
     * Zwraca aktualną rolę autoryzowanego
     *
     * @return
     */
    public RoleTypeEnum getCurrentRole() {
        User user = this.getCurrentAccount();

        if (user == null) {
            throw new UnauthorizedException();
        }

        return user.role.authority;

    }

    /**
     * Zwraca aktualne konto autoryzowanego
     *
     * @return
     */
    public User getCurrentAccount() {
        String sessionToken = this.getAuthorizationToken();

        if (sessionToken != null) {

            Session session = sessionDao.findBySessionHash(sessionToken);

            if (session != null && session.user != null) {
                return session.user;
            }

        }

        return null;
    }

    public User getCompanyAdmin() {

        User user = this.getCurrentAccount();

        if (user.role.authority != RoleTypeEnum.COMPANY_ADMIN) {
            return userDao.findCompanyAdmin(user.company);
        }

        return user;

    }

    /**
     * Zwraca kod autoryzacyjny aktualnie autoryzowanego
     *
     * @return
     */
    public String getAuthorizationToken() {
        return request.getHeader("session");
    }

    public String getApiKey() {
        return request.getHeader(HashingSecurityInterceptor.API_KEY_HEADER);
    }


    public String getDevKey() {
        return this.getMemberKey();
    }

    public String getMemberKey() {
        return request.getHeader(HashingSecurityInterceptor.DEV_KEY_HEADER);
    }

    public Boolean isLogged() {

        String sessionToken = this.getAuthorizationToken();

        if (sessionToken == null) {
            return false;
        }

        Session session = sessionDao.findBySessionHash(sessionToken);

        return session != null && session.closedDate == null;

    }

    public void removeSession() {

        String sessionToken = this.getAuthorizationToken();

        if (sessionToken == null) {
            return;
        }

        Session session = sessionDao.findBySessionHash(sessionToken);

        if (session == null) {
            return;
        }

        session.closedDate = new Date();

        sessionDao.save(session);

    }

}
