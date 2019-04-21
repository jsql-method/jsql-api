package pl.jsql.api.repo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import pl.jsql.api.dto.response.BuildResponse;
import pl.jsql.api.dto.response.QueriesChartDataResponse;
import pl.jsql.api.dto.response.QueryResponse;
import pl.jsql.api.model.user.Company;
import pl.jsql.api.model.user.User;

import javax.persistence.EntityManager;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Service
public class QueryExtendedDao {

    @Autowired
    private EntityManager em;

    public Long countQueriesForCompany(Company company, Date dateFrom, Date dateTo, List<Long> applications, List<Long> developers, Boolean dynamic, Boolean used, String search){

        StringBuilder builder = new StringBuilder();

        builder.append("SELECT count(t) FROM Query t where t.queryDate >= :dateFrom and t.queryDate <= :dateTo ");
        builder.append("and t.user.company = :company ");

        if(used != null){
            builder.append(" and t.used = :used ");
        }

        if(dynamic != null){
            builder.append(" and t.dynamic = :dynamic ");
        }

        if(search != null){
            builder.append(" and (t.hash like :search or t.query like :search) ");
        }

        if (applications.size() > 0) {

            builder.append("and (");

            for(Long id : applications){
                builder.append("t.application.id = ")
                        .append(id)
                        .append(" or ");
            }

            builder.append(") ");

        }

        if (developers.size() > 0) {

            builder.append("and (");

            for(Long id : developers){
                builder.append("t.user.id = ")
                        .append(id)
                        .append(" or ");
            }

            builder.append(") ");

        }

        String queryStr = builder.toString();
        queryStr = queryStr.replace(" or )", ")");

        javax.persistence.Query query = em.createQuery(queryStr, Long.class);

        query.setParameter("company", company);
        query.setParameter("dateFrom", dateFrom);
        query.setParameter("dateTo", dateTo);

        if(dynamic != null){
            query.setParameter("dynamic", dynamic);
        }

        if(used != null){
            query.setParameter("used", used);
        }

        if(search != null){
            query.setParameter("search", "%"+search+"%");
        }

        return (Long) query.getSingleResult();

    }

    public Long countQueriesForDeveloper(User currentUser, Date dateFrom, Date dateTo, List<Long> applications, Boolean dynamic, Boolean used, String search){

        StringBuilder builder = new StringBuilder();

        builder.append("SELECT count(t) FROM Query t where t.queryDate >= :dateFrom and t.queryDate <= :dateTo ");
        builder.append("and t.user = :currentUser  ");

        if(used != null){
            builder.append(" and t.used = :used ");
        }

        if(dynamic != null){
            builder.append(" and t.dynamic = :dynamic ");
        }

        if(search != null){
            builder.append(" and (t.hash like :search or t.query like :search) ");
        }

        if (applications.size() > 0) {

            builder.append("and (");

            for(Long id : applications){
                builder.append("t.application.id = ")
                        .append(id)
                        .append(" or ");
            }

            builder.append(") ");

        }

        String queryStr = builder.toString();
        queryStr = queryStr.replace(" or )", ")");

        javax.persistence.Query query = em.createQuery(queryStr, Long.class);

        query.setParameter("currentUser", currentUser);
        query.setParameter("dateFrom", dateFrom);
        query.setParameter("dateTo", dateTo);

        if(dynamic != null){
            query.setParameter("dynamic", dynamic);
        }

        if(used != null){
            query.setParameter("used", used);
        }

        if(search != null){
            query.setParameter("search", "%"+search+"%");
        }

        return (Long) query.getSingleResult();

    }

    public List<QueryResponse> selectQueriesForCompany(Company company, Date dateFrom, Date dateTo, List<Long> applications, List<Long> developers, Boolean dynamic, Boolean used, String search, Pageable pageable){

        StringBuilder builder = new StringBuilder();

        builder.append("SELECT new pl.jsql.api.dto.response.QueryResponse(t.id, t.query, t.hash, t.queryDate, t.used, t.dynamic, concat(t.user.firstName, ' ', t.user.lastName), t.application.name, t.application.id) ");
        builder.append("FROM Query t where t.queryDate >= :dateFrom and t.queryDate <= :dateTo ");
        builder.append("and t.user.company = :company ");

        if(used != null){
            builder.append(" and t.used = :used ");
        }

        if(dynamic != null){
            builder.append(" and t.dynamic = :dynamic ");
        }

        if(search != null){
            builder.append(" and (t.hash like :search or t.query like :search) ");
        }

        if (applications.size() > 0) {

            builder.append("and (");

            for(Long id : applications){
                builder.append("t.application.id = ")
                        .append(id)
                        .append(" or ");
            }

            builder.append(") ");

        }

        if (developers.size() > 0) {

            builder.append("and (");

            for(Long id : developers){
                builder.append("t.user.id = ")
                        .append(id)
                        .append(" or ");
            }

            builder.append(") ");

        }

        builder.append(" order by t.queryDate desc");

        String queryStr = builder.toString();
        queryStr = queryStr.replace(" or )", ")");

        javax.persistence.Query query = em.createQuery(queryStr, QueryResponse.class);

        query.setParameter("company", company);
        query.setParameter("dateFrom", dateFrom);
        query.setParameter("dateTo", dateTo);

        if(dynamic != null){
            query.setParameter("dynamic", dynamic);
        }

        if(used != null){
            query.setParameter("used", used);
        }

        if(search != null){
            query.setParameter("search", "%"+search+"%");
        }

        query.setMaxResults(pageable.getPageSize());
        query.setFirstResult((int) pageable.getOffset());

        return (List<QueryResponse>) query.getResultList();

    }

    public List<QueryResponse> selectQueriesForDeveloper(User currentUser, Date dateFrom, Date dateTo, List<Long> applications, Boolean dynamic, Boolean used, String search, Pageable pageable){

        StringBuilder builder = new StringBuilder();

        builder.append("SELECT new pl.jsql.api.dto.response.QueryResponse(t.id, t.query, t.hash, t.queryDate, t.used, t.dynamic, concat(t.user.firstName, ' ', t.user.lastName), t.application.name, t.application.id) ");
        builder.append("FROM Query t where t.queryDate >= :dateFrom and t.queryDate <= :dateTo ");
        builder.append("and t.user = :currentUser ");

        if(used != null){
            builder.append(" and t.used = :used ");
        }

        if(dynamic != null){
            builder.append(" and t.dynamic = :dynamic ");
        }

        if(search != null){
            builder.append(" and (t.hash like :search or t.query like :search) ");
        }

        if (applications.size() > 0) {

            builder.append("and (");

            for(Long id : applications){
                builder.append("t.application.id = ")
                        .append(id)
                        .append(" or ");
            }

            builder.append(") ");

        }


        builder.append(" order by t.queryDate desc");

        String queryStr = builder.toString();
        queryStr = queryStr.replace(" or )", ")");

        javax.persistence.Query query = em.createQuery(queryStr, QueryResponse.class);

        query.setParameter("currentUser", currentUser);
        query.setParameter("dateFrom", dateFrom);
        query.setParameter("dateTo", dateTo);

        if(dynamic != null){
            query.setParameter("dynamic", dynamic);
        }

        if(used != null){
            query.setParameter("used", used);
        }

        if(search != null){
            query.setParameter("search", "%"+search+"%");
        }

        query.setMaxResults(pageable.getPageSize());
        query.setFirstResult((int) pageable.getOffset());

        return (List<QueryResponse>) query.getResultList();

    }


    public List<QueriesChartDataResponse> selectChartQueriesForCompany(Company company, Date dateFrom, Date dateTo, List<Long> applications, List<Long> developers, Boolean dynamic, Boolean used, String search) {

        StringBuilder builder = new StringBuilder();

        builder.append("SELECT new pl.jsql.api.dto.response.QueriesChartDataResponse(t.id, t.queryDate, concat(t.user.firstName, ' ', t.user.lastName), t.application.name) ");
        builder.append("FROM Query t where t.queryDate >= :dateFrom and t.queryDate <= :dateTo ");
        builder.append("and t.user.company = :company ");

        if(used != null){
            builder.append(" and t.used = :used ");
        }

        if(dynamic != null){
            builder.append(" and t.dynamic = :dynamic ");
        }

        if(search != null){
            builder.append(" and (t.hash like :search or t.query like :search) ");
        }

        if (applications.size() > 0) {

            builder.append("and (");

            for(Long id : applications){
                builder.append("t.application.id = ")
                        .append(id)
                        .append(" or ");
            }

            builder.append(") ");

        }

        if (developers.size() > 0) {

            builder.append("and (");

            for(Long id : developers){
                builder.append("t.user.id = ")
                        .append(id)
                        .append(" or ");
            }

            builder.append(") ");

        }

        String queryStr = builder.toString();
        queryStr = queryStr.replace(" or )", ")");

        javax.persistence.Query query = em.createQuery(queryStr, QueriesChartDataResponse.class);

        query.setParameter("company", company);
        query.setParameter("dateFrom", dateFrom);
        query.setParameter("dateTo", dateTo);

        if(dynamic != null){
            query.setParameter("dynamic", dynamic);
        }

        if(used != null){
            query.setParameter("used", used);
        }

        if(search != null){
            query.setParameter("search", "%"+search+"%");
        }

        return (List<QueriesChartDataResponse>) query.getResultList();

    }


    public List<QueriesChartDataResponse> selectChartQueriesForDeveloper(User currentUser, Date dateFrom, Date dateTo, List<Long> applications, Boolean dynamic, Boolean used, String search) {

        StringBuilder builder = new StringBuilder();

        builder.append("SELECT new pl.jsql.api.dto.response.QueriesChartDataResponse(t.id, t.queryDate, concat(t.user.firstName, ' ', t.user.lastName), t.application.name) ");
        builder.append("FROM Query t where t.queryDate >= :dateFrom and t.queryDate <= :dateTo ");
        builder.append("and t.user = :currentUser  ");

        if(used != null){
            builder.append(" and t.used = :used ");
        }

        if(dynamic != null){
            builder.append(" and t.dynamic = :dynamic ");
        }

        if(search != null){
            builder.append(" and (t.hash like :search or t.query like :search) ");
        }

        if (applications.size() > 0) {

            builder.append("and (");

            for(Long id : applications){
                builder.append("t.application.id = ")
                        .append(id)
                        .append(" or ");
            }

            builder.append(") ");

        }

        String queryStr = builder.toString();
        queryStr = queryStr.replace(" or )", ")");

        javax.persistence.Query query = em.createQuery(queryStr, QueriesChartDataResponse.class);

        query.setParameter("currentUser", currentUser);
        query.setParameter("dateFrom", dateFrom);
        query.setParameter("dateTo", dateTo);

        if(dynamic != null){
            query.setParameter("dynamic", dynamic);
        }

        if(used != null){
            query.setParameter("used", used);
        }

        if(search != null){
            query.setParameter("search", "%"+search+"%");
        }

        return (List<QueriesChartDataResponse>) query.getResultList();

    }
}
