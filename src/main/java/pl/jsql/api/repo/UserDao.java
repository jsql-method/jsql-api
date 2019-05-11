package pl.jsql.api.repo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.jsql.api.dto.response.AppAdminResponse;
import pl.jsql.api.dto.response.AppDeveloperResponse;
import pl.jsql.api.model.user.Company;
import pl.jsql.api.model.user.Role;
import pl.jsql.api.model.user.User;

import java.util.List;

@Repository
public interface UserDao extends CrudRepository<User, Long> {

    User findByEmail(String email);
    User findByToken(String token);

    @Query("select new pl.jsql.api.dto.response.AppAdminResponse(u.id, u.email, u.firstName, u.lastName, u.enabled) from User u where u.company = :company and u.role = :role")
    List<AppAdminResponse> findAppAdminsByCompanyAndRole(@Param("company") Company company, @Param("role") Role role);

    @Query("select distinct u from User u, Role r where r.authority = pl.jsql.api.enums.RoleTypeEnum.COMPANY_ADMIN and u.company = :company and u.role = r and u.enabled = true")
    User findCompanyAdmin(@Param("company") Company company);

    @Query("select u from User u where u.company = :company and u.role = :role and u.enabled = true")
    List<User> findByCompanyAndRoleEnabled(@Param("company") Company company, @Param("role") Role role);

    @Query("select u from User u where u.company = :company and u.role = :role")
    List<User> findByCompanyAndRole(@Param("company") Company company, @Param("role") Role role);

    @Query("select new pl.jsql.api.dto.response.AppDeveloperResponse(u.id, u.email, u.firstName, u.lastName, u.enabled) from User u where u.company = :company and u.role = :role and u.isProductionDeveloper <> true")
    List<AppDeveloperResponse> findyByCompanyAndRoleWithoutFake(@Param("company") Company company, @Param("role") Role role);

    @Query("select count(u) from User u where u.company = :company and u.role.id <> 3 and u.isProductionDeveloper <> true")
    Integer countByCompany(@Param("company") Company company);

    @Query("select count(u) from User u where u.company = :company and u.isDeleted = false and u.isProductionDeveloper = false")
    Integer countActiveUsersByCompany(@Param("company") Company company);

    @Query("select case when count(u) > 0 then TRUE else FALSE end from User u where u.email = :email")
    boolean existByEmail(@Param("email") String email);

    @Query("select case when count(u) > 0 then TRUE else FALSE end from User u where u.firstName = :firstName and u.lastName = :lastName and u.company = :company")
    boolean existByFullnameForCompany(@Param("firstName") String firstName, @Param("lastName") String lastName, @Param("company") Company company);
}

