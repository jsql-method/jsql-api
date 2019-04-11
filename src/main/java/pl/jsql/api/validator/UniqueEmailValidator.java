package pl.jsql.api.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.jsql.api.repo.UserDao;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {

    @Autowired
    private UserDao userDao;

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        return !userDao.existByEmail(email);
    }

}
