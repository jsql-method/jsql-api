package pl.jsql.api.service

import groovy.json.StringEscapeUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pl.jsql.api.model.hashing.Application
import pl.jsql.api.model.hashing.Options
import pl.jsql.api.model.hashing.Query
import pl.jsql.api.repo.ApplicationDao
import pl.jsql.api.repo.OptionsDao
import pl.jsql.api.repo.QueryDao
import pl.jsql.api.security.service.SecurityService
import pl.jsql.api.utils.HashingUtil
import pl.jsql.api.utils.TokenUtil

import static pl.jsql.api.enums.HttpMessageEnum.SUCCESS

@Transactional
@Service
public class  HashingService {

    @Autowired
    ApplicationDao applicationDao

    @Autowired
    QueryDao queryDao

    @Autowired
    OptionsDao optionsDao

    @Autowired
    SecurityService securityService

    def getClientOptions() {
        Application application = applicationDao.findByApiKey(securityService.getApiKey())

        if (application == null) {
            throw new Exception("UNAUTHORIZED_WITH_KEY")
        }

        Options options = optionsDao.findByApplication(application)
        return [code: SUCCESS.getCode(), data: [
                application            : application,
                apiKey                 : securityService.getApiKey(),
                encodeQuery            : options.encodeQuery,
                encodingAlgorithm      : options.encodingAlgorithm.value,
                isSalt                 : options.isSalt,
                salt                   : options.salt,
                saltBefore             : options.saltBefore,
                saltAfter              : options.saltAfter,
                saltRandomize          : options.saltRandomize,
                hashLengthLikeQuery    : options.hashLengthLikeQuery,
                hashMinLength          : options.hashMinLength,
                hashMaxLenght          : options.hashMaxLength,
                removeQueriesAfterBuild: options.removeQueriesAfterBuild,
                databaseDialect        : options.databaseDialect.name,
                allowedPlainQueries    : options.allowedPlainQueries,
                prod                   : options.prod
        ]
        ]

    }

    def hashQuery(def options, String sqlQuery) {

        if (options.encodeQuery) {
            sqlQuery = HashingUtil.encode(options, sqlQuery)
        }

        String hash = this.generateQueryHash(options, sqlQuery, options.encodeQuery)

        if (options.isSalt) {
            if (options.saltRandomize) {

                def randomized = options.salt.split(options.salt.charAt(options.salt.length() - 1).toString())

                if (randomized.size() > 1) {

                    if (options.saltBefore) {
                        hash = randomized[0] + hash
                    }

                    if (options.saltAfter) {
                        hash = hash + randomized[1]
                    }

                } else if (randomized.size() > 0) {

                    if (options.saltBefore) {
                        hash = randomized[0] + hash
                    } else if (options.saltAfter) {
                        hash = hash + randomized[0]
                    }

                }

            } else {

                if (options.saltBefore) {
                    hash = options.salt + hash
                }

                if (options.saltAfter) {
                    hash = hash + options.salt
                }

            }
        }

        hash = hash.replaceAll("[^A-Za-z0-9]", "")

        hash = hash.replaceAll("\\\\", "")
        hash = hash.replaceAll("/", "")
        hash = hash.replaceAll("\\+", "")

        hash = StringEscapeUtils.escapeJavaScript(hash)


        return hash

    }

    def generateQueryHash(def options, String sqlQuery, Boolean encodedQuery) {

        int iterationCount = 0
        for (; ;) {

            String hash

            if (!encodedQuery) {

                if (options.hashLengthLikeQuery) {
                    hash = TokenUtil.generateToken(options.userId, sqlQuery.length())
                } else {
                    hash = TokenUtil.generateToken(options.userId, options.hashMinLength, options.hashMaxLength)
                }

            } else {
                hash = sqlQuery
            }

            Query query = queryDao.findByHashAndApplication(hash, options.application)

            if (query == null) {
                return hash
            }

            iterationCount++

            if (iterationCount > 10) {
                throw new Exception('UNABLE_GENERATE_QUERY_HASH')
            }

        }


    }

}
