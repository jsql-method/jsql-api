package pl.jsql.api.security.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.context.annotation.ScopedProxyMode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pl.jsql.api.enums.RoleTypeEnum
import pl.jsql.api.exceptions.SecurityException
import pl.jsql.api.model.user.Session
import pl.jsql.api.model.user.User
import pl.jsql.api.repo.SessionDao

import javax.servlet.http.HttpServletRequest

/**
 * Usługa działająca w zakresie requesta, dostarczająca informacji o aktualnej autoryzacji
 * @author Dawid
 *
 */
@Service
@Transactional
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
class SecurityService {

    @Autowired
    private HttpServletRequest request

    @Autowired
    SessionDao sessionDao

    /**
     * Zwraca aktualną rolę autoryzowanego
     * @return
     */
    RoleTypeEnum getCurrentRole() {
        User user = this.getCurrentAccount()

        if (user == null) {
            throw new SecurityException("ROLE_UNAUTHORIZED")
        }

        return user.role.authority

    }

    /**
     * Zwraca aktualne konto autoryzowanego
     * @return
     */
    User getCurrentAccount() {
        Session session = this.getAuthorizationToken()

        if (session != null && session.user != null) {

            return session.user
        }

        return null
    }

    /**
     * Zwraca kod autoryzacyjny aktualnie autoryzowanego
     * @return
     */
    private Session getAuthorizationToken() {

        String sessionToken = request.getHeader("session")

        if (sessionToken != null && !sessionToken.isEmpty()) {

            return sessionDao.findBySessionHash(sessionToken)

        }

        return null
    }

}
