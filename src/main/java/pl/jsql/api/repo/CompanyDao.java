package pl.jsql.api.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.jsql.api.model.user.Company;

import java.util.Optional;

@Repository
public interface CompanyDao extends CrudRepository<Company, Long> {

    Optional<Company> findById(Long id);

}

