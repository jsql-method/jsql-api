package pl.jsql.api.jobs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.jsql.api.repo.PlanDao;

@Component
public class PlanCancelationJob {

    @Autowired
    private PlanDao planDao;

    private final static long DELAY = 86400000L; //24 h

    @Transactional
    @Scheduled(fixedDelay = DELAY)
    public void cancelPlans()  {
        planDao.updatePlanSetUnactiveWhereExpiryDateCurrent();
        planDao.decrementTrialDays();
    }

}
