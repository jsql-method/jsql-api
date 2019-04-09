package pl.jsql.api.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.jsql.api.model.user.Session;

import java.util.Optional;

@Repository
public interface SessionDao extends CrudRepository<Session, Long> {

    Session findBySessionHash(String sessionHash);

}

