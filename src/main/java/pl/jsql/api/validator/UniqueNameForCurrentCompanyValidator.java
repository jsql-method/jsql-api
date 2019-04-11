package pl.jsql.api.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.jsql.api.repo.ApplicationDao;
import pl.jsql.api.security.service.SecurityService;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
public class UniqueNameForCurrentCompanyValidator implements ConstraintValidator<UniqueEmail, String> {

    @Autowired
    private ApplicationDao applicationDao;

    @Autowired
    private SecurityService securityService;

    @Override
    public boolean isValid(String name, ConstraintValidatorContext context) {
        return !applicationDao.existByNameForCompany(name, securityService.getCompanyAdmin());
    }

}
