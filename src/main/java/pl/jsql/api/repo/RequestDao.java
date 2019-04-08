package pl.jsql.api.repo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.jsql.api.model.hashing.Application;
import pl.jsql.api.model.stats.Request;
import pl.jsql.api.model.user.Company;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
interface RequestDao extends CrudRepository<Request, Long> {

    @Query("SELECT count(t), hour(t.requestDate) FROM Request t where day(t.requestDate) = :day and month(t.requestDate) = :month and year(t.requestDate) = :year and t.application = :application group by hour(t.requestDate)")
    List<List<Long>> countByHour(@Param("application") Application application, @Param("day") Integer day, @Param("month") Integer month, @Param("year") Integer year);

    @Query("SELECT t.application.id as application, count(t) as totalRequests, day(t.requestDate) as day, month(t.requestDate) as month, year(t.requestDate) as year FROM Request t where t.requestDate >= :from and t.requestDate <= :to and t.user.company = :company and t.application.id in :apps group by t.application.id, day(t.requestDate), month(t.requestDate), year(t.requestDate)")
    List<Map<String, Object>> findByApplicationsAndDateBetween(@Param("from") Date from,
                                                               @Param("to") Date to,
                                                               @Param("company") Company company,
                                                               @Param("apps") List<Long> apps);

}

