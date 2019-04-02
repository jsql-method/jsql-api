package pl.jsql.api.repo

import org.springframework.data.repository.CrudRepository
import pl.jsql.api.model.payment.Plans
import pl.jsql.api.model.user.Company

import javax.transaction.Transactional

@Transactional
interface PlansDao extends CrudRepository<Plans, Long> {

    Plans findFirstByCompany(Company company)

}
