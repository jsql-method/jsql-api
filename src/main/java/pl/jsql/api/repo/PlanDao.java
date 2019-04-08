package pl.jsql.api.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.jsql.api.model.payment.Plan;
import pl.jsql.api.model.user.Company;

import java.util.Optional;

@Repository
interface PlanDao extends CrudRepository<Plan, Long> {

    Optional<Plan> findFirstByCompany(Company company);

}
