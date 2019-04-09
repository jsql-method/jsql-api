package pl.jsql.api.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.jsql.api.model.hashing.DeveloperKey;
import pl.jsql.api.model.user.User;

import java.util.Optional;

@Repository
public interface DeveloperKeyDao extends CrudRepository<DeveloperKey, Long> {

    DeveloperKey findByUser(User user);

    DeveloperKey findByKey(String key);

    void deleteByUser(User user);

}

