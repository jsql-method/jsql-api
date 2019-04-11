package pl.jsql.api.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import pl.jsql.api.dto.response.BasicResponse;
import pl.jsql.api.dto.response.MessageResponse;
import pl.jsql.api.exceptions.NotFoundException;
import pl.jsql.api.exceptions.UnauthorizedException;

@ControllerAdvice
public class ExceptionsHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<BasicResponse<String>> handleConstraintViolation(UnauthorizedException ex) {

        return new ResponseEntity<>(new BasicResponse<>(401, "Unauthorized"), HttpStatus.UNAUTHORIZED);

    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<BasicResponse<MessageResponse>> handleNotFoundException(NotFoundException ex) {
        return new ResponseEntity<>(new BasicResponse<>(200, new MessageResponse(ex.getMessage())), HttpStatus.OK);
    }



}

