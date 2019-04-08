package pl.jsql.api.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.jsql.api.model.dict.EncodingDict;

import java.util.Optional;

@Repository
interface EncodingDictDao extends CrudRepository<EncodingDict, Long> {

    Optional<EncodingDict> findByName(String name);

    Optional<EncodingDict> findByValue(String value);

}

