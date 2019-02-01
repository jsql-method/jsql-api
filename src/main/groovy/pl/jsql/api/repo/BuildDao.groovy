package pl.jsql.api.repo

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import pl.jsql.api.model.stats.Build
import pl.jsql.api.model.user.Company

import javax.transaction.Transactional

@Transactional
interface BuildDao extends CrudRepository<Build, Long> {
    List<Build> findAllByOrderByHashingDateAsc()

    @Query("select r from Build r where r.user.company = :company order by r.hashingDate asc")
    List<Build> findByCompanyQuery(@Param("company") Company company)

    @Query("SELECT t FROM Build t where t.hashingDate >= :from and t.hashingDate <= :to and t.user.company = :company and t.application.id in :apps and t.user.id in :users")
    List<Build> findByCompanyAndCreatedDateBetween(
            @Param("from") Date from,
            @Param("to") Date to,
            @Param("company") Company company,
            @Param("apps") List<Long> apps,
            @Param("users") List<Long> users)

}

