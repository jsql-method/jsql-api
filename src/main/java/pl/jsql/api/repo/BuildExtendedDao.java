package pl.jsql.api.repo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.jsql.api.dto.response.BuildResponse;
import pl.jsql.api.model.user.Company;
import pl.jsql.api.model.user.User;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;

@Service
public class BuildExtendedDao {

    @Autowired
    private EntityManager em;

    public Integer countBuildsForCompany(Company company, Date dateFrom, Date dateTo, List<Long> applications, List<Long> developers) {

        StringBuilder builder = new StringBuilder();

        builder.append("select count(b) from Build b where b.user.company = :company and b.hashingDate >= :dateFrom and b.hashingDate <= :dateTo ");

        if (applications.size() > 0) {
            builder.append("and b.application.id in :applications ");
        }

        if (developers.size() > 0) {
            builder.append("and b.user.id in :developers ");
        }

        Query query = em.createQuery(builder.toString(), Integer.class);

        query.setParameter("company", company);
        query.setParameter("dateFrom", dateFrom);
        query.setParameter("dateTo", dateTo);

        if (applications.size() > 0) {
            query.setParameter("applications", applications);
        }

        if (developers.size() > 0) {
            query.setParameter("developers", developers);
        }

        return (Integer) query.getSingleResult();

    }

    public Integer countBuildsForDeveloper(User currentUser, Date dateFrom, Date dateTo, List<Long> applications) {

        StringBuilder builder = new StringBuilder();

        builder.append("select count(b) from Build b where b.user = :currentUser and b.hashingDate >= :dateFrom and b.hashingDate <= :dateTo ");

        if (applications.size() > 0) {
            builder.append("and b.application.id in :applications ");
        }

        Query query = em.createQuery(builder.toString(), Integer.class);

        query.setParameter("currentUser", currentUser);
        query.setParameter("dateFrom", dateFrom);
        query.setParameter("dateTo", dateTo);

        if (applications.size() > 0) {
            query.setParameter("applications", applications);
        }

        return (Integer) query.getSingleResult();

    }

    public List<BuildResponse> selectBuildsForCompany(Company company, Date dateFrom, Date dateTo, List<Long> applications, List<Long> developers, Pageable pageable) {

        StringBuilder builder = new StringBuilder();

        builder.append("select new pl.jsql.api.dto.response.BuildResponse(b.application.name, b.application.id, concat(b.user.firstName,' ',b.user.lastName), b.user.id, b.hashingDate, b.queriesCount) ");
        builder.append("from Build b ");
        builder.append("where b.user.company = :company and b.hashingDate >= :dateFrom and b.hashingDate <= :dateTo ");

        if (applications.size() > 0) {
            builder.append("and b.application.id in :applications ");
        }

        if (developers.size() > 0) {
            builder.append("and b.user.id in :developers ");
        }

        Query query = em.createQuery(builder.toString(), BuildResponse.class);

        query.setParameter("company", company);
        query.setParameter("dateFrom", dateFrom);
        query.setParameter("dateTo", dateTo);

        if (applications.size() > 0) {
            query.setParameter("applications", applications);
        }

        if (developers.size() > 0) {
            query.setParameter("developers", developers);
        }

        query.setMaxResults(pageable.getPageSize());
        query.setFirstResult((int) pageable.getOffset());

        return (List<BuildResponse>) query.getResultList();

    }

    public List<BuildResponse> selectBuildsForDeveloper(User currentUser, Date dateFrom, Date dateTo, List<Long> applications, Pageable pageable) {

        StringBuilder builder = new StringBuilder();

        builder.append("select new pl.jsql.api.dto.response.BuildResponse(b.application.name, b.application.id, concat(b.user.firstName,' ',b.user.lastName), b.user.id, b.hashingDate, b.queriesCount) ");
        builder.append("from Build b ");
        builder.append("where b.user = :currentUser and b.hashingDate >= :dateFrom and b.hashingDate <= :dateTo ");

        if (applications.size() > 0) {
            builder.append("and b.application.id in :applications ");
        }

        Query query = em.createQuery(builder.toString(), BuildResponse.class);

        query.setParameter("currentUser", currentUser);
        query.setParameter("dateFrom", dateFrom);
        query.setParameter("dateTo", dateTo);

        if (applications.size() > 0) {
            query.setParameter("applications", applications);
        }

        query.setMaxResults(pageable.getPageSize());
        query.setFirstResult((int) pageable.getOffset());

        return (List<BuildResponse>) query.getResultList();

    }

}
