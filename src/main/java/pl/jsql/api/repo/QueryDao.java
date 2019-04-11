package pl.jsql.api.repo;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.jsql.api.dto.response.QueryResponse;
import pl.jsql.api.model.hashing.Application;
import pl.jsql.api.model.hashing.Query;
import pl.jsql.api.model.user.Company;
import pl.jsql.api.model.user.User;

import java.util.Date;
import java.util.List;

@Repository
public interface QueryDao extends CrudRepository<Query, Long> {

    Query findByHashAndApplication(String hash, Application application);

    Query findByApplicationAndUserAndQuery(Application application, User user, String query);

    Query findByApplicationAndUserAndHash(Application application, User user, String hash);

    @Modifying
    @org.springframework.data.jpa.repository.Query("delete from Query q where q.application = :application and q.user = :user")
    void deleteByQuery(@Param("application") Application application, @Param("user") User user);

    @org.springframework.data.jpa.repository.Query("SELECT count(t) FROM Query t where t.queryDate >= :dateFrom and t.queryDate <= :dateTo " +
            "and t.user.company = :company and (t.application.id in :applications or true) and (t.user.id in :developers or true) and t.used = :used and t.dynamic = :dynamic")
    Integer countQueriesForCompany(
            @Param("company") Company company,
            @Param("dateFrom") Date dateFrom,
            @Param("dateTo") Date dateTo,
            @Param("applications") List<Long> applications,
            @Param("developers") List<Long> developers,
            @Param("dynamic") Boolean dynamic,
            @Param("used") Boolean used);

    @org.springframework.data.jpa.repository.Query("SELECT count(t) FROM Query t where t.queryDate >= :from and t.queryDate <= :to " +
            "and t.user = :currentUser and (t.application.id in :applications or true) and t.used = :used and t.dynamic = :dynamic")
    Integer countQueriesForDeveloper(
            @Param("currentUser") User currentUser,
            @Param("dateFrom") Date dateFrom,
            @Param("dateTo") Date dateTo,
            @Param("applications") List<Long> applications,
            @Param("dynamic") Boolean dynamic,
            @Param("used") Boolean used);

    @org.springframework.data.jpa.repository.Query("SELECT new pl.jsql.api.dto.response.QueryResponse(t.id, t.query, t.hash, t.queryDate, t.used, t.dynamic, concat(t.user.firstName, ' ', t.user.lastName), t.application.name, t.application.id) " +
            "FROM Query t where t.queryDate >= :dateFrom and t.queryDate <= :dateTo " +
            "and t.user.company = :company and (t.application.id in :applications or true) and (t.user.id in :developers or true) and t.used = :used and t.dynamic = :dynamic")
    List<QueryResponse> selectQueriesForCompany(
            @Param("company") Company company,
            @Param("dateFrom") Date dateFrom,
            @Param("dateTo") Date dateTo,
            @Param("applications") List<Long> applications,
            @Param("developers") List<Long> developers,
            @Param("dynamic") Boolean dynamic,
            @Param("used") Boolean used,
            Pageable pageable);

    @org.springframework.data.jpa.repository.Query("SELECT new pl.jsql.api.dto.response.QueryResponse(t.id, t.query, t.hash, t.queryDate, t.used, t.dynamic, concat(t.user.firstName, ' ', t.user.lastName), t.application.name, t.application.id) " +
            "FROM Query t where t.queryDate >= :dateFrom and t.queryDate <= :dateTo " +
            "and t.user = :currentUser and (t.application.id in :applications or true) and t.used = :used and t.dynamic = :dynamic")
    List<QueryResponse> selectQueriesForDeveloper(
            @Param("currentUser") User currentUser,
            @Param("dateFrom") Date dateFrom,
            @Param("dateTo") Date dateTo,
            @Param("applications") List<Long> applications,
            @Param("dynamic") Boolean dynamic,
            @Param("used") Boolean used,
            Pageable pageable);

}


