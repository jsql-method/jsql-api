package pl.jsql.api.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.jsql.api.model.stats.Request;

@Repository
public interface RequestDao extends CrudRepository<Request, Long> {


}

