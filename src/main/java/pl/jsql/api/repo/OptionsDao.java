package pl.jsql.api.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.jsql.api.model.hashing.Application;
import pl.jsql.api.model.hashing.Options;

import java.util.Optional;

@Repository
public interface OptionsDao extends CrudRepository<Options, Long> {

    Optional<Options> findByApplication(Application application);

}

