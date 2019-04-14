package pl.jsql.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.jsql.api.dto.request.OptionsRequest;
import pl.jsql.api.dto.response.MessageResponse;
import pl.jsql.api.dto.response.OptionsResponse;
import pl.jsql.api.dto.response.OptionsValuesResponse;
import pl.jsql.api.dto.response.SelectResponse;
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

import java.util.Arrays;
import java.util.List;


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
        optionsResponse.prod = options.prod;
        optionsResponse.application = application;
        optionsResponse.apiKey = application.apiKey;

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
        options.prod = optionsRequest.prod == null ? options.prod : optionsRequest.prod;


        return new MessageResponse();

    }

    public OptionsValuesResponse getValues() {

        OptionsValuesResponse optionsValuesResponse = new OptionsValuesResponse();

        optionsValuesResponse.databaseDialectValues = DatabaseDialectEnum.toSelectResponse();
        optionsValuesResponse.encodingAlgorithmValues = EncodingEnum.toSelectResponse();

        return optionsValuesResponse;

    }


}
