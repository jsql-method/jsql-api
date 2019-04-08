package pl.jsql.api.repo;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.jsql.api.model.hashing.Application;
import pl.jsql.api.model.user.Company;
import pl.jsql.api.model.user.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationDao extends CrudRepository<Application, Long> {

    @Query("SELECT t FROM Application t where t.apiKey = :apiKey and active = true")
    Optional<Application> findByApiKey(@Param("apiKey") String apiKey);

    @Query("SELECT t FROM Application t where t.user = :user and active = true")
    List<Application> findByUserQuery(@Param("user") User user);

    @Query("SELECT t FROM Application t where t.user = :user and t.id = :id and active = true")
    Optional<Application> findByUserAndIdQuery(@Param("user") User user, @Param("id") Long id);

    @Modifying
    @Query("update Application set active = false where user = :user")
    void updateApplicationToNotActiveByUser(@Param("user") User user);

    @Query("SELECT t FROM Application t where t.name = :name and t.user.company = :company")
    Optional<Application> findByNameAndCompany(@Param("name") String name, @Param("company") Company company);

    @Query("SELECT COUNT(t) FROM Application t where t.user = :user and active = true")
    Integer countByUser(@Param("user") User user);

}

