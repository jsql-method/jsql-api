package pl.jsql.api.config;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import pl.jsql.api.dto.response.BasicResponse;
import pl.jsql.api.exceptions.UnauthorizedException;

@ControllerAdvice
public class SecurityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = UnauthorizedException.class)
    public BasicResponse<String> handleConflict(UnauthorizedException ex) {
        String[] split = ex.getMessage().split(",");
        return new BasicResponse<>(Integer.valueOf(split[0]), split[1]);
    }
}
