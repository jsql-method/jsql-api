package pl.jsql.api.repo;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.jsql.api.dto.response.RequestResponse;
import pl.jsql.api.model.hashing.Application;
import pl.jsql.api.model.stats.Request;
import pl.jsql.api.model.user.Company;
import pl.jsql.api.model.user.User;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public interface RequestDao extends CrudRepository<Request, Long> {


    @Query("select count(r) from Request r where r.application.companyAdmin.company = :company and (r.application.id in :applications or true) and r.requestDate >= :dateFrom and r.requestDate <= :dateTo")
    Integer countRequestsForCompany(
            @Param("company") Company company,
            @Param("dateFrom") Date dateFrom,
            @Param("dateTo") Date dateTo,
            @Param("applications") List<Long> applications
    );

    @Query("select count(r) from Request r, ApplicationDevelopers ad where r.application = ad.application and ad.developer = :developer and (r.application.id in :applications or true) and r.requestDate >= :dateFrom and r.requestDate <= :dateTo")
    Integer countRequestsForDeveloper(
            @Param("developer") User developer,
            @Param("dateFrom") Date dateFrom,
            @Param("dateTo") Date dateTo,
            @Param("applications") List<Long> applications
    );

    @Query("select new pl.jsql.api.dto.response.RequestResponse(r.application.name, r.application.id, r.requestDate) from Request r where r.application.companyAdmin.company = :company and (r.application.id in :applications or true) and r.requestDate >= :dateFrom and r.requestDate <= :dateTo")
    List<RequestResponse> selectRequestsForCompany(
            @Param("company") Company company,
            @Param("dateFrom") Date dateFrom,
            @Param("dateTo") Date dateTo,
            @Param("applications") List<Long> applications,
            Pageable pageable
    );

    @Query("select new pl.jsql.api.dto.response.RequestResponse(r.application.name, r.application.id, r.requestDate) from Request r, ApplicationDevelopers ad where r.application = ad.application and ad.developer = :developer and (r.application.id in :applications or true) and r.requestDate >= :dateFrom and r.requestDate <= :dateTo")
    List<RequestResponse> selectRequestsForDeveloper(
            @Param("developer") User developer,
            @Param("dateFrom") Date dateFrom,
            @Param("dateTo") Date dateTo,
            @Param("applications") List<Long> applications,
            Pageable pageable
    );

}

