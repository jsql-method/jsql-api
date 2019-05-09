package pl.jsql.api.service.pabbly;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.jsql.api.enums.PabblyStatus;
import pl.jsql.api.model.payment.Plan;
import pl.jsql.api.model.user.User;
import pl.jsql.api.repo.*;
import pl.jsql.api.security.service.SecurityService;
import pl.jsql.api.service.AuthService;
import pl.jsql.api.service.UserService;

import java.util.Map;

@Service
public class PabblyPaymentFailureService implements IPabbly {

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

        Map<String, Object> requestTransaction = (Map<String, Object>) requestData.get("transaction");
        Map<String, Object> requestPaymentMethod = (Map<String, Object>) requestTransaction.get("payment_method");

        userEmail = (String) requestPaymentMethod.get("email");
        user = userDao.findByEmail(userEmail);

        if (user == null) {
            return;
        }

        Plan plan = planDao.findFirstByCompany(user.company);
        plan.active = false;
        planDao.save(plan);

    }

}
