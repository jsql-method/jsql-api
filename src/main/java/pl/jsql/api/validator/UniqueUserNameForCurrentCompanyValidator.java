package pl.jsql.api.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.jsql.api.repo.ApplicationDao;
import pl.jsql.api.repo.UserDao;
import pl.jsql.api.security.service.SecurityService;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;

@Component
public class UniqueUserNameForCurrentCompanyValidator implements ConstraintValidator<UniqueUserNameForCurrentCompany, Object> {

    @Autowired
    private UserDao userDao;

    @Autowired
    private SecurityService securityService;

    @Override
    public void initialize(UniqueUserNameForCurrentCompany constraintAnnotation) {

    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {

        String firstName = null;
        try {
            firstName = (String) getFieldValue(value, "firstName");
        } catch (Exception e) {
            e.printStackTrace();
        }
        String lastName = null;
        try {
            lastName = (String) getFieldValue(value, "lastName");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return !userDao.existByFullnameForCompany(firstName, lastName, securityService.getCurrentAccount().company);
    }


    private Object getFieldValue(Object object, String fieldName) throws Exception {
        Class<?> clazz = object.getClass();
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(object);
    }

}
