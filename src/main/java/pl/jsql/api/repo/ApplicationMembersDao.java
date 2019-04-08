package pl.jsql.api.repo

import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import pl.jsql.api.model.hashing.Application
import pl.jsql.api.model.hashing.ApplicationMembers
import pl.jsql.api.model.user.User

import javax.transaction.Transactional

@Transactional
interface ApplicationMembersDao extends CrudRepository<ApplicationMembers, Long> {

    @Query("SELECT t FROM ApplicationMembers t where t.member = :member and t.application.active = true")
    List<ApplicationMembers> findByUserQuery(@Param("member") User member)

    @Query("SELECT t FROM ApplicationMembers t where t.member = :member and t.application = :application and t.application.active = true")
    ApplicationMembers findByUserAndAppQuery(
            @Param("member") User member, @Param("application") Application application)

    @Query("select t from ApplicationMembers t where t.application.active = true and t.member = :member")
    List<ApplicationMembers> findByApplicationActive(@Param("member") User member)

    @Modifying
    @Query("update ApplicationMembers t set t.member = null, t.application = null where t.member = :member and t.application = :application")
    void clearJoinsByUserAndApp(@Param("member") User member, @Param("application") Application application)

    @Modifying
    @Query("delete from ApplicationMembers t where t.member = :member and t.application = :application")
    void deleteByUserAndApp(@Param("member") User member, @Param("application") Application application)

    @Modifying
    @Query("update ApplicationMembers t set t.member = null, t.application = null where t.member = :member")
    void clearJoinsByUser(@Param("member") User member)

    @Modifying
    @Query("delete from ApplicationMembers t where t.member = :member")
    void deleteAllByUser(@Param("member") User member)

}

