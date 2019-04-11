package pl.jsql.api.repo;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.jsql.api.dto.response.ApplicationResponse;
import pl.jsql.api.model.hashing.Application;
import pl.jsql.api.model.user.Company;
import pl.jsql.api.model.user.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationDao extends CrudRepository<Application, Long> {

    @Query("SELECT t FROM Application t where t.apiKey = :apiKey and t.active = true")
    Application findByApiKey(@Param("apiKey") String apiKey);

    @Query("SELECT t FROM Application t where t.companyAdmin = :companyAdmin and active = true")
    List<Application> findByUserQuery(@Param("companyAdmin") User companyAdmin);

    @Modifying
    @Query("update Application set active = false where companyAdmin = :companyAdmin")
    void updateApplicationToNotActiveByUser(@Param("companyAdmin") User companyAdmin);

    @Query("SELECT t FROM Application t where t.name = :name and t.companyAdmin.company = :company")
    Application findByNameAndCompany(@Param("name") String name, @Param("company") Company company);

    @Query("SELECT COUNT(t) FROM Application t where t.companyAdmin = :companyAdmin and t.active = true")
    Integer countByCompanyAdmin(@Param("companyAdmin") User companyAdmin);

    @Query("select new pl.jsql.api.dto.response.ApplicationResponse(a.id, a.apiKey, a.name, d.key) from Application a, DeveloperKey d where a.productionDeveloper = d.user")
    List<ApplicationResponse> selectAllApplicationsForAdmin();

    @Query("select new pl.jsql.api.dto.response.ApplicationResponse(ad.application.id, ad.application.apiKey, ad.application.name, d.key) from ApplicationDevelopers ad, DeveloperKey d where ad.developer = :developer and d.user = ad.developer")
    List<ApplicationResponse> selectAllApplicationsForAppDeveloper(@Param("developer") User developer);

    @Query("select new pl.jsql.api.dto.response.ApplicationResponse(ad.application.id, ad.application.apiKey, ad.application.name, d.key) from ApplicationDevelopers ad, DeveloperKey d where ad.developer = :appAdmin and d.user = ad.developer")
    List<ApplicationResponse> selectAllApplicationsForAppAdmin(@Param("appAdmin") User appAdmin);

    @Query("select new pl.jsql.api.dto.response.ApplicationResponse(a.id, a.apiKey, a.name, d.key) from Application a, DeveloperKey d where a.companyAdmin = :companyAdmin and d.user = a.productionDeveloper")
    List<ApplicationResponse> selectAllApplicationsForCompanyAdmin(@Param("companyAdmin") User companyAdmin);

    @Query("select new pl.jsql.api.dto.response.ApplicationResponse(a.id, a.apiKey, a.name, d.key) from Application a, DeveloperKey d where a.id = :id and a.productionDeveloper = d.user")
    ApplicationResponse selectApplicationForAdminById(@Param("id") Long id);

    @Query("select new pl.jsql.api.dto.response.ApplicationResponse(ad.application.id, ad.application.apiKey, ad.application.name, d.key) from ApplicationDevelopers ad, DeveloperKey d where ad.application.id = :id and ad.developer = :developer and d.user = ad.developer")
    ApplicationResponse selectApplicationForAppDeveloper(@Param("id") Long id, @Param("developer") User developer);

    @Query("select new pl.jsql.api.dto.response.ApplicationResponse(ad.application.id, ad.application.apiKey, ad.application.name, d.key) from ApplicationDevelopers ad, DeveloperKey d where ad.application.id = :id and ad.developer = :appAdmin and d.user = ad.developer")
    ApplicationResponse selectApplicationForAppAdmin(@Param("id") Long id, @Param("appAdmin") User appAdmin);

    @Query("select new pl.jsql.api.dto.response.ApplicationResponse(a.id, a.apiKey, a.name, d.key) from Application a, DeveloperKey d where a.id = :id and a.companyAdmin = :companyAdmin and d.user = a.productionDeveloper")
    ApplicationResponse selectApplicationForCompanyAdmin(@Param("id")Long id, @Param("companyAdmin") User companyAdmin);

    @Query("select count(a) from Application a where a.companyAdmin = :companyAdmin and a.active = true")
    Integer countActiveApplicationsByCompanyAdmin(@Param("companyAdmin") User companyAdmin);

    @Query("select case when count(a) > 0 then TRUE else FALSE end from Application a where a.name = :name and a.companyAdmin = :companyAdmin")
    boolean existByNameForCompany(@Param("name") String name, @Param("companyAdmin") User companyAdmin);

}

