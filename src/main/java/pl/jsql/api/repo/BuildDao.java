package pl.jsql.api.repo;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.jsql.api.dto.response.BuildResponse;
import pl.jsql.api.model.stats.Build;
import pl.jsql.api.model.user.Company;
import pl.jsql.api.model.user.User;

import java.util.Date;
import java.util.List;

@Repository
public interface BuildDao extends CrudRepository<Build, Long> {


    @Query("select count(b) from Build b where b.user.company = :company and (b.user.id in :developers or true) " +
            "and (b.application.id in :applications or true) and b.hashingDate >= :dateFrom and b.hashingDate <= :dateTo")
    Integer countBuildsForCompany(
            @Param("company") Company company,
            @Param("dateFrom") Date dateFrom,
            @Param("dateTo") Date dateTo,
            @Param("applications") List<Long> applications,
            @Param("developers") List<Long> developers);

    @Query("select count(b) from Build b where b.user = :currentUser " +
            "and (b.application.id in :applications or true) and b.hashingDate >= :dateFrom and b.hashingDate <= :dateTo")
    Integer countBuildsForDeveloper(
            @Param("currentUser") User currentUser,
            @Param("dateFrom") Date dateFrom,
            @Param("dateTo") Date dateTo,
            @Param("applications") List<Long> applications);

    @Query("select new pl.jsql.api.dto.response.BuildResponse(b.application.name, b.application.id, concat(b.user.firstName,' ',b.user.lastName), b.user.id, b.hashingDate, b.queriesCount) " +
            "from Build b " +
            "where b.user.company = :company and (b.user.id in :developers or true) " +
            "and (b.application.id in :applications or true) and b.hashingDate >= :dateFrom and b.hashingDate <= :dateTo")
    List<BuildResponse> selectBuildsForCompany(
            @Param("company") Company company,
            @Param("dateFrom") Date dateFrom,
            @Param("dateTo") Date dateTo,
            @Param("applications") List<Long> applications,
            @Param("developers") List<Long> developers,
            Pageable pageable);

    @Query("select new pl.jsql.api.dto.response.BuildResponse(b.application.name, b.application.id, concat(b.user.firstName,' ',b.user.lastName), b.user.id, b.hashingDate, b.queriesCount) " +
            "from Build b " +
            "where b.user = :currentUser " +
            "and (b.application.id in :applications or true) and b.hashingDate >= :dateFrom and b.hashingDate <= :dateTo")
    List<BuildResponse> selectBuildsForDeveloper(
            @Param("currentUser") User currentUser,
            @Param("dateFrom") Date dateFrom,
            @Param("dateTo") Date dateTo,
            @Param("applications") List<Long> applications,
            Pageable pageable);
}

