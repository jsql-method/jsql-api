package pl.jsql.api.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = UniqueUserNameForCurrentCompanyValidator.class)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueUserNameForCurrentCompany {

    String message() default "${validation.message.user.username.availability}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
