package pl.jsql.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.jsql.api.dto.request.UserRequest;
import pl.jsql.api.model.payment.Plan;
import pl.jsql.api.model.user.User;
import pl.jsql.api.repo.PlanDao;

import javax.transaction.Transactional;
import java.util.Date;

@Transactional
@Service
public class PlanService {

    @Autowired
    private PlanDao planDao;

    public void createPlan(User user, UserRequest userRequest) {

        Plan plan = new Plan();
        plan.company = user.company;
        plan.activationDate = new Date();
        plan.active = true;
        plan.plan = userRequest.plan;
        plan.trial = userRequest.isTrial;
        plan.pabblySubscriptionId = userRequest.pabblySubscriptionId;
        plan.trialDays = userRequest.trialDays;

        if(userRequest.isTrial){
            plan.hadTrial = true;
        }

        planDao.save(plan);

    }

}
