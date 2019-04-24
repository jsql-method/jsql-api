package pl.jsql.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

        return optionsResponse;


    }

    public String hashQuery(OptionsResponse options, String sqlQuery) {

        String hash = this.generateQueryHash(options, sqlQuery);

        if (options.isSalt) {

            if (options.saltRandomize) {

                if (options.saltBefore) {
                    hash = hash + TokenUtil.generateToken(sqlQuery);
                }

                if (options.saltAfter) {
                    hash = TokenUtil.generateToken(sqlQuery) + hash;
                }

            } else if (options.salt != null && !options.salt.isEmpty()) {

                if (options.saltBefore) {
                    hash = options.salt + hash;
                }

                if (options.saltAfter) {
                    hash = hash + options.salt;
                }

            }
        }

        return hash;

    }

    private String generateQueryHash(OptionsResponse options, String sqlQuery) {

        String hash = HashingUtil.encode(options, sqlQuery);

        if (options.hashLengthLikeQuery) {
            hash = TokenUtil.generateMixToken(options.apiKey, hash, sqlQuery.length());
        } else {
            hash = TokenUtil.generateMixToken(options.apiKey, hash, options.hashMinLength, options.hashMaxLength);
        }

        Application application = applicationDao.findByApiKey(options.apiKey);

        if(application == null){
            throw new CryptographyException("cannot_generate_query_hash_e1");
        }

        Query query = queryDao.findByHashAndApplication(hash, application);

        if (query != null) {
            throw new CryptographyException("cannot_generate_query_hash_e2");
        }

        return hash;

    }

}
