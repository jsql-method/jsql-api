package pl.jsql.api.config

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import pl.jsql.api.exceptions.SecurityException

@ControllerAdvice
class SecurityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = [SecurityException.class])
    def handleConflict(SecurityException ex) {
        String[] splitted = ex.getMessage().split(",")
        return new ResponseEntity([code: Integer.valueOf(splitted[0]), description: splitted[1]], HttpStatus.OK)
    }
}

