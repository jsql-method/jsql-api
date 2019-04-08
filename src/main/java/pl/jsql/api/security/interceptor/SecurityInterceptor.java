package pl.jsql.api.security.interceptor

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter
import pl.jsql.api.enums.RoleTypeEnum
import pl.jsql.api.exceptions.SecurityException
import pl.jsql.api.model.user.Session
import pl.jsql.api.repo.SessionDao
import pl.jsql.api.security.annotation.Security

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.lang.annotation.Annotation

import static pl.jsql.api.enums.HttpMessageEnum.*

/**
 * Interceptor metod kontrolerów implementujący logikę zabezpieczeń
 * @author Dawid
 *
 */
public class  SecurityInterceptor extends HandlerInterceptorAdapter {

    private final static String AUTH_HEADER = "Session"

    @Autowired
    SessionDao sessionDao

    /**
     * Pobiera annotację Security metody wejściowej
     * @param handler
     * @return
     */
    protected Security getMethodAnnotation(HandlerMethod handler) {

        HandlerMethod handlerMethod = (HandlerMethod) handler

        Annotation annotation = handlerMethod.getMethodAnnotation(Security.class )

        if (annotation != null) {

            return (Security) annotation
        }

        return null
    }

    /**
     * Sprawdza czy aktualna sesja nie wygasła. Tylko dla administracji
     * @param request
     * @param session
     * @return
     */
    protected Boolean isSessionActive(HttpServletRequest request, Session session) {

        return session == null ? false : session.closedDate == null

    }

    /**
     * Sprawdza autoryzację logującego, rzuca wyjątek w razie niepowodzenia
     * @param request
     * @param handler
     */
    protected void authorize(HttpServletRequest request, HandlerMethod handler) throws Exception {

        Boolean isAuthorized = false
        Boolean sessionInactive = false

        String innerCause = ''

        Session session

        if (handler.getpublic class () == HandlerMethod.class ) {

            Security securityLevel = this.getMethodAnnotation(handler)

            if (securityLevel != null) {
                if (securityLevel.role() == RoleTypeEnum.PUBLIC && !securityLevel.requireActiveSession()) {
                    isAuthorized = true
                } else {

                    String tokenHeader = request.getHeader(AUTH_HEADER)

                    if (tokenHeader != null && !tokenHeader.isEmpty()) {

                        session = sessionDao.findBySessionHash(tokenHeader)

                        if (session != null) {

                            if (this.isSessionActive(request, session)) {

                                if (securityLevel.roles().length > 0) {

                                    for (RoleTypeEnum role : securityLevel.roles()) {

                                        if (session.user.role.authority == role || role == RoleTypeEnum.PUBLIC) {

                                            isAuthorized = true
                                        }
                                    }
                                } else {
                                    if (session.user.role.authority == securityLevel.role() || securityLevel.role() == RoleTypeEnum.PUBLIC) {

                                        isAuthorized = true

                                    }
                                }

                            } else {

                                sessionInactive = true

                            }
                        } else {
                            sessionInactive = true
                        }
                    } else {
                        throw new SecurityException(MISSING_HEADER.getCode() + "," + MISSING_HEADER.getDescription() + AUTH_HEADER)
                    }
                }
            }
        }

        if (sessionInactive) {
            throw new SecurityException(UNAUTHORIZED.getCode() + "," + UNAUTHORIZED.getDescription())
        }

        if (!isAuthorized) {
            throw new SecurityException(FORBIDDEN.getCode() + "," + FORBIDDEN.getDescription())
        }

    }

    /**
     * Wykonuje się przed metodą kontrolera
     */
    boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler)
            throws Exception {

        if (request.getHeader('origin')) {
            String origin = request.getHeader('origin')
            response.addHeader('Access-Control-Allow-Origin', origin)
            response.addHeader('Access-Control-Allow-Methods', 'GET, POST, PATCH, DELETE, OPTIONS')
            response.addHeader('Access-Control-Allow-Credentials', 'true')
            response.addHeader('Access-Control-Allow-Headers',
                    request.getHeader('Access-Control-Request-Headers'))
        }
        if (request.method == 'OPTIONS') {
            response.writer.print('OK')
            response.writer.flush()
            return
        }
        this.authorize(request, handler)

        return true
    }

    void postHandle(
            HttpServletRequest request, HttpServletResponse response,
            Object handler, ModelAndView modelAndView)
            throws Exception {
    }
}
