package pl.jsql.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.jsql.api.dto.request.ForgotPasswordRequest;
import pl.jsql.api.dto.request.PabblyPaymentRequest;
import pl.jsql.api.dto.request.UserRequest;
import pl.jsql.api.dto.response.PlanResponse;
import pl.jsql.api.enums.PabblyStatus;
import pl.jsql.api.enums.PlansEnum;
import pl.jsql.api.model.payment.Plan;
import pl.jsql.api.model.user.User;
import pl.jsql.api.repo.*;
import pl.jsql.api.security.service.SecurityService;

import javax.transaction.Transactional;
import java.util.Map;

@Transactional
@Service
public class PaymentService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private AuthService authService;

    @Autowired
    private PlanDao planDao;

    @Autowired
    private SettingDao settingDao;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private ApplicationDao applicationDao;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private UserService userService;

    public void activeOrUnactivePlan(Map<String, Object> request) {

        PabblyStatus eventType = PabblyStatus.valueOf((String) request.get("event_type"));
        Map<String, Object> requestData = (Map<String, Object>) request.get("data");
        Map<String, Object> requestPlan = (Map<String, Object>) requestData.get("plan");

        String userEmail;
        User user;
        Plan plan;

        if (eventType == PabblyStatus.SUBSCRIPTION_ACTIVATE ||  eventType == PabblyStatus.SUBSCRIPTION_CREATE) {

            userEmail = (String) requestData.get("email_id");
            String planDescription = (String) requestPlan.get("plan_code");
            int trialPeriod = (int) requestPlan.get("trial_period");

            user = userDao.findByEmail(userEmail);

            if (user == null) {

                UserRequest userRequest = new UserRequest();
                authService.register(userRequest);
                user = userDao.findByEmail(userEmail);

            }

            plan = planDao.findFirstByCompany(user.company);

            switch (PlansEnum.valueOf(planDescription.toUpperCase())) {

                case STARTER:
                    plan.active = true;
                    plan.plan = PlansEnum.STARTER;
                    break;

                case BUSINESS:
                    plan.active = true;
                    plan.plan = PlansEnum.BUSINESS;
                    break;

                case LARGE:
                    plan.active = true;
                    plan.plan = PlansEnum.LARGE;
                    break;

            }

            userService.forgotPassword(new ForgotPasswordRequest(user.email));

            planDao.save(plan);

        } else if (eventType == PabblyStatus.PAYMENT_FAILURE) {

            Map<String, Object> requestTransaction = (Map<String, Object>) requestData.get("transaction");
            Map<String, Object> requestPaymentMethod = (Map<String, Object>) requestTransaction.get("payment_method");

            userEmail = (String) requestPaymentMethod.get("email");
            user = userDao.findByEmail(userEmail);

            if (user == null) {
                return;
            }

            plan = planDao.findFirstByCompany(user.company);
            plan.active = false;
            planDao.save(plan);

        }


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

        return planResponse;

    }

}
