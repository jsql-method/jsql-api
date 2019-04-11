package pl.jsql.api.security.annotation;

import pl.jsql.api.enums.RoleTypeEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Security {

    RoleTypeEnum role() default RoleTypeEnum.PUBLIC;

    RoleTypeEnum[] roles() default {};

    boolean requireActiveSession() default true;

}