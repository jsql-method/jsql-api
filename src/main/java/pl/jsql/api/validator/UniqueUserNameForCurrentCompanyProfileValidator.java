package pl.jsql.api.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.jsql.api.model.user.User;
import pl.jsql.api.repo.UserDao;
import pl.jsql.api.security.service.SecurityService;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;

@Component
public class UniqueUserNameForCurrentCompanyProfileValidator implements ConstraintValidator<UniqueUserNameForCurrentCompanyProfile, Object> {

    @Autowired
    private UserDao userDao;

    @Autowired
    private SecurityService securityService;

    @Override
    public void initialize(UniqueUserNameForCurrentCompanyProfile constraintAnnotation) {

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

        if(firstName == null && lastName == null){
            return true;
        }

        User currentAccount = securityService.getCurrentAccount();

        if(currentAccount.firstName.equals(firstName) && currentAccount.lastName.equals(lastName)){
            return true;
        }

        return !userDao.existByFullnameForCompany(firstName, lastName, currentAccount.company);
    }


    private Object getFieldValue(Object object, String fieldName) throws Exception {
        Class<?> clazz = object.getClass();
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(object);
    }

}
