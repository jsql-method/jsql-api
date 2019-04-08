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
import pl.jsql.api.repo.ApplicationMembersDao;
import pl.jsql.api.repo.DeveloperKeyDao;
import pl.jsql.api.repo.devKeyDao;

import javax.servlet.http.HttpServletRequest;

import static pl.jsql.api.enums.HttpMessageEnum.MISSING_HEADER;
import static pl.jsql.api.enums.HttpMessageEnum.NO_SUCH_APP_OR_MEMBER;

@Aspect
@Component
public class  HashingSecurityInterceptor {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private ApplicationDao applicationDao;

    @Autowired
    private DeveloperKeyDao developerKeyDao;

    @Autowired
    private ApplicationDevelopersDao applicationDevelopersDao;

    private final String API_KEY_HEADER = "ApiKey";
    private final String DEV_KEY_HEADER = "DevKey";

    @Pointcut("@annotation(pl.jsql.api.security.annotation.HashingSecurity)")
    private void hashingSecurityAnnotation() {}

    @Around("pl.jsql.api.security.interceptor.HashingSecurityInterceptor.hashingSecurityAnnotation()")
    ResponseEntity<MessageResponse> doSomething(ProceedingJoinPoint pjp) throws Throwable {

        String apiKey = request.getHeader(API_KEY_HEADER);
        String devKey = request.getHeader(DEV_KEY_HEADER);

        if (apiKey == null || devKey == null) {
            return new ResponseEntity<>(new MessageResponse(), HttpStatus.OK);
        }

        Application application = applicationDao.findByApiKey(apiKey);
        DeveloperKey member = developerKeyDao.findByKey(devKey);


        if (application == null || member == null) {
            return new ResponseEntity([code: NO_SUCH_APP_OR_MEMBER.getCode(), description: NO_SUCH_APP_OR_MEMBER.getDescription()], HttpStatus.OK)
        }


        if (applicationDevelopersDao.findByUserAndAppQuery(member.user, application) == null) {
            return new ResponseEntity([code: NO_SUCH_APP_OR_MEMBER.getCode(), description: NO_SUCH_APP_OR_MEMBER.getDescription()], HttpStatus.OK)
        }

        return pjp.proceed()
    }

}
