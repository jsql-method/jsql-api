package pl.jsql.api.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.jsql.api.model.user.Session;

import java.util.Optional;

@Repository
interface SessionDao extends CrudRepository<Session, Long> {

    Optional<Session> findBySessionHash(String sessionHash);

}

