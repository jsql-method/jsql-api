package pl.jsql.api.repo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.jsql.api.model.user.Company;

import java.util.Optional;

@Repository
public interface CompanyDao extends CrudRepository<Company, Long> {

    @Query("select c from Company c where c.id = :id")
    Company selectById(@Param("id") Long id);

}

