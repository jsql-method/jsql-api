package pl.jsql.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.jsql.api.dto.request.QueryUpdateRequest;
import pl.jsql.api.dto.response.MessageResponse;
import pl.jsql.api.dto.response.OptionsResponse;
import pl.jsql.api.dto.response.QueryPairResponse;
import pl.jsql.api.dto.response.SimpleOptionsResponse;
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

    public List<QueryPairResponse> getRequestQueriesResult(List<String> request, Boolean grouped) {

        def clientOptions = hashingService.getClientOptions().data
        def requestHashList = request

        List<QueryPairResponse> responseQueryHashList = new ArrayList<>();

        User member = developerKeyDao.findByKey(securityService.getMemberKey()).user;

        if (grouped) {
            def resultHash = ""
            def query = ""
            requestHashList.each {
                String hash ->
                if (hash.length() > 0) {
                    resultHash += "=+" + hash
                    query += " " + queryService.getQuery(clientOptions.application, clientOptions.allowedPlainQueries, member, hash).query
                }
            }

            if (!queryDao.findByApplicationAndUserAndHash(clientOptions.application, member, resultHash)) {
                queryService.saveQueryPair(clientOptions.application, member, query, resultHash, true)
            }

            responseQueryHashList << new QueryPairResponse(token:resultHash, query:query)

            statsService.saveRequest(clientOptions.application, member, resultHash)

        } else {

            requestHashList.each {
                String hash ->
                if (hash.length() > 0) {

                    def query = queryService.getQuery(clientOptions.application, clientOptions.allowedPlainQueries, member, hash)

                    responseQueryHashList << new QueryPairResponse(token:hash, query:query.query)

                    statsService.saveRequest(clientOptions.application, member, hash)
                }

            }

        }

        return responseQueryHashList

    }

    List<QueryPairResponse> getRequestHashesResult(List<String> request) {

        def clientOptions = hashingService.getClientOptions().data
        def requestQueryList = request

        User member = memberKeyDao.findByKey(securityService.getMemberKey()).user

        if (clientOptions.removeQueriesAfterBuild) {

            queryService.deleteForApplicationAndMember(clientOptions.application, member)

        }

        List<QueryPairResponse> responseQueryHashList = new ArrayList<>()

        requestQueryList.each {
            String query ->

            if (query.length() > 0) {

                String hash = hashingService.hashQuery(clientOptions, query).trim()

                if (queryDao.findByApplicationAndUserAndHash(clientOptions.application, member, hash) == null) {
                    queryService.saveQueryPair(clientOptions.application, member, query, hash)
                }

                responseQueryHashList << new QueryPairResponse(token:hash, query:query)

            }

        }

        statsService.saveBuild(clientOptions.application, member, requestQueryList.size())

        return responseQueryHashList

    }

}
