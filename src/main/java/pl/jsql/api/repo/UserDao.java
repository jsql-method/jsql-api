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

    User findByActivationToken(String token);

    User findByForgotToken(String forgotToken);

    @Query("select new pl.jsql.api.dto.response.AppAdminResponse(u.id, u.email, u.firstName, u.lastName) from User u where u.company = :company and u.role = :role and u.enabled = true")
    List<AppAdminResponse> findAppAdminsByCompanyAndRole(@Param("company") Company company, @Param("role") Role role);

    @Query("select u from User u where u.company = :company and u.role = :role and u.enabled = true")
    List<User> findByCompanyAndRole(@Param("company") Company company, @Param("role") Role role);

    @Query("select new pl.jsql.api.dto.response.AppDeveloperResponse(u.id, u.email, u.firstName, u.lastName) from User u where u.company = :company and u.role = :role and u.enabled = true and u.isProductionDeveloper <> true")
    List<AppDeveloperResponse> findyByCompanyAndRoleWithoutFake(@Param("company") Company company, @Param("role") Role role);

    @Query("select count(u) from User u where u.company = :company and u.role.id <> 3 and u.isProductionDeveloper <> true")
    Integer countByCompany(@Param("company") Company company);

}

