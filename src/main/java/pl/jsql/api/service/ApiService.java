package pl.jsql.api.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pl.jsql.api.dto.request.QueryUpdateRequest
import pl.jsql.api.dto.response.MessageResponse
import pl.jsql.api.dto.response.QueryPairResponse
import pl.jsql.api.model.hashing.Query
import pl.jsql.api.model.user.User
import pl.jsql.api.repo.ApplicationDao
import pl.jsql.api.repo.MemberKeyDao
import pl.jsql.api.repo.QueryDao
import pl.jsql.api.security.service.SecurityService
import pl.jsql.api.service.HashingService
import pl.jsql.api.service.QueryService
import pl.jsql.api.service.StatsService
import pl.jsql.api.utils.Utils

import static pl.jsql.api.enums.HttpMessageEnum.*

@Transactional
@Service
public class  ApiService {

    @Autowired
    HashingService hashingService

    @Autowired
    QueryService queryService

    @Autowired
    StatsService statsService

    @Autowired
    QueryDao queryDao

    @Autowired
    ApplicationDao applicationDao

    @Autowired
    MemberKeyDao memberKeyDao

    @Autowired
    SecurityService securityService

    def getClientDatabaseOptions() {

        def clientOptions = hashingService.getClientOptions().data

        return [databaseDialect: clientOptions.databaseDialect.toString()]

    }

    MessageResponse updateQueriesById(Long id, QueryUpdateRequest queryUpdateRequest) {

        Query query = queryDao.findById(id).orElse(null)

        if (query == null) {
            return new MessageResponse(message: QUERY_WITH_ID_DOES_NOT_EXIST.getDescription())
        }

        Boolean canUpdate = true

        if (Utils.containsIgnoreCase(query.query, "select") && !Utils.containsIgnoreCase(queryUpdateRequest.query, "select")) {
            canUpdate = false
        } else if (Utils.containsIgnoreCase(query.query, "insert") && !Utils.containsIgnoreCase(queryUpdateRequest.query, "insert")) {
            canUpdate = false
        } else if (Utils.containsIgnoreCase(query.query, "update") && !Utils.containsIgnoreCase(queryUpdateRequest.query, "update")) {
            canUpdate = false
        } else if (Utils.containsIgnoreCase(query.query, "delete") && !Utils.containsIgnoreCase(queryUpdateRequest.query, "delete")) {
            canUpdate = false
        }

        if (canUpdate && query.application.apiKey == queryUpdateRequest.apiKey) {
            query.query = queryUpdateRequest.query
            queryDao.save(query)
        } else if (!canUpdate && query.application.apiKey == queryUpdateRequest.apiKey) {
            return new MessageResponse(message: QUERY_CHANGE_TYPE_ERROR.getDescription())
        } else {
            return new MessageResponse(message: API_KEY_DOES_NOT_MATCH.getDescription())
        }

        return new MessageResponse()
    }


    List<QueryPairResponse> getRequestQueriesResult(List<String> request) {
        return this.getRequestQueriesResult(request, false)
    }

    List<QueryPairResponse> getRequestQueriesResult(List<String> request, Boolean grouped) {


        def clientOptions = hashingService.getClientOptions().data

        def requestHashList = request

        List<QueryPairResponse> responseQueryHashList = new ArrayList<>()

        User member = memberKeyDao.findByKey(securityService.getMemberKey()).user

        if (grouped) {
            def resultHash = ""
            def query = ""
            requestHashList.each { String hash ->
                if (hash.length() > 0) {
                    resultHash += "=+" + hash
                    query += " " + queryService.getQuery(clientOptions.application, clientOptions.allowedPlainQueries, member, hash).query
                }
            }

            if (!queryDao.findByApplicationAndUserAndHash(clientOptions.application, member, resultHash)) {
                queryService.saveQueryPair(clientOptions.application, member, query, resultHash, true)
            }

            responseQueryHashList << new QueryPairResponse(token: resultHash, query: query)

            statsService.saveRequest(clientOptions.application, member, resultHash)

        } else {

            requestHashList.each { String hash ->
                if (hash.length() > 0) {

                    def query = queryService.getQuery(clientOptions.application, clientOptions.allowedPlainQueries, member, hash)

                    responseQueryHashList << new QueryPairResponse(token: hash, query: query.query)

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

        requestQueryList.each { String query ->

            if (query.length() > 0) {

                String hash = hashingService.hashQuery(clientOptions, query).trim()

                if (queryDao.findByApplicationAndUserAndHash(clientOptions.application, member, hash) == null) {
                    queryService.saveQueryPair(clientOptions.application, member, query, hash)
                }

                responseQueryHashList << new QueryPairResponse(token: hash, query: query)

            }

        }

        statsService.saveBuild(clientOptions.application, member, requestQueryList.size())

        return responseQueryHashList

    }

}
