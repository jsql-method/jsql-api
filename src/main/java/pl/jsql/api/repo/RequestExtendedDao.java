package pl.jsql.api.repo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.jsql.api.dto.response.RequestResponse;
import pl.jsql.api.model.user.Company;
import pl.jsql.api.model.user.User;

import javax.persistence.EntityManager;
import java.util.Date;
import java.util.List;

@Service
public class RequestExtendedDao {

    @Autowired
    private EntityManager em;

    public Integer countRequestsForCompany(Company company, Date dateFrom, Date dateTo, List<Long> applications) {

        StringBuilder builder = new StringBuilder();

        builder.append("select count(r) from Request r where r.application.companyAdmin.company = :company and r.requestDate >= :dateFrom and r.requestDate <= :dateTo ");

        if (applications.size() > 0) {
            builder.append("and r.application.id in :applications ");
        }

        javax.persistence.Query query = em.createQuery(builder.toString(), Integer.class);

        query.setParameter("company", company);
        query.setParameter("dateFrom", dateFrom);
        query.setParameter("dateTo", dateTo);

        if (applications.size() > 0) {
            query.setParameter("applications", applications);
        }

        return (Integer) query.getSingleResult();

    }

    public Integer countRequestsForDeveloper(User developer, Date dateFrom, Date dateTo, List<Long> applications) {

        StringBuilder builder = new StringBuilder();

        builder.append("select count(r) from Request r, ApplicationDevelopers ad where r.application = ad.application and ad.developer = :developer and r.requestDate >= :dateFrom and r.requestDate <= :dateTo ");

        if (applications.size() > 0) {
            builder.append("and r.application.id in :applications ");
        }

        javax.persistence.Query query = em.createQuery(builder.toString(), Integer.class);

        query.setParameter("developer", developer);
        query.setParameter("dateFrom", dateFrom);
        query.setParameter("dateTo", dateTo);

        if (applications.size() > 0) {
            query.setParameter("applications", applications);
        }

        return (Integer) query.getSingleResult();

    }

    public List<RequestResponse> selectRequestsForCompany(Company company, Date dateFrom, Date dateTo, List<Long> applications, Pageable pageable) {

        StringBuilder builder = new StringBuilder();

        builder.append("select new pl.jsql.api.dto.response.RequestResponse(r.application.name, r.application.id, r.requestDate) ");
        builder.append("from Request r where ");
        builder.append("r.application.companyAdmin.company = :company and r.requestDate >= :dateFrom and r.requestDate <= :dateTo ");

        if (applications.size() > 0) {
            builder.append("and r.application.id in :applications ");
        }

        javax.persistence.Query query = em.createQuery(builder.toString(), RequestResponse.class);

        query.setParameter("company", company);
        query.setParameter("dateFrom", dateFrom);
        query.setParameter("dateTo", dateTo);

        if (applications.size() > 0) {
            query.setParameter("applications", applications);
        }

        query.setMaxResults(pageable.getPageSize());
        query.setFirstResult((int) pageable.getOffset());

        return (List<RequestResponse>) query.getResultList();

    }

    public List<RequestResponse> selectRequestsForDeveloper(User developer, Date dateFrom, Date dateTo, List<Long> applications, Pageable pageable) {

        StringBuilder builder = new StringBuilder();

        builder.append("select new pl.jsql.api.dto.response.RequestResponse(r.application.name, r.application.id, r.requestDate) ");
        builder.append("from Request r, ApplicationDevelopers ad ");
        builder.append("where r.application = ad.application and ad.developer = :developer and r.requestDate >= :dateFrom and r.requestDate <= :dateTo ");

        if (applications.size() > 0) {
            builder.append("and r.application.id in :applications ");
        }

        javax.persistence.Query query = em.createQuery(builder.toString(), RequestResponse.class);

        query.setParameter("developer", developer);
        query.setParameter("dateFrom", dateFrom);
        query.setParameter("dateTo", dateTo);

        if (applications.size() > 0) {
            query.setParameter("applications", applications);
        }

        query.setMaxResults(pageable.getPageSize());
        query.setFirstResult((int) pageable.getOffset());

        return (List<RequestResponse>) query.getResultList();

    }

}
