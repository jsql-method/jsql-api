package pl.jsql.api.security.interceptor

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import pl.jsql.api.model.hashing.Application
import pl.jsql.api.model.hashing.MemberKey
import pl.jsql.api.repo.ApplicationDao
import pl.jsql.api.repo.ApplicationMembersDao
import pl.jsql.api.repo.MemberKeyDao

import javax.servlet.http.HttpServletRequest

import static pl.jsql.api.enums.HttpMessageEnum.MISSING_HEADER
import static pl.jsql.api.enums.HttpMessageEnum.NO_SUCH_APP_OR_MEMBER

@Aspect
@Component
public class  HashingSecurityInterceptor {

    @Autowired
    private HttpServletRequest request

    @Autowired
    ApplicationDao applicationDao

    @Autowired
    MemberKeyDao memberKeyDao

    @Autowired
    ApplicationMembersDao applicationMembersDao

    final String API_KEY_HEADER = "ApiKey"
    final String DEV_KEY_HEADER = "DevKey"

    @Pointcut("@annotation(pl.jsql.api.security.annotation.HashingSecurity)")
    private void hashingSecurityAnnotation() {}

    @Around("pl.jsql.api.security.interceptor.HashingSecurityInterceptor.hashingSecurityAnnotation()")
    Object doSomething(ProceedingJoinPoint pjp) throws Throwable {

        String apiKey = request.getHeader(API_KEY_HEADER)
        String memberKey = request.getHeader(DEV_KEY_HEADER)
        MemberKey member
        Application application

        if (apiKey == null) {
            return new ResponseEntity([code: MISSING_HEADER.getCode(), description: MISSING_HEADER.getDescription() + API_KEY_HEADER], HttpStatus.OK)
        } else {
            application = applicationDao.findByApiKey(apiKey)
        }

        if (memberKey == null) {
            return new ResponseEntity([code: MISSING_HEADER.getCode(), description: MISSING_HEADER.getDescription() + DEV_KEY_HEADER], HttpStatus.OK)
        } else {
            member = memberKeyDao.findByKey(memberKey)
        }

        if (application == null || member == null) {
            return new ResponseEntity([code: NO_SUCH_APP_OR_MEMBER.getCode(), description: NO_SUCH_APP_OR_MEMBER.getDescription()], HttpStatus.OK)
        }


        if (applicationMembersDao.findByUserAndAppQuery(member.user, application) == null) {
            return new ResponseEntity([code: NO_SUCH_APP_OR_MEMBER.getCode(), description: NO_SUCH_APP_OR_MEMBER.getDescription()], HttpStatus.OK)
        }

        return pjp.proceed()
    }

}
