package pl.jsql.api.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.jsql.api.model.stats.ReportError;

@Repository
public interface ReportErrorDao extends CrudRepository<ReportError, Long> {


}

