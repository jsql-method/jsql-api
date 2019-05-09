package pl.jsql.api.jobs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.jsql.api.repo.PlanDao;

@Component
public class PlanCancelationJob {

    @Autowired
    private PlanDao planDao;

    private final static long DELAY = 60*60*12*1000L; //12 h

    @Scheduled(fixedDelay = DELAY)
    public void clearCache()  {
        planDao.updatePlanSetUnactiveWhereExpiryDateCurrent();
    }

}
