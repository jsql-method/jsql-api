package pl.jsql.api.controller.generic;

import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pl.jsql.api.dto.response.BasicResponse;

import java.util.HashMap;

@Component
abstract public class ValidateController {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public BasicResponse<HashMap<String, String>> handleMethodNotValidException(MethodArgumentNotValidException ex) {

        HashMap<String, String> errors = new HashMap<>();

        for (ObjectError objectError : ex.getBindingResult().getAllErrors()) {

            if (objectError instanceof FieldError) {

                FieldError fieldError = (FieldError) objectError;

                String message = fieldError.getDefaultMessage();
                message = message.replace("$", "").replace("}", "").replace("{", "");

                errors.put(fieldError.getField(), message);
            }

        }

        return new BasicResponse<>(204, errors);
    }
}
