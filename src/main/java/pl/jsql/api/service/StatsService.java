package pl.jsql.api.service;

import com.google.gson.Gson;
import org.apache.commons.lang3.time.FastDateFormat;
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

import javax.validation.Valid;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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
    public void saveRequest(Application application, User user, String queryHash, String query) {

        Request request = new Request();
        request.application = application;
        request.user = user;
        request.queryHash = queryHash;
        request.query = query;
        request.requestDate = new Date();

        requestDao.save(request);

    }

    @Autowired
    private FastDateFormat simpleDateFormat;

    public PaginatedDataResponse<BuildsResponse> getBuilds(BuildsRequest buildRequest, Integer page) throws ParseException {

        Date dateFrom = simpleDateFormat.parse(buildRequest.dateFrom);
        Date dateTo = simpleDateFormat.parse(buildRequest.dateTo);

        User currentUser = securityService.getCurrentAccount();
        BuildsResponse buildsResponse = new BuildsResponse();

        switch (securityService.getCurrentRole()) {
            case COMPANY_ADMIN:
            case APP_ADMIN:
                buildsResponse.totalBuilds = buildExtendedDao.countBuildsForCompany(currentUser.company, dateFrom, dateTo, buildRequest.applications, buildRequest.developers);
                buildsResponse.todayBuilds = buildExtendedDao.countBuildsForCompany(currentUser.company, new Date(), new Date(), buildRequest.applications, buildRequest.developers);
                break;
            case APP_DEV:
                buildsResponse.totalBuilds = buildExtendedDao.countBuildsForDeveloper(currentUser, dateFrom, dateTo, buildRequest.applications);
                buildsResponse.todayBuilds = buildExtendedDao.countBuildsForDeveloper(currentUser, new Date(), new Date(), buildRequest.applications);
                break;
        }

        PaginationResponse paginationResponse = this.pagination.paginate(page, buildsResponse.totalBuilds.intValue());

        switch (securityService.getCurrentRole()) {
            case COMPANY_ADMIN:
            case APP_ADMIN:
                buildsResponse.builds = buildExtendedDao.selectBuildsForCompany(currentUser.company, dateFrom, dateTo, buildRequest.applications, buildRequest.developers, paginationResponse.pageRequest);
                break;
            case APP_DEV:
                buildsResponse.builds = buildExtendedDao.selectBuildsForDeveloper(currentUser, dateFrom, dateTo, buildRequest.applications, paginationResponse.pageRequest);
                break;
        }


        return new PaginatedDataResponse<>(buildsResponse, paginationResponse);

    }


    public List<BuildsChartDataResponse> getBuildsChart(@Valid BuildsRequest buildRequest) throws ParseException {

        Date dateFrom = simpleDateFormat.parse(buildRequest.dateFrom);
        Date dateTo = simpleDateFormat.parse(buildRequest.dateTo);

        User currentUser = securityService.getCurrentAccount();
        List<BuildResponse> buildsResponses = new ArrayList<>();

        switch (securityService.getCurrentRole()) {
            case COMPANY_ADMIN:
            case APP_ADMIN:
                buildsResponses = buildExtendedDao.selectChartBuildsForCompany(currentUser.company, dateFrom, dateTo, buildRequest.applications, buildRequest.developers);
                break;
            case APP_DEV:
                buildsResponses = buildExtendedDao.selectChartBuildsForDeveloper(currentUser, dateFrom, dateTo, buildRequest.applications);
                break;
        }

        List<BuildsChartDataResponse> buildsChartDataResponses = new ArrayList<>();

        for (BuildResponse buildResponse : buildsResponses) {
            buildsChartDataResponses.add(new BuildsChartDataResponse(simpleDateFormat.format(buildResponse.hashingDate), buildResponse.queriesCount, buildResponse.developerName, buildResponse.applicationName));

        }

        return buildsChartDataResponses;

    }

    public PaginatedDataResponse<QueriesResponse> getQueries(QueriesRequest queriesRequest, Integer page) throws ParseException {

        Date dateFrom = simpleDateFormat.parse(queriesRequest.dateFrom);
        Date dateTo = simpleDateFormat.parse(queriesRequest.dateTo);

        User currentUser = securityService.getCurrentAccount();
        QueriesResponse queriesResponse = new QueriesResponse();

        switch (securityService.getCurrentRole()) {
            case COMPANY_ADMIN:
            case APP_ADMIN:
                queriesResponse.totalQueries = queryExtendedDao.countQueriesForCompany(currentUser.company, dateFrom, dateTo, queriesRequest.applications, queriesRequest.developers, queriesRequest.dynamic, queriesRequest.used, queriesRequest.search);
                queriesResponse.todayQueries = queryExtendedDao.countQueriesForCompany(currentUser.company, new Date(), new Date(), queriesRequest.applications, queriesRequest.developers, queriesRequest.dynamic, queriesRequest.used, queriesRequest.search);
                break;
            case APP_DEV:
                queriesResponse.totalQueries = queryExtendedDao.countQueriesForDeveloper(currentUser, dateFrom, dateTo, queriesRequest.applications, queriesRequest.dynamic, queriesRequest.used, queriesRequest.search);
                queriesResponse.todayQueries = queryExtendedDao.countQueriesForDeveloper(currentUser, new Date(), new Date(), queriesRequest.applications, queriesRequest.dynamic, queriesRequest.used, queriesRequest.search);
                break;
        }

        PaginationResponse paginationResponse = this.pagination.paginate(page, queriesResponse.totalQueries.intValue());

        switch (securityService.getCurrentRole()) {
            case COMPANY_ADMIN:
            case APP_ADMIN:
                queriesResponse.queries = queryExtendedDao.selectQueriesForCompany(currentUser.company, dateFrom, dateTo, queriesRequest.applications, queriesRequest.developers, queriesRequest.dynamic, queriesRequest.used, queriesRequest.search, paginationResponse.pageRequest);
                break;
            case APP_DEV:
                queriesResponse.queries = queryExtendedDao.selectQueriesForDeveloper(currentUser, dateFrom, dateTo, queriesRequest.applications, queriesRequest.dynamic, queriesRequest.used, queriesRequest.search, paginationResponse.pageRequest);
                break;
        }

        return new PaginatedDataResponse<>(queriesResponse, paginationResponse);

    }

    public List<QueriesChartDataResponse> getQueriesChart(@Valid QueriesRequest queriesRequest) throws ParseException {

        Date dateFrom = simpleDateFormat.parse(queriesRequest.dateFrom);
        Date dateTo = simpleDateFormat.parse(queriesRequest.dateTo);

        User currentUser = securityService.getCurrentAccount();

        List<QueriesChartDataResponse> queriesChartDataResponses = new ArrayList<>();

        switch (securityService.getCurrentRole()) {
            case COMPANY_ADMIN:
            case APP_ADMIN:
                queriesChartDataResponses = queryExtendedDao.selectChartQueriesForCompany(currentUser.company, dateFrom, dateTo, queriesRequest.applications, queriesRequest.developers, queriesRequest.dynamic, queriesRequest.used, queriesRequest.search);
                break;
            case APP_DEV:
                queriesChartDataResponses = queryExtendedDao.selectChartQueriesForDeveloper(currentUser, dateFrom, dateTo, queriesRequest.applications, queriesRequest.dynamic, queriesRequest.used, queriesRequest.search);
                break;
        }

        return queriesChartDataResponses;

    }

    public PaginatedDataResponse<RequestsResponse> getRequests(RequestsRequest requestsRequest, Integer page) throws ParseException {

        Date dateFrom = simpleDateFormat.parse(requestsRequest.dateFrom);
        Date dateTo = simpleDateFormat.parse(requestsRequest.dateTo);

        User currentUser = securityService.getCurrentAccount();
        RequestsResponse requestsResponse = new RequestsResponse();

        switch (securityService.getCurrentRole()) {
            case COMPANY_ADMIN:
            case APP_ADMIN:
                requestsResponse.totalRequests = requestExtendedDao.countRequestsForCompany(currentUser.company, dateFrom, dateTo, requestsRequest.applications);
                requestsResponse.todayRequests = requestExtendedDao.countRequestsForCompany(currentUser.company, new Date(), new Date(), requestsRequest.applications);
                break;
            case APP_DEV:
                requestsResponse.totalRequests = requestExtendedDao.countRequestsForDeveloper(currentUser, dateFrom, dateTo, requestsRequest.applications);
                requestsResponse.todayRequests = requestExtendedDao.countRequestsForDeveloper(currentUser, new Date(), new Date(), requestsRequest.applications);
                break;
        }

        PaginationResponse paginationResponse = this.pagination.paginate(page, requestsResponse.totalRequests.intValue());

        switch (securityService.getCurrentRole()) {
            case COMPANY_ADMIN:
            case APP_ADMIN:
                requestsResponse.requests = requestExtendedDao.selectRequestsForCompany(currentUser.company, dateFrom, dateTo, requestsRequest.applications, paginationResponse.pageRequest);
                break;
            case APP_DEV:
                requestsResponse.requests = requestExtendedDao.selectRequestsForDeveloper(currentUser, dateFrom, dateTo, requestsRequest.applications, paginationResponse.pageRequest);
                break;
        }

        return new PaginatedDataResponse<>(requestsResponse, paginationResponse);

    }

    public List<RequestsChartDataResponse> getRequestsChart(@Valid RequestsRequest requestsRequest) throws ParseException {

        System.out.println("requestsRequest.dateFrom : " + requestsRequest.dateFrom);
        System.out.println("requestsRequest.dateTo : " + requestsRequest.dateTo);

        Date dateFrom = simpleDateFormat.parse(requestsRequest.dateFrom);
        Date dateTo = simpleDateFormat.parse(requestsRequest.dateTo);

        User currentUser = securityService.getCurrentAccount();
        List<RequestResponse> requestResponses = new ArrayList<>();

        switch (securityService.getCurrentRole()) {
            case COMPANY_ADMIN:
            case APP_ADMIN:
                requestResponses = requestExtendedDao.selectChartRequestsForCompany(currentUser.company, dateFrom, dateTo, requestsRequest.applications);
                break;
            case APP_DEV:
                requestResponses = requestExtendedDao.selectChartRequestsForDeveloper(currentUser, dateFrom, dateTo, requestsRequest.applications);
                break;
        }

        List<RequestsChartDataResponse> requestsChartDataResponses = new ArrayList<>();
        HashMap<String, Long> groupCount = new HashMap<>();

        for (RequestResponse requestResponse : requestResponses) {

            String date = simpleDateFormat.format(requestResponse.requestDate);
            String key = date + requestResponse.applicationName;
            groupCount.putIfAbsent(key, 0L);
            groupCount.put(key, groupCount.get(key) + 1);

        }

        for (RequestResponse requestResponse : requestResponses) {
            String date = simpleDateFormat.format(requestResponse.requestDate);
            String key = date + requestResponse.applicationName;
            requestsChartDataResponses.add(new RequestsChartDataResponse(date, requestResponse.applicationName, groupCount.get(key)));

        }

        return requestsChartDataResponses;


    }

}
