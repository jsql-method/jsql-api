package pl.jsql.api.repo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.jsql.api.dto.response.BuildResponse;
import pl.jsql.api.dto.response.BuildsChartDataResponse;
import pl.jsql.api.dto.response.RequestResponse;
import pl.jsql.api.model.user.Company;
import pl.jsql.api.model.user.User;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Service
public class BuildExtendedDao {

    @Autowired
    private EntityManager em;

    public Long countBuildsForCompany(Company company, Date dateFrom, Date dateTo, List<Long> applications, List<Long> developers) {

        StringBuilder builder = new StringBuilder();

        builder.append("select count(b) from Build b where b.user.company = :company and b.hashingDate >= :dateFrom and b.hashingDate <= :dateTo ");

        if (applications.size() > 0) {

            builder.append("and (");

            for(Long id : applications){
                builder.append("b.application.id = ")
                        .append(id)
                        .append(" or ");
            }

            builder.append(") ");

        }

        if (developers.size() > 0) {

            builder.append("and (");

            for(Long id : developers){
                builder.append("b.user.id = ")
                        .append(id)
                        .append(" or ");
            }

            builder.append(") ");

        }

        String queryStr = builder.toString();
        queryStr = queryStr.replace(" or )", ")");

        Query query = em.createQuery(queryStr, Long.class);

        query.setParameter("company", company);
        query.setParameter("dateFrom", dateFrom);
        query.setParameter("dateTo", dateTo);

        return (Long) query.getSingleResult();

    }

    public Long countBuildsForDeveloper(User currentUser, Date dateFrom, Date dateTo, List<Long> applications) {

        StringBuilder builder = new StringBuilder();

        builder.append("select count(b) from Build b where b.user = :currentUser and b.hashingDate >= :dateFrom and b.hashingDate <= :dateTo ");

        if (applications.size() > 0) {

            builder.append("and (");

            for(Long id : applications){
                builder.append("b.application.id = ")
                        .append(id)
                        .append(" or ");
            }

            builder.append(") ");

        }

        String queryStr = builder.toString();
        queryStr = queryStr.replace(" or )", ")");

        Query query = em.createQuery(queryStr, Long.class);

        query.setParameter("currentUser", currentUser);
        query.setParameter("dateFrom", dateFrom);
        query.setParameter("dateTo", dateTo);

        return (Long) query.getSingleResult();

    }

    public List<BuildResponse> selectChartBuildsForCompany(Company company, Date dateFrom, Date dateTo, List<Long> applications, List<Long> developers) {

        StringBuilder builder = new StringBuilder();

        builder.append("select new pl.jsql.api.dto.response.BuildResponse(b.application.name, concat(b.user.firstName,' ',b.user.lastName), b.hashingDate, b.queriesCount) ");
        builder.append("from Build b ");
        builder.append("where b.user.company = :company and b.hashingDate >= :dateFrom and b.hashingDate <= :dateTo ");

        if (applications.size() > 0) {

            builder.append("and (");

            for(Long id : applications){
                builder.append("b.application.id = ")
                        .append(id)
                        .append(" or ");
            }

            builder.append(") ");

        }

        if (developers.size() > 0) {

            builder.append("and (");

            for(Long id : developers){
                builder.append("b.user.id = ")
                        .append(id)
                        .append(" or ");
            }

            builder.append(") ");

        }

        String queryStr = builder.toString();
        queryStr = queryStr.replace(" or )", ")");

        Query query = em.createQuery(queryStr, BuildResponse.class);

        query.setParameter("company", company);
        query.setParameter("dateFrom", dateFrom);
        query.setParameter("dateTo", dateTo);

        return (List<BuildResponse>) query.getResultList();

    }

    public List<BuildResponse> selectBuildsForCompany(Company company, Date dateFrom, Date dateTo, List<Long> applications, List<Long> developers, Pageable pageable) {

        StringBuilder builder = new StringBuilder();

        builder.append("select new pl.jsql.api.dto.response.BuildResponse(b.application.name, b.application.id, concat(b.user.firstName,' ',b.user.lastName), b.user.id, b.hashingDate, b.queriesCount) ");
        builder.append("from Build b ");
        builder.append("where b.user.company = :company and b.hashingDate >= :dateFrom and b.hashingDate <= :dateTo ");

        if (applications.size() > 0) {

            builder.append("and (");

            for(Long id : applications){
                builder.append("b.application.id = ")
                        .append(id)
                        .append(" or ");
            }

            builder.append(") ");

        }

        if (developers.size() > 0) {

            builder.append("and (");

            for(Long id : developers){
                builder.append("b.user.id = ")
                        .append(id)
                        .append(" or ");
            }

            builder.append(") ");

        }

        builder.append(" order by b.hashingDate desc");

        String queryStr = builder.toString();
        queryStr = queryStr.replace(" or )", ")");

        Query query = em.createQuery(queryStr, BuildResponse.class);

        query.setParameter("company", company);
        query.setParameter("dateFrom", dateFrom);
        query.setParameter("dateTo", dateTo);

        if(pageable != null){
            query.setMaxResults(pageable.getPageSize());
            query.setFirstResult((int) pageable.getOffset());
        }

        return (List<BuildResponse>) query.getResultList();

    }

    public List<BuildResponse> selectBuildsForDeveloper(User currentUser, Date dateFrom, Date dateTo, List<Long> applications, Pageable pageable) {

        StringBuilder builder = new StringBuilder();

        builder.append("select new pl.jsql.api.dto.response.BuildResponse(b.application.name, b.application.id, concat(b.user.firstName,' ',b.user.lastName), b.user.id, b.hashingDate, b.queriesCount) ");
        builder.append("from Build b ");
        builder.append("where b.user = :currentUser and b.hashingDate >= :dateFrom and b.hashingDate <= :dateTo ");

        if (applications.size() > 0) {

            builder.append("and (");

            for(Long id : applications){
                builder.append("b.application.id = ")
                        .append(id)
                        .append(" or ");
            }

            builder.append(") ");

        }

        builder.append(" order by b.hashingDate desc");

        String queryStr = builder.toString();
        queryStr = queryStr.replace(" or )", ")");

        Query query = em.createQuery(queryStr, BuildResponse.class);

        query.setParameter("currentUser", currentUser);
        query.setParameter("dateFrom", dateFrom);
        query.setParameter("dateTo", dateTo);

        query.setMaxResults(pageable.getPageSize());
        query.setFirstResult((int) pageable.getOffset());

        return (List<BuildResponse>) query.getResultList();

    }

    public List<BuildResponse> selectChartBuildsForDeveloper(User currentUser, Date dateFrom, Date dateTo, List<Long> applications) {

        StringBuilder builder = new StringBuilder();

        builder.append("select new pl.jsql.api.dto.response.BuildResponse(b.application.name, concat(b.user.firstName,' ',b.user.lastName), b.hashingDate, b.queriesCount) ");
        builder.append("from Build b ");
        builder.append("where b.user = :currentUser and b.hashingDate >= :dateFrom and b.hashingDate <= :dateTo ");

        if (applications.size() > 0) {

            builder.append("and (");

            for(Long id : applications){
                builder.append("b.application.id = ")
                        .append(id)
                        .append(" or ");
            }

            builder.append(") ");

        }

        String queryStr = builder.toString();
        queryStr = queryStr.replace(" or )", ")");

        Query query = em.createQuery(queryStr, BuildResponse.class);

        query.setParameter("currentUser", currentUser);
        query.setParameter("dateFrom", dateFrom);
        query.setParameter("dateTo", dateTo);

        return (List<BuildResponse>) query.getResultList();

    }

}
