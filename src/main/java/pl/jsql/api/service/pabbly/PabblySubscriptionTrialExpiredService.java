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
public class PabblySubscriptionTrialExpiredService implements IPabbly {

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

        int trial = (int) requestPlan.get("trial_period");

        Plan plan = planDao.findFirstByCompany(user.company);
        plan.active = false;
        plan.trial = trial != 0;
        plan.trialDays = trial;

        planDao.save(plan);

    }

}
