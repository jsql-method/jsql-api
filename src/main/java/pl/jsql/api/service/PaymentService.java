package pl.jsql.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.jsql.api.dto.request.UserRequest;
import pl.jsql.api.dto.response.PaymentResponse;
import pl.jsql.api.dto.response.PlanResponse;
import pl.jsql.api.model.payment.Plan;
import pl.jsql.api.model.user.User;
import pl.jsql.api.repo.ApplicationDao;
import pl.jsql.api.repo.PlanDao;
import pl.jsql.api.repo.UserDao;
import pl.jsql.api.security.service.SecurityService;
import pl.jsql.api.service.pabbly.PabblyGetCustomerService;
import pl.jsql.api.service.pabbly.PabblyVerifyHostedService;

import javax.transaction.Transactional;
import java.util.Map;

@Transactional
@Service
public class PaymentService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private PlanDao planDao;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private ApplicationDao applicationDao;

    @Autowired
    private PabblyService pabblyService;

    @Autowired
    private PabblyVerifyHostedService pabblyVerifyHostedService;

    public void processPaymentStatus(Map<String, Object> request) {
        pabblyService.processPaymentStatus(request);
    }

    public PlanResponse getPlan() {

        User companyAdmin = securityService.getCompanyAdmin();

        int activeUsers = userDao.countActiveUsersByCompany(companyAdmin.company);
        int activeApplications = applicationDao.countActiveApplicationsByCompanyAdmin(companyAdmin);

        Plan plan = planDao.findFirstByCompany(companyAdmin.company);

        PlanResponse planResponse = new PlanResponse();

        planResponse.activationDate = plan.activationDate;
        planResponse.active = plan.active;
        planResponse.maxApps = plan.plan.maxApps;
        planResponse.usedApps = activeApplications;
        planResponse.maxUsers = plan.plan.maxUsers;
        planResponse.usedUsers = activeUsers;
        planResponse.name = plan.plan.name;
        planResponse.trial = plan.trial;
        planResponse.expiryDate = plan.expiryDate;
        planResponse.trialDays = plan.trialDays;

        return planResponse;

    }

    public PaymentResponse verifyHosted(String token) {
        return pabblyVerifyHostedService.verifyHosted(token);
    }

}
