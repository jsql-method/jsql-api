package pl.jsql.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.jsql.api.dto.request.BuildsRequest;
import pl.jsql.api.dto.request.QueriesRequest;
import pl.jsql.api.dto.request.RequestsRequest;
import pl.jsql.api.dto.response.*;
import pl.jsql.api.model.hashing.Application;
import pl.jsql.api.model.stats.Build;
import pl.jsql.api.model.stats.Request;
import pl.jsql.api.model.user.User;
import pl.jsql.api.repo.*;
import pl.jsql.api.security.service.SecurityService;
import pl.jsql.api.service.pagination.Pagination;

import java.util.Date;

@Transactional
@Service
public class StatsService {

    @Autowired
    private BuildDao buildDao;

    @Autowired
    private RequestDao requestDao;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private QueryDao queryDao;

    @Autowired
    private Pagination pagination;

    @Autowired
    private BuildExtendedDao buildExtendedDao;

    @Autowired
    private QueryExtendedDao queryExtendedDao;

    @Autowired
    private RequestExtendedDao requestExtendedDao;

    @Async
    public void saveBuild(Application application, User user, Integer queriesCount) {

        Build build = new Build();
        build.application = application;
        build.user = user;
        build.hashingDate = new Date();
        build.queriesCount = queriesCount;
        buildDao.save(build);

    }


    @Async
    public void saveRequest(Application application, User user, String queryHash) {

        Request request = new Request();
        request.application = application;
        request.user = user;
        request.queryHash = queryHash;
        request.requestDate = new Date();

        requestDao.save(request);

    }

    public PaginatedDataResponse<BuildsResponse> getBuilds(BuildsRequest buildRequest, Integer page) {

        User currentUser = securityService.getCurrentAccount();
        BuildsResponse buildsResponse = new BuildsResponse();

        switch (securityService.getCurrentRole()) {
            case COMPANY_ADMIN:
            case APP_ADMIN:
                buildsResponse.totalBuilds = buildExtendedDao.countBuildsForCompany(currentUser.company, buildRequest.dateFrom, buildRequest.dateTo, buildRequest.applications, buildRequest.developers);
                buildsResponse.todayBuilds = buildExtendedDao.countBuildsForCompany(currentUser.company, new Date(), new Date(), buildRequest.applications, buildRequest.developers);
                break;
            case APP_DEV:
                buildsResponse.totalBuilds = buildExtendedDao.countBuildsForDeveloper(currentUser, buildRequest.dateFrom, buildRequest.dateTo, buildRequest.applications);
                buildsResponse.todayBuilds = buildExtendedDao.countBuildsForDeveloper(currentUser, new Date(), new Date(), buildRequest.applications);
                break;
        }

        PaginationResponse paginationResponse = this.pagination.paginate(page, buildsResponse.totalBuilds);

        switch (securityService.getCurrentRole()) {
            case COMPANY_ADMIN:
            case APP_ADMIN:
                buildsResponse.builds = buildExtendedDao.selectBuildsForCompany(currentUser.company, buildRequest.dateFrom, buildRequest.dateTo, buildRequest.applications, buildRequest.developers, paginationResponse.pageRequest);
                break;
            case APP_DEV:
                buildsResponse.builds = buildExtendedDao.selectBuildsForDeveloper(currentUser, buildRequest.dateFrom, buildRequest.dateTo, buildRequest.applications, paginationResponse.pageRequest);
                break;
        }


        return new PaginatedDataResponse<>(buildsResponse, paginationResponse);

    }

    public PaginatedDataResponse<QueriesResponse> getQueries(QueriesRequest queriesRequest, Integer page) {

        User currentUser = securityService.getCurrentAccount();
        QueriesResponse queriesResponse = new QueriesResponse();

        switch (securityService.getCurrentRole()) {
            case COMPANY_ADMIN:
            case APP_ADMIN:
                queriesResponse.totalQueries = queryExtendedDao.countQueriesForCompany(currentUser.company, queriesRequest.dateFrom, queriesRequest.dateTo, queriesRequest.applications, queriesRequest.developers, queriesRequest.dynamic, queriesRequest.used);
                queriesResponse.todayQueries = queryExtendedDao.countQueriesForCompany(currentUser.company, new Date(), new Date(), queriesRequest.applications, queriesRequest.developers, queriesRequest.dynamic, queriesRequest.used);
                break;
            case APP_DEV:
                queriesResponse.totalQueries = queryExtendedDao.countQueriesForDeveloper(currentUser, queriesRequest.dateFrom, queriesRequest.dateTo, queriesRequest.applications, queriesRequest.dynamic, queriesRequest.used);
                queriesResponse.todayQueries = queryExtendedDao.countQueriesForDeveloper(currentUser, new Date(), new Date(), queriesRequest.applications, queriesRequest.dynamic, queriesRequest.used);
                break;
        }

        PaginationResponse paginationResponse = this.pagination.paginate(page, queriesResponse.totalQueries);

        switch (securityService.getCurrentRole()) {
            case COMPANY_ADMIN:
            case APP_ADMIN:
                queriesResponse.queries = queryExtendedDao.selectQueriesForCompany(currentUser.company, queriesRequest.dateFrom, queriesRequest.dateTo, queriesRequest.applications, queriesRequest.developers, queriesRequest.dynamic, queriesRequest.used, paginationResponse.pageRequest);
                break;
            case APP_DEV:
                queriesResponse.queries = queryExtendedDao.selectQueriesForDeveloper(currentUser, queriesRequest.dateFrom, queriesRequest.dateTo, queriesRequest.applications, queriesRequest.dynamic, queriesRequest.used, paginationResponse.pageRequest);
                break;
        }

        return new PaginatedDataResponse<>(queriesResponse, paginationResponse);

    }

    public PaginatedDataResponse<RequestsResponse> getRequests(RequestsRequest requestsRequest, Integer page) {

        User currentUser = securityService.getCurrentAccount();
        RequestsResponse requestsResponse = new RequestsResponse();

        switch (securityService.getCurrentRole()) {
            case COMPANY_ADMIN:
            case APP_ADMIN:
                requestsResponse.totalRequests = requestExtendedDao.countRequestsForCompany(currentUser.company, requestsRequest.dateFrom, requestsRequest.dateTo, requestsRequest.applications);
                requestsResponse.todayRequests = requestExtendedDao.countRequestsForCompany(currentUser.company, new Date(), new Date(), requestsRequest.applications);
                break;
            case APP_DEV:
                requestsResponse.totalRequests = requestExtendedDao.countRequestsForDeveloper(currentUser, requestsRequest.dateFrom, requestsRequest.dateTo, requestsRequest.applications);
                requestsResponse.todayRequests = requestExtendedDao.countRequestsForDeveloper(currentUser, new Date(), new Date(), requestsRequest.applications);
                break;
        }

        PaginationResponse paginationResponse = this.pagination.paginate(page, requestsResponse.totalRequests);

        switch (securityService.getCurrentRole()) {
            case COMPANY_ADMIN:
            case APP_ADMIN:
                requestsResponse.requests = requestExtendedDao.selectRequestsForCompany(currentUser.company, requestsRequest.dateFrom, requestsRequest.dateTo, requestsRequest.applications, paginationResponse.pageRequest);
                break;
            case APP_DEV:
                requestsResponse.requests = requestExtendedDao.selectRequestsForDeveloper(currentUser, requestsRequest.dateFrom, requestsRequest.dateTo, requestsRequest.applications, paginationResponse.pageRequest);
                break;
        }

        return new PaginatedDataResponse<>(requestsResponse, paginationResponse);

    }

}
