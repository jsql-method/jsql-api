package pl.jsql.api.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.jsql.api.model.payment.Plan;
import pl.jsql.api.model.user.Company;

import java.util.Optional;

@Repository
public interface PlanDao extends CrudRepository<Plan, Long> {

    Plan findFirstByCompany(Company company);

}
