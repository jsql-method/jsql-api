package pl.jsql.api.repo;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.jsql.api.model.hashing.Application;
import pl.jsql.api.model.hashing.Query;
import pl.jsql.api.model.user.Company;
import pl.jsql.api.model.user.User;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface QueryDao extends CrudRepository<Query, Long> {

    Query findByHashAndApplication(String hash, Application application);

    Query findByApplicationAndUserAndQuery(Application application, User user, String query);

    Query findByApplicationAndUserAndHash(Application application, User user, String hash);

    @Modifying
    @org.springframework.data.jpa.repository.Query("delete from Query q where q.application = :application and q.user = :user")
    void deleteByQuery(@Param("application") Application application, @Param("user") User user);

    @org.springframework.data.jpa.repository.Query("SELECT t FROM Query t where t.queryDate >= :from and t.queryDate <= :to and t.user.company = :company and t.application.id in :apps and t.user.id in :users and t.used = :used and t.dynamic = :dynamic")
    List<Query> findByCompanyAndCreatedDateBetween(
            @Param("from") Date from,
            @Param("to") Date to,
            @Param("company") Company company,
            @Param("apps") List<Long> apps,
            @Param("users") List<Long> users,
            @Param("used") Boolean used,
            @Param("dynamic") Boolean dynamic);

}

