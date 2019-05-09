package pl.jsql.api.service.pabbly;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.jsql.api.dto.request.UserRequest;
import pl.jsql.api.enums.PabblyStatus;
import pl.jsql.api.enums.PlansEnum;
import pl.jsql.api.model.payment.Plan;
import pl.jsql.api.model.user.User;
import pl.jsql.api.repo.PlanDao;
import pl.jsql.api.repo.UserDao;
import pl.jsql.api.service.AuthService;

import java.util.Map;

@Service
public class PabblySubscriptionActivateService implements IPabbly {

    @Autowired
    private UserDao userDao;

    @Autowired
    private AuthService authService;

    @Autowired
    private PlanDao planDao;

    @Autowired
    private PabblyGetCustomerService pabblyGetCustomerService;

    @Override
    public void process(PabblyStatus eventType, Map<String, Object> request) {

        Map<String, Object> requestData = (Map<String, Object>) request.get("data");
        Map<String, Object> requestPlan = (Map<String, Object>) requestData.get("plan");

        String userEmail;
        User user;

        userEmail = (String) requestData.get("email_id");
        String planDescription = (String) requestPlan.get("plan_code");
        PlansEnum planEnum = PlansEnum.valueOf(planDescription.toUpperCase());
        int trial = (int) requestPlan.get("trial_period");

        user = userDao.findByEmail(userEmail);

        if (user == null) {

            UserRequest userRequest = pabblyGetCustomerService.getCustomer((String) requestData.get("customer_id"));
            userRequest.plan = planEnum;
            userRequest.isTrial = trial != 0;
            authService.register(userRequest);

        }else{

            String activeStr = (String) requestPlan.get("plan_active");

            Boolean planActive = false;
            if(activeStr.equals("true")){
                planActive = true;
            }

            Plan plan = planDao.findFirstByCompany(user.company);
            plan.active = planActive;
            plan.trial = trial != 0;
            plan.plan = planEnum;
            plan.trialDays = trial;

            planDao.save(plan);

        }


    }

}
