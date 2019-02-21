package pl.jsql.api.repo

import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import pl.jsql.api.model.user.Company
import pl.jsql.api.model.user.Role
import pl.jsql.api.model.user.User

import javax.transaction.Transactional

@Transactional
interface UserDao extends CrudRepository<User, Long> {

    User findByEmail(String email)

    User findByActivationToken(String token)

    User findByForgotToken(String forgotToken)

    @Query("select u from User u where u.company = :company and u.role = :role and u.enabled = true")
    List<User> findByCompanyAndRole(@Param('company') Company company, @Param('role') Role role)

    @Query("select u from User u where u.company = :company and u.role = :role and u.enabled = true and u.isFakeDeveloper <> true")
    List<User> findyByCompanyAndRoleWithoutFake(@Param('company') Company company, @Param('role') Role role)

    @Modifying
    @Query("delete from User u where u.role = :role")
    void removeAllByRole(@Param('role') Role role)

    @Query("select count(u) from User u where u.company = :company and u.role.id <> 3 and u.isFakeDeveloper <> true")
    Integer countByCompany(@Param('company') Company company)

}

