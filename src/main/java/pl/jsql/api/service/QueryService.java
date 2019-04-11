package pl.jsql.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.jsql.api.model.hashing.Application;
import pl.jsql.api.model.hashing.Query;
import pl.jsql.api.model.user.User;
import pl.jsql.api.repo.QueryDao;

import java.util.Date;

@Transactional
@Service
public class QueryService {

    @Autowired
    QueryDao queryDao;

    public void deleteForApplicationAndMember(Application application, User user) {
        queryDao.deleteByQuery(application, user);
    }

    public void saveQueryPair(Application application, User user, String sqlQuery, String hash, Boolean dynamic) {

        Query query = new Query();
        query.application = application;
        query.user = user;
        query.query = sqlQuery;
        query.hash = hash;
        query.used = true;
        query.dynamic = dynamic;
        query.queryDate = new Date();

        queryDao.save(query);

    }

    public void saveQueryPair(Application application, User user, String sqlQuery, String hash) {
        saveQueryPair(application, user, sqlQuery, hash, false);
    }

    public Query getQuery(Application application, Boolean allowedPlainQueries, User user, String hash) {

        if (allowedPlainQueries) {
            return queryDao.findByApplicationAndUserAndQuery(application, user, hash);
        }

        return queryDao.findByApplicationAndUserAndHash(application, user, hash);

    }

}
