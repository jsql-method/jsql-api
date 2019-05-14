package pl.jsql.api.service.pabbly;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.jsql.api.enums.PabblyStatus;
import pl.jsql.api.enums.PlansEnum;
import pl.jsql.api.model.payment.Plan;
import pl.jsql.api.model.user.User;
import pl.jsql.api.repo.PlanDao;
import pl.jsql.api.repo.UserDao;

import java.util.Map;

@Service
public class PabblySubscriptionUpgradeService implements IPabbly {

    @Autowired
    private UserDao userDao;

    @Autowired
    private PlanDao planDao;

    @Override
    public void process(PabblyStatus eventType, Map<String, Object> request) {

        Map<String, Object> requestData = (Map<String, Object>) request.get("data");
        Map<String, Object> requestPlan = (Map<String, Object>) requestData.get("plan");

        String userEmail;
        User user;

        userEmail = (String) requestData.get("email_id");
        user = userDao.findByEmail(userEmail);

        if (user == null) {
            return;
        }

        int trial = (int) requestData.get("trial_days");
        String activeStr = (String) requestPlan.get("plan_active");

        Boolean planActive = false;
        if(activeStr.equals("true")){
            planActive = true;
        }

        String planDescription = (String) requestPlan.get("plan_code");
        PlansEnum planEnum = PlansEnum.valueOf(planDescription.toUpperCase());

        Plan plan = planDao.findFirstByCompany(user.company);
        plan.active = planActive;

        System.out.println("UPGRADE SUBSCRIPTION : "+plan.plan.toString());
        System.out.println("UPGRADE SUBSCRIPTION HAD TRIAL : "+plan.hadTrial);
        if(!plan.hadTrial){
            plan.trial = trial != 0;
            plan.trialDays = trial;

            System.out.println("UPGRADE SUBSCRIPTION SET NEW TRIAL");
        }

        plan.plan = planEnum;

        planDao.save(plan);

    }

}
