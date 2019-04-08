package pl.jsql.api.controller.generic

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.validation.FieldError
import org.springframework.validation.ObjectError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import pl.jsql.api.dto.response.BasicResponse
import pl.jsql.api.dto.response.MessageResponse

@Component
abstract public class  ValidateController {

    @ExceptionHandler(MethodArgumentNotValidException.class )
    ResponseEntity handleMethodNotValidException(MethodArgumentNotValidException ex) {

        def errors = [:]

        for (ObjectError objectError : ex.getBindingResult().getAllErrors()) {

            if (objectError instanceof FieldError) {

                FieldError fieldError = (FieldError) objectError

                def message = fieldError.getDefaultMessage()
                message = message.replace("\$", "").replace("}", "").replace("{", "")

                errors[fieldError.getField()] = message

            }

        }

        return new ResponseEntity<>(new BasicResponse<>(status: 204, data: new MessageResponse(messages: errors)), HttpStatus.OK)

    }

}
