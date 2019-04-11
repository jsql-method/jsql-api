package pl.jsql.api.repo;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.jsql.api.model.user.Session;

import java.util.Date;
import java.util.Optional;

@Repository
public interface SessionDao extends CrudRepository<Session, Long> {

    Session findBySessionHash(String sessionHash);

    @Modifying
    @Query("update Session s set s.closedDate = :closedDate where s.sessionHash = :sessionHash")
    void updateSessionClosedDate(@Param("sessionHash") String sessionHash, @Param("closedDate") Date closedDate);

}

