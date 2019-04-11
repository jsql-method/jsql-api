package pl.jsql.api.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.jsql.api.model.stats.Build;

@Repository
public interface BuildDao extends CrudRepository<Build, Long> {

}

