package pl.jsql.api.config

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import pl.jsql.api.dto.response.MessageResponse
import pl.jsql.api.exceptions.SecurityException

@ControllerAdvice
class SecurityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = [SecurityException.class])
    def handleConflict(SecurityException ex) {

        String[] split = ex.getMessage().split(",")
        return new ResponseEntity(new MessageResponse(message: split[1]), HttpStatus.OK)

    }

}

