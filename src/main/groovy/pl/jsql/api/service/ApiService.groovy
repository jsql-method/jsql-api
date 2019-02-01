package pl.jsql.api.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pl.jsql.api.dto.QueryUpdateRequest
import pl.jsql.api.enums.RoleTypeEnum
import pl.jsql.api.model.hashing.Application
import pl.jsql.api.model.hashing.Query
import pl.jsql.api.model.user.User
import pl.jsql.api.repo.ApplicationDao
import pl.jsql.api.repo.MemberKeyDao
import pl.jsql.api.repo.QueryDao
import pl.jsql.api.utils.Utils

import java.text.SimpleDateFormat

import static pl.jsql.api.enums.HttpMessageEnum.*

@Transactional
@Service
class ApiService {

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


    def getClientDatabaseOptions(String memberKey, String apiKey) {


        def clientOptions = hashingService.getClientOptions(apiKey).data

        return [code: SUCCESS.getCode(), data: [
                databaseDialect    : clientOptions.databaseDialect.toString(),
                applicationLanguage: clientOptions.applicationLanguage.toString()]
        ]
    }

    def updateQueriesById(Long id, QueryUpdateRequest queryUpdateRequest) {

        Query query = queryDao.findById(id).orElse(null)

        if (query == null) {
            return [code: QUERY_WITH_ID_DOES_NOT_EXIST.getCode(), data: QUERY_WITH_ID_DOES_NOT_EXIST.getDescription()]
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
            return [code: QUERY_CHANGE_TYPE_ERROR.getCode(), data: QUERY_CHANGE_TYPE_ERROR.getDescription()]
        } else {
            return [code: API_KEY_DOES_NOT_MATCH.getCode(), data: API_KEY_DOES_NOT_MATCH.getDescription()]
        }

        return [code: SUCCESS.getCode(), data: null]
    }


    def getRequestQueriesResult(String memberKey, String apiKey, def request) {
        return this.getRequestQueriesResult(memberKey, apiKey, request, false)
    }

    def getRequestQueriesResult(String memberKey, String apiKey, def request, def grouped) {


        def clientOptions = hashingService.getClientOptions(apiKey).data

        def requestHashList = request

        def responseQueryHashList = []

        User member = memberKeyDao.findByKey(memberKey).user

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

            responseQueryHashList << [token: resultHash, query: query]

            statsService.saveRequest(clientOptions.application, member, resultHash)

        } else {

            requestHashList.each { String hash ->
                if (hash.length() > 0) {

                    def query = queryService.getQuery(clientOptions.application, clientOptions.allowedPlainQueries, member, hash)

                    responseQueryHashList << [token: hash, query: query.query]

                    statsService.saveRequest(clientOptions.application, member, hash)
                }

            }

        }

        return responseQueryHashList

    }

    def getRequestHashesResult(String memberKey, String apiKey, def request) {

        def clientOptions = hashingService.getClientOptions(apiKey).data
        def requestQueryList = request

        User member = memberKeyDao.findByKey(memberKey).user

        if (clientOptions.removeQueriesAfterBuild) {

            queryService.deleteForApplicationAndMember(clientOptions.application, member)

        }

        def responseQueryHashList = []

        requestQueryList.each { String query ->

            if (query.length() > 0) {

                String hash = hashingService.hashQuery(clientOptions, query).trim()

                if (queryDao.findByApplicationAndUserAndHash(clientOptions.application, member, hash) == null) {
                    queryService.saveQueryPair(clientOptions.application, member, query, hash)
                }
                responseQueryHashList << [token: hash, query: query]

            }

        }

        statsService.saveBuild(clientOptions.application, member, requestQueryList.size())

        return [code: SUCCESS.getCode(), data: responseQueryHashList]

    }

}
