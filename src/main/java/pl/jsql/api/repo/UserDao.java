package pl.jsql.api.repo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.jsql.api.model.user.Company;
import pl.jsql.api.model.user.Role;
import pl.jsql.api.model.user.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserDao extends CrudRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByActivationToken(String token);

    Optional<User> findByForgotToken(String forgotToken);

    @Query("select u from User u where u.company = :company and u.role = :role and u.enabled = true")
    List<User> findByCompanyAndRole(@Param("company") Company company, @Param("role") Role role);

    @Query("select u from User u where u.company = :company and u.role = :role and u.enabled = true and u.isProductionDeveloper <> true")
    List<User> findyByCompanyAndRoleWithoutFake(@Param("company") Company company, @Param("role") Role role);

    @Query("select count(u) from User u where u.company = :company and u.role.id <> 3 and u.isProductionDeveloper <> true")
    Integer countByCompany(@Param("company") Company company);

}

