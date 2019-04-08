package pl.jsql.api.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.jsql.api.model.dict.DatabaseDialectDict;

import java.util.Optional;

@Repository
interface DatabaseDialectDictDao extends CrudRepository<DatabaseDialectDict, Long> {

    Optional<DatabaseDialectDict> findByName(String name);

    Optional<DatabaseDialectDict> findByValue(String value);
}

