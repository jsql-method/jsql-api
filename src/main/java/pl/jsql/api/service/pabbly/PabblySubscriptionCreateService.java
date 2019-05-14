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

import java.util.Calendar;
import java.util.Map;

@Service
public class PabblySubscriptionCreateService implements IPabbly {

    @Autowired
    private UserDao userDao;

    @Autowired
    private AuthService authService;

    @Autowired
    private PabblyGetCustomerService pabblyGetCustomerService;

    @Autowired
    private PlanDao planDao;

    @Override
    public void process(PabblyStatus eventType, Map<String, Object> request) {

        Map<String, Object> requestData = (Map<String, Object>) request.get("data");
        Map<String, Object> requestPlan = (Map<String, Object>) requestData.get("plan");

        String userEmail;
        User user;

        userEmail = (String) requestData.get("email_id");
        String planDescription = (String) requestPlan.get("plan_code");
        PlansEnum planEnum = PlansEnum.valueOf(planDescription.toUpperCase());
        int trial = (int) requestData.get("trial_days");

        user = userDao.findByEmail(userEmail);

        if (user == null) {

            UserRequest userRequest = pabblyGetCustomerService.getCustomer((String) requestData.get("customer_id"));
            userRequest.plan = planEnum;
            userRequest.pabblySubscriptionId = (String) requestData.get("id");
            userRequest.pabblyCustomerId = (String) requestData.get("customer_id");
            userRequest.isTrial = trial != 0;
            userRequest.trialDays = trial;
            authService.register(userRequest);

        }else{

            Plan plan = planDao.findFirstByCompany(user.company);

            System.out.println("CREATE SUBSCRIPTION AGAIN: "+plan.plan.toString());
            System.out.println("CREATE SUBSCRIPTION AGAIN HAD TRIAL: "+plan.hadTrial);
            if(plan.hadTrial) {
                plan.trial = false;
                System.out.println("CREATE SUBSCRIPTION AGAIN REMOVE TRIAL");
            }else{
                plan.trial = trial != 0;
                plan.trialDays = trial;
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.MONTH, 1);

                plan.expiryDate = cal.getTime();

                System.out.println("CREATE SUBSCRIPTION AGAIN SET AGAIN TRIAL");
            }

            plan.plan = planEnum;
            plan.doubledSubscriptions = true;

            planDao.save(plan);

        }


    }

}
