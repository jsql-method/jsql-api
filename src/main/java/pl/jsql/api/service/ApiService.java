package pl.jsql.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.jsql.api.dto.request.QueryUpdateRequest;
import pl.jsql.api.dto.response.MessageResponse;
import pl.jsql.api.dto.response.OptionsResponse;
import pl.jsql.api.dto.response.QueryPairResponse;
import pl.jsql.api.dto.response.SimpleOptionsResponse;
import pl.jsql.api.exceptions.UnauthorizedException;
import pl.jsql.api.model.hashing.Application;
import pl.jsql.api.model.hashing.Query;
import pl.jsql.api.model.user.User;
import pl.jsql.api.repo.ApplicationDao;
import pl.jsql.api.repo.DeveloperKeyDao;
import pl.jsql.api.repo.QueryDao;
import pl.jsql.api.security.service.SecurityService;

import java.util.ArrayList;
import java.util.List;

@Transactional
@Service
public class ApiService {

    @Autowired
    private HashingService hashingService;

    @Autowired
    private QueryService queryService;

    @Autowired
    private StatsService statsService;

    @Autowired
    private QueryDao queryDao;

    @Autowired
    private ApplicationDao applicationDao;

    @Autowired
    private DeveloperKeyDao developerKeyDao;

    @Autowired
    private SecurityService securityService;

    public SimpleOptionsResponse getClientDatabaseOptions() {

        OptionsResponse optionsResponse = hashingService.getClientOptions();

        SimpleOptionsResponse simpleOptionsResponse = new SimpleOptionsResponse();
        simpleOptionsResponse.databaseDialect = optionsResponse.databaseDialect;

        if(optionsResponse.prod){
            simpleOptionsResponse.databaseConnectionTimeout = optionsResponse.productionDatabaseOptions.databaseConnectionTimeout;
        }else{
            simpleOptionsResponse.databaseConnectionTimeout = optionsResponse.developerDatabaseOptions.databaseConnectionTimeout;
        }

        return simpleOptionsResponse;

    }

    public MessageResponse updateQueriesById(Long queryId, QueryUpdateRequest queryUpdateRequest) {

        Query query = queryDao.findById(queryId).orElse(null);

        if (query == null) {
            return new MessageResponse("query_does_not_exists");
        }

        if (!query.application.id.equals(queryUpdateRequest.applicationId)) {
            return new MessageResponse("application_and_query_does_not_match");
        }

        if (query.application.id.equals(queryUpdateRequest.applicationId)) {
            query.query = queryUpdateRequest.query;
            queryDao.save(query);
        }

        return new MessageResponse();

    }

    public List<QueryPairResponse> getRequestQueriesResult(List<String> request) {
        return this.getRequestQueriesResult(request, false);
    }

    public List<QueryPairResponse> getRequestQueriesResult(List<String> requestHashes, Boolean grouped) {

        OptionsResponse optionsResponse = hashingService.getClientOptions();
        Application application = applicationDao.findByApiKey(optionsResponse.apiKey);

        if (application == null) {
            throw new UnauthorizedException();
        }

        List<QueryPairResponse> responseQueryHashList = new ArrayList<>();

        User developer = developerKeyDao.findByKey(securityService.getMemberKey()).user;

        if (grouped) {

            StringBuilder resultHash = new StringBuilder();
            StringBuilder query = new StringBuilder();

            for (String hash : requestHashes) {

                if (hash.length() > 0) {

                    Query query1 = queryService.getQuery(application, optionsResponse, developer, hash);
                    queryService.markQueryAsUsed(query1);

                    resultHash
                            .append("=+")
                            .append(hash);
                    query
                            .append(" ")
                            .append(query1.query);
                }

            }

            String resultHashString = resultHash.toString();
            String queryString = query.toString();

            if (queryDao.findByApplicationAndUserAndHash(application, developer, resultHashString) == null) {
                Query query1 = queryService.saveQueryPair(application, developer, queryString, resultHashString, true);
                queryService.markQueryAsUsed(query1);
            }

            responseQueryHashList.add(new QueryPairResponse(resultHashString, queryString));

            statsService.saveRequest(application, developer, resultHashString, queryString);

        } else {

            for (String hash : requestHashes) {
                if (hash.length() > 0) {

                    Query query = queryService.getQuery(application, optionsResponse, developer, hash);
                    queryService.markQueryAsUsed(query);

                    responseQueryHashList.add(new QueryPairResponse(hash, query.query));

                    statsService.saveRequest(application, developer, hash, query.query);

                }

            }

        }

        return responseQueryHashList;

    }

    public List<QueryPairResponse> getRequestHashesResult(List<String> requestQueries) {

        OptionsResponse optionsResponse = hashingService.getClientOptions();
        Application application = applicationDao.findByApiKey(optionsResponse.apiKey);

        if (application == null) {
            throw new UnauthorizedException();
        }

        User developer = developerKeyDao.findByKey(securityService.getMemberKey()).user;

        if (optionsResponse.removeQueriesAfterBuild) {
            queryService.deleteForApplicationAndMember(application, developer);
        }

        List<QueryPairResponse> responseQueryHashList = new ArrayList<>();

        for (String query : requestQueries) {

            if (query.length() > 0) {

                String hash = hashingService.hashQuery(optionsResponse, query).trim();

                if (queryDao.findByApplicationAndUserAndHash(application, developer, hash) == null) {
                   queryService.saveQueryPair(application, developer, query, hash);
                }

                responseQueryHashList.add(new QueryPairResponse(hash, query));

            }

        }


        statsService.saveBuild(application, developer, requestQueries.size());

        return responseQueryHashList;

    }

}
