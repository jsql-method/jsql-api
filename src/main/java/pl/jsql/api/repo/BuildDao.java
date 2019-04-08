package pl.jsql.api.repo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.jsql.api.model.stats.Build;
import pl.jsql.api.model.user.Company;

import java.util.Date;
import java.util.List;

@Repository
interface BuildDao extends CrudRepository<Build, Long> {

    @Query("SELECT t FROM Build t where t.hashingDate >= :from and t.hashingDate <= :to and t.user.company = :company and t.application.id in :apps and t.user.id in :users")
    List<Build> findByCompanyAndCreatedDateBetween(
            @Param("from") Date from,
            @Param("to") Date to,
            @Param("company") Company company,
            @Param("apps") List<Long> apps,
            @Param("users") List<Long> users);

}

