package pl.jsql.api.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.jsql.api.model.hashing.DeveloperKey;
import pl.jsql.api.model.user.User;

import java.util.Optional;

@Repository
interface DeveloperKeyDao extends CrudRepository<DeveloperKey, Long> {

    Optional<DeveloperKey> findByUser(User user);

    Optional<DeveloperKey> findByKey(String key);

    void deleteByUser(User user);

}

