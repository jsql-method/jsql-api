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

    @Query("SELECT t FROM ApplicationDevelopers t where t.member = :member and t.application.active = true")
    List<ApplicationDevelopers> findByUserQuery(@Param("member") User member);

    @Query("SELECT t FROM ApplicationDevelopers t where t.member = :member and t.application = :application and t.application.active = true")
    ApplicationDevelopers findByUserAndAppQuery(@Param("member") User member, @Param("application") Application application);

    @Query("select t from ApplicationDevelopers t where t.application.active = true and t.member = :member")
    List<ApplicationDevelopers> findByApplicationActive(@Param("member") User member);

    @Modifying
    @Query("update ApplicationDevelopers t set t.member = null, t.application = null where t.member = :member and t.application = :application")
    void clearJoinsByUserAndApp(@Param("member") User member, @Param("application") Application application);

    @Modifying
    @Query("delete from ApplicationDevelopers t where t.member = :member and t.application = :application")
    void deleteByUserAndApp(@Param("member") User member, @Param("application") Application application);

    @Modifying
    @Query("update ApplicationDevelopers t set t.member = null, t.application = null where t.member = :member")
    void clearJoinsByUser(@Param("member") User member);

    @Modifying
    @Query("delete from ApplicationDevelopers t where t.member = :member")
    void deleteAllByUser(@Param("member") User member);

}

