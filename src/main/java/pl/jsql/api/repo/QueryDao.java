package pl.jsql.api.repo;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.jsql.api.model.hashing.Application;
import pl.jsql.api.model.hashing.Query;
import pl.jsql.api.model.user.User;

@Repository
public interface QueryDao extends CrudRepository<Query, Long> {

    Query findByHashAndApplication(String hash, Application application);

    Query findByApplicationAndUserAndQueryAndArchived(Application application, User user, String query, Boolean archived);

    Query findByApplicationAndUserAndHashAndArchived(Application application, User user, String hash, Boolean archived);

    @Modifying
    @org.springframework.data.jpa.repository.Query("delete from Query q where q.application = :application and q.user = :user")
    void deleteByQuery(@Param("application") Application application, @Param("user") User user);

    @Modifying
    @org.springframework.data.jpa.repository.Query("update Query q set q.used = true where q = :query")
    void markQueryAsUsed(@Param("query") Query query);

    @Modifying
    @org.springframework.data.jpa.repository.Query("update Query q set q.archived = true where q.application = :application and q.user = :developer and q.query <> q.hash")
    void updateByApplicationAndUserAndQueryAndHashNotEqual(@Param("application") Application application, @Param("developer") User developer);

    @Modifying
    @org.springframework.data.jpa.repository.Query("update Query q set q.archived = true where q.application = :application and q.user = :developer and q.query = :query")
    void updateByApplicationAndUserAndQueriesEqual(@Param("application") Application application, @Param("developer") User developer, @Param("query") String query);

    @Modifying
    @org.springframework.data.jpa.repository.Query("update Query q set q.archived = true where q.application = :application and q.user = :developer")
    void invalidateAllQueries(@Param("application") Application application, @Param("developer") User developer);
}


