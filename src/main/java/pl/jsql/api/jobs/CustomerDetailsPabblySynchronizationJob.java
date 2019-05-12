package pl.jsql.api.jobs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.jsql.api.dto.request.UserRequest;
import pl.jsql.api.enums.RoleTypeEnum;
import pl.jsql.api.model.user.Company;
import pl.jsql.api.model.user.User;
import pl.jsql.api.repo.CompanyDao;
import pl.jsql.api.repo.PlanDao;
import pl.jsql.api.repo.UserDao;
import pl.jsql.api.service.pabbly.PabblyGetCustomerService;

import java.util.List;

@Component
public class CustomerDetailsPabblySynchronizationJob {

    @Autowired
    private UserDao userDao;

    @Autowired
    private CompanyDao companyDao;

    @Autowired
    private PabblyGetCustomerService pabblyGetCustomerService;

    private final static long DELAY = 86400000L; //24 h

    @Value("${spring.datasource.password}")
    private String databasePassword;

    @Transactional
    @Scheduled(fixedDelay = DELAY)
    public void synchronize() {

        if(databasePassword.contains("postgres")){
            return;
        }

        List<User> companyAdmins = userDao.findByRole(RoleTypeEnum.COMPANY_ADMIN);

        for (User user : companyAdmins) {

            UserRequest userRequest = pabblyGetCustomerService.getCustomer(user.company.pabblyCustomerId);

            if(userRequest != null){
                updateUser(user, userRequest);
                updateCompany(user, userRequest);
            }

        }

    }

    @Transactional
    public void updateCompany(User user, UserRequest userRequest) {

        Company company = companyDao.findById(user.company.id).orElse(null);

        if(company != null){
            company.name = userRequest.companyName;
            companyDao.save(company);
        }

    }

    @Transactional
    public void updateUser(User user, UserRequest userRequest) {

        user.firstName = userRequest.firstName;
        user.lastName = userRequest.lastName;

        userDao.save(user);

    }

}
