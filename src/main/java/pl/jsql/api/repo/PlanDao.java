package pl.jsql.api.repo;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.jsql.api.model.payment.Plan;
import pl.jsql.api.model.user.Company;

import java.util.Optional;

@Repository
public interface PlanDao extends CrudRepository<Plan, Long> {

    Plan findFirstByCompany(Company company);

    @Modifying
    @Query("update Plan p set p.active = false, p.trial = false where p.expiryDate = current_date")
    void updatePlanSetUnactiveWhereExpiryDateCurrent();

    @Modifying
    @Query("update Plan p set p.trialDays = p.trialDays - 1 where p.trialDays > 0")
    void decrementTrialDays();

}
