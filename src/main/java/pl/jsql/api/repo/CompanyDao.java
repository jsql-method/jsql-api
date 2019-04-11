package pl.jsql.api.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.jsql.api.model.user.Company;

@Repository
public interface CompanyDao extends CrudRepository<Company, Long> {

}

