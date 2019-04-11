package pl.jsql.api.security.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import pl.jsql.api.enums.RoleTypeEnum;
import pl.jsql.api.exceptions.UnauthorizedException;
import pl.jsql.api.model.user.Session;
import pl.jsql.api.repo.SessionDao;
import pl.jsql.api.security.annotation.Security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;

/**
 * Interceptor metod kontrolerów implementujący logikę zabezpieczeń
 * @author Dawid
 *
 */
public class  SecurityInterceptor extends HandlerInterceptorAdapter {

    private final static String AUTH_HEADER = "Session";

    @Autowired
    private SessionDao sessionDao;

    /**
     * Pobiera annotację Security metody wejściowej
     */
    private Security getMethodAnnotation(HandlerMethod handler) {

        HandlerMethod handlerMethod = (HandlerMethod) handler;

        Annotation annotation = handlerMethod.getMethodAnnotation(Security.class );

        if (annotation != null) {

            return (Security) annotation;
        }

        return null;
    }

    /**
     * Sprawdza czy aktualna sesja nie wygasła. Tylko dla administracji
     */
    private Boolean isSessionActive(Session session) {
        return session != null && session.closedDate == null;
    }

    /**
     * Sprawdza autoryzację logującego, rzuca wyjątek w razie niepowodzenia
     */
    private void authorize(HttpServletRequest request, HandlerMethod handler) throws Exception {

        Boolean isAuthorized = false;
        Boolean sessionInactive = false;

        String innerCause = "";

        Session session;

        if (handler.getClass() == HandlerMethod.class ) {

            Security securityLevel = this.getMethodAnnotation(handler);

            if (securityLevel != null) {
                if (securityLevel.role() == RoleTypeEnum.PUBLIC && !securityLevel.requireActiveSession()) {
                    isAuthorized = true;
                } else {

                    String tokenHeader = request.getHeader(AUTH_HEADER);

                    if (tokenHeader != null && !tokenHeader.isEmpty()) {

                        session = sessionDao.findBySessionHash(tokenHeader);

                        if (session != null) {

                            if (this.isSessionActive(session)) {

                                if (securityLevel.roles().length > 0) {

                                    for (RoleTypeEnum role : securityLevel.roles()) {

                                        if (session.user.role.authority == role || role == RoleTypeEnum.PUBLIC) {

                                            isAuthorized = true;
                                        }
                                    }
                                } else {
                                    if (session.user.role.authority == securityLevel.role() || securityLevel.role() == RoleTypeEnum.PUBLIC) {

                                        isAuthorized = true;

                                    }
                                }

                            } else {

                                sessionInactive = true;

                            }
                        } else {
                            sessionInactive = true;
                        }
                    } else {
                        throw new UnauthorizedException();
                    }
                }
            }
        }

        if (sessionInactive) {
            throw new UnauthorizedException();
        }

        if (!isAuthorized) {
            throw new UnauthorizedException();
        }

    }

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws
            Exception {

//        if (request.getHeader('origin')) {
//            String origin = request.getHeader('origin')
//            response.addHeader('Access-Control-Allow-Origin', origin)
//            response.addHeader('Access-Control-Allow-Methods', 'GET, POST, PATCH, DELETE, OPTIONS')
//            response.addHeader('Access-Control-Allow-Credentials', 'true')
//            response.addHeader('Access-Control-Allow-Headers',
//                    request.getHeader('Access-Control-Request-Headers'))
//        }
//        if (request.method == 'OPTIONS') {
//            response.writer.print('OK')
//            response.writer.flush()
//            return
//        }

        if(handler instanceof HandlerMethod){
            this.authorize(request, (HandlerMethod) handler);
        }


        return true;

    }

    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

}
