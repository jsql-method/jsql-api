package pl.jsql.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.jsql.api.dto.request.OptionsRequest;
import pl.jsql.api.dto.request.ProductionToggleRequest;
import pl.jsql.api.dto.response.DatabaseConnectionResponse;
import pl.jsql.api.dto.response.MessageResponse;
import pl.jsql.api.dto.response.OptionsResponse;
import pl.jsql.api.dto.response.OptionsValuesResponse;
import pl.jsql.api.enums.DatabaseDialectEnum;
import pl.jsql.api.enums.EncodingEnum;
import pl.jsql.api.enums.RoleTypeEnum;
import pl.jsql.api.exceptions.NotFoundException;
import pl.jsql.api.model.hashing.Application;
import pl.jsql.api.model.hashing.ApplicationDevelopers;
import pl.jsql.api.model.hashing.Options;
import pl.jsql.api.model.user.User;
import pl.jsql.api.repo.*;
import pl.jsql.api.security.service.SecurityService;
import pl.jsql.api.utils.Utils;


@Transactional
@Service
public class OptionsService {

    @Autowired
    private ApplicationDao applicationDao;

    @Autowired
    private OptionsDao optionsDao;

    @Autowired
    private ApplicationDevelopersDao applicationDevelopersDao;

    @Autowired
    UserDao userDao;

    @Autowired
    RoleDao roleDao;

    @Autowired
    SecurityService securityService;

    @Autowired
    private ApiService apiService;

    public OptionsResponse getByApplicationId(Long applicationId) {

        Application application = applicationDao.findById(applicationId).orElse(null);

        if (application == null) {
            throw new NotFoundException("application_not_found");
        }

        User currentUser = securityService.getCurrentAccount();

        if (securityService.getCurrentRole() == RoleTypeEnum.APP_DEV) {

            ApplicationDevelopers applicationDeveloper = applicationDevelopersDao.findByUserAndAppQuery(currentUser, application);

            if (applicationDeveloper == null) {
                throw new NotFoundException("application_not_found");
            }

        }

        Options options = optionsDao.findByApplication(application);

        OptionsResponse optionsResponse = new OptionsResponse();
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
        optionsResponse.prodCache = options.prodCache;
        optionsResponse.apiKey = application.apiKey;
        optionsResponse.randomSaltBefore = options.randomSaltBefore;
        optionsResponse.randomSaltAfter = options.randomSaltAfter;

        DatabaseConnectionResponse prodDatabaseConnectionResponse = new DatabaseConnectionResponse();
        prodDatabaseConnectionResponse.databaseConnectionPassword = Utils.anonimize(options.prodDatabaseConnectionPassword);
        prodDatabaseConnectionResponse.databaseConnectionTimeout = options.prodDatabaseConnectionTimeout;
        prodDatabaseConnectionResponse.databaseConnectionUrl = Utils.anonimize(options.prodDatabaseConnectionUrl);
        prodDatabaseConnectionResponse.databaseConnectionUsername = Utils.anonimize(options.prodDatabaseConnectionUsername);

        optionsResponse.productionDatabaseOptions = prodDatabaseConnectionResponse;

        DatabaseConnectionResponse devDatabaseConnectionResponse = new DatabaseConnectionResponse();
        devDatabaseConnectionResponse.databaseConnectionPassword = Utils.anonimize(options.devDatabaseConnectionPassword);
        devDatabaseConnectionResponse.databaseConnectionTimeout = options.devDatabaseConnectionTimeout;
        devDatabaseConnectionResponse.databaseConnectionUrl = Utils.anonimize(options.devDatabaseConnectionUrl);
        devDatabaseConnectionResponse.databaseConnectionUsername = Utils.anonimize(options.devDatabaseConnectionUsername);

        optionsResponse.developerDatabaseOptions = devDatabaseConnectionResponse;

        return optionsResponse;

    }

    public MessageResponse update(Long applicationId, OptionsRequest optionsRequest) {

        Application application = applicationDao.findById(applicationId).orElse(null);

        if (application == null) {
            throw new NotFoundException("application_not_found");
        }

        Options options = optionsDao.findByApplication(application);

        options.databaseDialect = optionsRequest.databaseDialect == null ? options.databaseDialect : optionsRequest.databaseDialect;
        options.encodingAlgorithm = optionsRequest.encodingAlgorithm == null ? options.encodingAlgorithm : optionsRequest.encodingAlgorithm;
        options.isSalt = optionsRequest.isSalt == null ? options.isSalt : optionsRequest.isSalt;
        options.salt = optionsRequest.salt == null ? options.salt : optionsRequest.salt;
        options.saltBefore = optionsRequest.saltBefore == null ? options.saltBefore : optionsRequest.saltBefore;
        options.saltAfter = optionsRequest.saltAfter == null ? options.saltAfter : optionsRequest.saltAfter;
        options.saltRandomize = optionsRequest.saltRandomize == null ? options.saltRandomize : optionsRequest.saltRandomize;
        options.hashLengthLikeQuery = optionsRequest.hashLengthLikeQuery == null ? options.hashLengthLikeQuery : optionsRequest.hashLengthLikeQuery;
        options.hashMinLength = optionsRequest.hashMinLength == null ? options.hashMinLength : optionsRequest.hashMinLength;
        options.hashMaxLength = optionsRequest.hashMaxLength == null ? options.hashMaxLength : optionsRequest.hashMaxLength;
        options.removeQueriesAfterBuild = optionsRequest.removeQueriesAfterBuild == null ? options.removeQueriesAfterBuild : optionsRequest.removeQueriesAfterBuild;
        options.allowedPlainQueries = optionsRequest.allowedPlainQueries == null ? options.allowedPlainQueries : optionsRequest.allowedPlainQueries;

        if(!Utils.isAnonime(optionsRequest.productionDatabaseOptions.databaseConnectionUsername)){
            options.prodDatabaseConnectionUsername = optionsRequest.productionDatabaseOptions.databaseConnectionUsername;
        }

        if(!Utils.isAnonime(optionsRequest.productionDatabaseOptions.databaseConnectionPassword)) {
            options.prodDatabaseConnectionPassword = optionsRequest.productionDatabaseOptions.databaseConnectionPassword;
        }

        options.prodDatabaseConnectionTimeout = optionsRequest.productionDatabaseOptions.databaseConnectionTimeout == null ? options.prodDatabaseConnectionTimeout : optionsRequest.productionDatabaseOptions.databaseConnectionTimeout;

        if(!Utils.isAnonime(optionsRequest.productionDatabaseOptions.databaseConnectionUrl)) {
            options.prodDatabaseConnectionUrl = optionsRequest.productionDatabaseOptions.databaseConnectionUrl;
        }

        if(!Utils.isAnonime(optionsRequest.developerDatabaseOptions.databaseConnectionUsername)) {
            options.devDatabaseConnectionUsername =  optionsRequest.developerDatabaseOptions.databaseConnectionUsername;
        }

        if(!Utils.isAnonime(optionsRequest.developerDatabaseOptions.databaseConnectionPassword)) {
            options.devDatabaseConnectionPassword = optionsRequest.developerDatabaseOptions.databaseConnectionPassword;
        }

        options.devDatabaseConnectionTimeout = optionsRequest.developerDatabaseOptions.databaseConnectionTimeout == null ? options.devDatabaseConnectionTimeout : optionsRequest.developerDatabaseOptions.databaseConnectionTimeout;

        if(!Utils.isAnonime(optionsRequest.developerDatabaseOptions.databaseConnectionUrl)) {
            options.devDatabaseConnectionUrl = optionsRequest.developerDatabaseOptions.databaseConnectionUrl;
        }

        optionsDao.save(options);

        return new MessageResponse();

    }

    public OptionsValuesResponse getValues() {

        OptionsValuesResponse optionsValuesResponse = new OptionsValuesResponse();

        optionsValuesResponse.databaseDialectValues = DatabaseDialectEnum.toSelectResponse();
        optionsValuesResponse.encodingAlgorithmValues = EncodingEnum.toSelectResponse();

        return optionsValuesResponse;

    }


    /**
     * Włacza cache
     */
    public MessageResponse toggleProduction(Long applicationId, ProductionToggleRequest productionToggleRequest) {

        Application application = applicationDao.findById(applicationId).orElse(null);

        if (application == null) {
            throw new NotFoundException("application_not_found");
        }

        Options options = optionsDao.findByApplication(application);
        options.prodCache = productionToggleRequest.prodCache;

        optionsDao.save(options);

        return new MessageResponse();

    }

//    public Boolean isProduction(Application application){
//
//        Options options = optionsDao.findByApplication(application);
//        return options.prodCache;
//
//    }


    public MessageResponse purgeOptions(Long id) {
        //TODO
        return null;
    }


    public MessageResponse purgeQueries(Long id) {
        //TODO
        return null;
    }

}
