package pl.jsql.api.repo

import org.springframework.data.repository.CrudRepository
import pl.jsql.api.model.user.Company

import javax.transaction.Transactional

@Transactional
interface CompanyDao extends CrudRepository<Company, Long> {

    Optional<Company> findById(Long aLong)

}

