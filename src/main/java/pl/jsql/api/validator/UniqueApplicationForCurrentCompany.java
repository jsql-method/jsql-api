package pl.jsql.api.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = UniqueEmailValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueApplicationForCurrentCompany {

    String message() default "${validation.message.email.availability}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
