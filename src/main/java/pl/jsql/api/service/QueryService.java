package pl.jsql.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.jsql.api.dto.response.OptionsResponse;
import pl.jsql.api.exceptions.CryptographyException;
import pl.jsql.api.model.hashing.Application;
import pl.jsql.api.model.hashing.Query;
import pl.jsql.api.model.user.User;
import pl.jsql.api.repo.QueryDao;

import java.util.Date;

@Transactional
@Service
public class QueryService {

    @Autowired
    private QueryDao queryDao;

    @Autowired
    private HashingService hashingService;

    public void deleteForApplicationAndMember(Application application, User user) {
        queryDao.deleteByQuery(application, user);
    }

    public Query saveQueryPair(Application application, User user, String sqlQuery, String hash, Boolean dynamic) {

        Query query = new Query();
        query.application = application;
        query.user = user;
        query.query = sqlQuery;
        query.hash = hash;
        query.used = false;
        query.dynamic = dynamic;
        query.queryDate = new Date();

        return queryDao.save(query);

    }

    public void markQueryAsUsed(Query query) {
        queryDao.markQueryAsUsed(query);
    }

    public Query saveQueryPair(Application application, User user, String sqlQuery, String hash) {
        return saveQueryPair(application, user, sqlQuery, hash, false);
    }

    public Query getQuery(Application application, OptionsResponse optionsResponse, User user, String hash) {

        Query query = null;

        if (optionsResponse.allowedPlainQueries) {
            query = queryDao.findByApplicationAndUserAndQueryAndArchived(application, user, hash, false);

            if (query == null) {
                throw new CryptographyException("Query not found");
            }

            return query;

        }

        if (!hashingService.isValidSalt(optionsResponse, hash)) {
            throw new CryptographyException("Salt incorrect");
        }

        query = queryDao.findByApplicationAndUserAndHashAndArchived(application, user, hash, false);

        if (query == null) {
            throw new CryptographyException("Query not found");
        }

        return query;
    }

}
