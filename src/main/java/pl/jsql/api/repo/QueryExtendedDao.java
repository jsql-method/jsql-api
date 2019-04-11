package pl.jsql.api.repo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import pl.jsql.api.dto.response.BuildResponse;
import pl.jsql.api.dto.response.QueryResponse;
import pl.jsql.api.model.user.Company;
import pl.jsql.api.model.user.User;

import javax.persistence.EntityManager;
import java.util.Date;
import java.util.List;

@Service
public class QueryExtendedDao {

    @Autowired
    private EntityManager em;

    public Integer countQueriesForCompany(Company company, Date dateFrom, Date dateTo, List<Long> applications, List<Long> developers, Boolean dynamic, Boolean used){

        StringBuilder builder = new StringBuilder();

        builder.append("SELECT count(t) FROM Query t where t.queryDate >= :dateFrom and t.queryDate <= :dateTo ");
        builder.append("and t.user.company = :company and t.used = :used and t.dynamic = :dynamic ");

        if (applications.size() > 0) {
            builder.append("and t.application.id in :applications ");
        }

        if (developers.size() > 0) {
            builder.append("and t.user.id in :developers ");
        }

        javax.persistence.Query query = em.createQuery(builder.toString(), Integer.class);

        query.setParameter("company", company);
        query.setParameter("dateFrom", dateFrom);
        query.setParameter("dateTo", dateTo);
        query.setParameter("dynamic", dynamic);
        query.setParameter("used", used);

        if (applications.size() > 0) {
            query.setParameter("applications", applications);
        }

        if (developers.size() > 0) {
            query.setParameter("developers", developers);
        }

        return (Integer) query.getSingleResult();

    }

    public Integer countQueriesForDeveloper(User currentUser, Date dateFrom, Date dateTo, List<Long> applications, Boolean dynamic, Boolean used){

        StringBuilder builder = new StringBuilder();

        builder.append("SELECT count(t) FROM Query t where t.queryDate >= :from and t.queryDate <= :to ");
        builder.append("and t.user = :currentUser and t.used = :used and t.dynamic = :dynamic ");

        if (applications.size() > 0) {
            builder.append("and t.application.id in :applications ");
        }

        javax.persistence.Query query = em.createQuery(builder.toString(), Integer.class);

        query.setParameter("currentUser", currentUser);
        query.setParameter("dateFrom", dateFrom);
        query.setParameter("dateTo", dateTo);
        query.setParameter("dynamic", dynamic);
        query.setParameter("used", used);

        if (applications.size() > 0) {
            query.setParameter("applications", applications);
        }

        return (Integer) query.getSingleResult();

    }

    public List<QueryResponse> selectQueriesForCompany(Company company, Date dateFrom, Date dateTo, List<Long> applications, List<Long> developers, Boolean dynamic, Boolean used, Pageable pageable){

        StringBuilder builder = new StringBuilder();

        builder.append("SELECT new pl.jsql.api.dto.response.QueryResponse(t.id, t.query, t.hash, t.queryDate, t.used, t.dynamic, concat(t.user.firstName, ' ', t.user.lastName), t.application.name, t.application.id) ");
        builder.append("FROM Query t where t.queryDate >= :dateFrom and t.queryDate <= :dateTo ");
        builder.append("and t.user.company = :company and t.used = :used and t.dynamic = :dynamic ");

        if (applications.size() > 0) {
            builder.append("and t.application.id in :applications ");
        }

        if (developers.size() > 0) {
            builder.append("and t.user.id in :developers ");
        }

        javax.persistence.Query query = em.createQuery(builder.toString(), QueryResponse.class);

        query.setParameter("company", company);
        query.setParameter("dateFrom", dateFrom);
        query.setParameter("dateTo", dateTo);
        query.setParameter("dynamic", dynamic);
        query.setParameter("used", used);

        if (applications.size() > 0) {
            query.setParameter("applications", applications);
        }

        if (developers.size() > 0) {
            query.setParameter("developers", developers);
        }

        query.setMaxResults(pageable.getPageSize());
        query.setFirstResult((int) pageable.getOffset());

        return (List<QueryResponse>) query.getResultList();

    }

    public List<QueryResponse> selectQueriesForDeveloper(User currentUser, Date dateFrom, Date dateTo, List<Long> applications, Boolean dynamic, Boolean used, Pageable pageable){

        StringBuilder builder = new StringBuilder();

        builder.append("SELECT new pl.jsql.api.dto.response.QueryResponse(t.id, t.query, t.hash, t.queryDate, t.used, t.dynamic, concat(t.user.firstName, ' ', t.user.lastName), t.application.name, t.application.id) ");
        builder.append("FROM Query t where t.queryDate >= :dateFrom and t.queryDate <= :dateTo ");
        builder.append("and t.user = :currentUser and t.used = :used and t.dynamic = :dynamic ");

        if (applications.size() > 0) {
            builder.append("and t.application.id in :applications ");
        }

        javax.persistence.Query query = em.createQuery(builder.toString(), QueryResponse.class);

        query.setParameter("currentUser", currentUser);
        query.setParameter("dateFrom", dateFrom);
        query.setParameter("dateTo", dateTo);
        query.setParameter("dynamic", dynamic);
        query.setParameter("used", used);

        if (applications.size() > 0) {
            query.setParameter("applications", applications);
        }

        query.setMaxResults(pageable.getPageSize());
        query.setFirstResult((int) pageable.getOffset());

        return (List<QueryResponse>) query.getResultList();

    }


}
