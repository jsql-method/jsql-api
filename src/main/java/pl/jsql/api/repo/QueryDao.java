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

    Query findByApplicationAndUserAndQuery(Application application, User user, String query);

    Query findByApplicationAndUserAndHash(Application application, User user, String hash);

    @Modifying
    @org.springframework.data.jpa.repository.Query("delete from Query q where q.application = :application and q.user = :user")
    void deleteByQuery(@Param("application") Application application, @Param("user") User user);

}


