package pl.jsql.api.security.interceptor;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import pl.jsql.api.dto.response.MessageResponse;
import pl.jsql.api.model.hashing.Application;
import pl.jsql.api.model.hashing.DeveloperKey;
import pl.jsql.api.repo.ApplicationDao;
import pl.jsql.api.repo.ApplicationDevelopersDao;
import pl.jsql.api.repo.DeveloperKeyDao;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
public class HashingSecurityInterceptor {

    public static final String API_KEY_HEADER = "ApiKey";
    public static final String DEV_KEY_HEADER = "DevKey";

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private ApplicationDao applicationDao;

    @Autowired
    private DeveloperKeyDao developerKeyDao;

    @Autowired
    private ApplicationDevelopersDao applicationDevelopersDao;

    @Pointcut("@annotation(pl.jsql.api.security.annotation.HashingSecurity)")
    private void hashingSecurityAnnotation() {
    }

    @Around("pl.jsql.api.security.interceptor.HashingSecurityInterceptor.hashingSecurityAnnotation()")
    Object doSomething(ProceedingJoinPoint pjp) throws Throwable {

        String apiKey = request.getHeader(API_KEY_HEADER);
        String devKey = request.getHeader(DEV_KEY_HEADER);

        if (apiKey == null || devKey == null) {
            return new ResponseEntity<>(new MessageResponse(true,"Unauthorized"), HttpStatus.UNAUTHORIZED);
        }

        Application application = applicationDao.findByApiKey(apiKey);
        DeveloperKey developerKey = developerKeyDao.findByKey(devKey);

        if (application == null || developerKey == null) {
            return new ResponseEntity<>(new MessageResponse(true,"Unauthorized"), HttpStatus.UNAUTHORIZED);
        }

        if (applicationDevelopersDao.findByUserAndAppQuery(developerKey.user, application) == null) {
            return new ResponseEntity<>(new MessageResponse(true,"Unauthorized"), HttpStatus.UNAUTHORIZED);
        }

        return pjp.proceed();

    }

}
