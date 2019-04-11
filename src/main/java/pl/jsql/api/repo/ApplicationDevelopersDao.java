package pl.jsql.api.repo;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.jsql.api.model.hashing.Application;
import pl.jsql.api.model.hashing.ApplicationDevelopers;
import pl.jsql.api.model.user.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationDevelopersDao extends CrudRepository<ApplicationDevelopers, Long> {

    @Query("SELECT t FROM ApplicationDevelopers t where t.developer = :developer and t.application.active = true")
    List<ApplicationDevelopers> findByUserQuery(@Param("developer") User developer);

    @Query("SELECT t FROM ApplicationDevelopers t where t.developer = :developer and t.application = :application and t.application.active = true")
    ApplicationDevelopers findByUserAndAppQuery(@Param("developer") User developer, @Param("application") Application application);

    @Query("select t from ApplicationDevelopers t where t.application.active = true and t.developer = :developer")
    List<ApplicationDevelopers> findByApplicationActive(@Param("developer") User developer);

    @Modifying
    @Query("update ApplicationDevelopers t set t.developer = null, t.application = null where t.developer = :developer and t.application = :application")
    void clearJoinsByUserAndApp(@Param("developer") User developer, @Param("application") Application application);

    @Modifying
    @Query("delete from ApplicationDevelopers t where t.developer = :developer and t.application = :application")
    void deleteByUserAndApp(@Param("developer") User developer, @Param("application") Application application);

    @Modifying
    @Query("update ApplicationDevelopers t set t.developer = null, t.application = null where t.developer = :developer")
    void clearJoinsByUser(@Param("developer") User developer);

    @Modifying
    @Query("delete from ApplicationDevelopers t where t.developer = :developer")
    void deleteAllByUser(@Param("developer") User developer);

    @Modifying
    @Query("delete from ApplicationDevelopers ad where ad.application = :application")
    void deleteAllByApplication(@Param("application") Application application);

}

