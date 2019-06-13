package pl.jsql.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.jsql.api.dto.response.DatabaseConnectionResponse;
import pl.jsql.api.dto.response.OptionsResponse;
import pl.jsql.api.exceptions.CryptographyException;
import pl.jsql.api.exceptions.UnauthorizedException;
import pl.jsql.api.model.hashing.Application;
import pl.jsql.api.model.hashing.Options;
import pl.jsql.api.model.hashing.Query;
import pl.jsql.api.repo.ApplicationDao;
import pl.jsql.api.repo.OptionsDao;
import pl.jsql.api.repo.QueryDao;
import pl.jsql.api.security.service.SecurityService;
import pl.jsql.api.utils.HashingUtil;
import pl.jsql.api.utils.TokenUtil;

@Transactional
@Service
public class HashingService {

    @Autowired
    private ApplicationDao applicationDao;

    @Autowired
    private QueryDao queryDao;

    @Autowired
    private OptionsDao optionsDao;

    @Autowired
    private SecurityService securityService;

    public OptionsResponse getClientOptions() {

        Application application = applicationDao.findByApiKey(securityService.getApiKey());

        if (application == null) {
            throw new UnauthorizedException("unauthorized_with_key");
        }

        Options options = optionsDao.findByApplication(application);

        OptionsResponse optionsResponse = new OptionsResponse();

        optionsResponse.apiKey = securityService.getApiKey();
        optionsResponse.encodingAlgorithm = options.encodingAlgorithm;
        optionsResponse.isSalt = options.isSalt;
        optionsResponse.salt = options.salt;
        optionsResponse.saltBefore = options.saltBefore;
        optionsResponse.saltAfter = options.saltAfter;
        optionsResponse.saltRandomize = options.saltRandomize;
        optionsResponse.hashLengthLikeQuery = options.hashLengthLikeQuery;
        optionsResponse.hashMinLength = options.hashMinLength;
        optionsResponse.hashMaxLength = options.hashMaxLength;
        optionsResponse.removeQueriesAfterBuild = options.removeQueriesAfterBuild;
        optionsResponse.databaseDialect = options.databaseDialect;
        optionsResponse.allowedPlainQueries = options.allowedPlainQueries;
        optionsResponse.prod = options.prod;
        optionsResponse.randomSaltAfter = options.randomSaltAfter;
        optionsResponse.randomSaltBefore = options.randomSaltBefore;

        DatabaseConnectionResponse prodDatabaseConnectionResponse = new DatabaseConnectionResponse();
        prodDatabaseConnectionResponse.databaseConnectionPassword = options.prodDatabaseConnectionPassword;
        prodDatabaseConnectionResponse.databaseConnectionTimeout = options.prodDatabaseConnectionTimeout;
        prodDatabaseConnectionResponse.databaseConnectionUrl = options.prodDatabaseConnectionUrl;
        prodDatabaseConnectionResponse.databaseConnectionUsername = options.prodDatabaseConnectionUsername;

        optionsResponse.productionDatabaseOptions = prodDatabaseConnectionResponse;

        DatabaseConnectionResponse devDatabaseConnectionResponse = new DatabaseConnectionResponse();
        devDatabaseConnectionResponse.databaseConnectionPassword = options.devDatabaseConnectionPassword;
        devDatabaseConnectionResponse.databaseConnectionTimeout = options.devDatabaseConnectionTimeout;
        devDatabaseConnectionResponse.databaseConnectionUrl = options.devDatabaseConnectionUrl;
        devDatabaseConnectionResponse.databaseConnectionUsername = options.devDatabaseConnectionUsername;

        optionsResponse.developerDatabaseOptions = devDatabaseConnectionResponse;

        return optionsResponse;


    }

    public boolean isValidSalt(OptionsResponse options, String hash) {

        boolean isValid = true;

        if (options.isSalt) {

            if (options.saltRandomize) {

                if (options.saltBefore) {
                    isValid = hash.startsWith(options.randomSaltBefore);
                    if(!isValid){
                        return isValid;
                    }
                }

                if (options.saltAfter) {
                    isValid =  hash.endsWith(options.randomSaltAfter);
                    if(!isValid){
                        return isValid;
                    }
                }

            } else if (options.salt != null && !options.salt.isEmpty()) {

                if (options.saltBefore) {
                    isValid =  hash.startsWith(options.salt);
                    if(!isValid){
                        return isValid;
                    }
                }

                if (options.saltAfter) {
                    isValid =  hash.endsWith(options.salt);
                    if(!isValid){
                        return isValid;
                    }
                }

            }

        }

        return isValid;

    }

    public String hashQuery(OptionsResponse options, String sqlQuery, Boolean development) {

        if(options.allowedPlainQueries && development){
            return sqlQuery;
        }

        String hash = this.generateQueryHash(options, sqlQuery);

        if (options.isSalt) {

            if (options.saltRandomize) {

                if (options.saltAfter && options.saltBefore) {
                    hash = options.randomSaltBefore + hash + options.randomSaltAfter;
                } else if (options.saltBefore) {
                    hash = options.randomSaltBefore + hash;
                } else if (options.saltAfter) {
                    hash = hash + options.randomSaltAfter;
                }

            } else if (options.salt != null && !options.salt.isEmpty()) {

                if (options.saltAfter && options.saltBefore) {
                    hash = options.salt + hash + options.salt;
                } else if (options.saltBefore) {
                    hash = options.salt + hash;
                } else if (options.saltAfter) {
                    hash = hash + options.salt;
                }

            }

        }

        return hash;

    }

    private String generateQueryHash(OptionsResponse options, String sqlQuery) {

        Application application = applicationDao.findByApiKey(options.apiKey);

        if (application == null) {
            throw new CryptographyException("cannot_generate_query_hash_e1");
        }

        String hash = HashingUtil.encode(options, sqlQuery);

        if (options.hashLengthLikeQuery) {
            hash = TokenUtil.generateMixToken(options.apiKey, hash, sqlQuery.length());
        } else {
            hash = TokenUtil.generateMixToken(options.apiKey, hash, options.hashMinLength, options.hashMaxLength);
        }

        Query query = queryDao.findByHashAndApplication(hash, application);

        if (query != null) {
            throw new CryptographyException("cannot_generate_query_hash_e2");
        }

        return hash;

    }

}
