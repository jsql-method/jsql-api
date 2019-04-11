package pl.jsql.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.jsql.api.dto.request.BuildsRequest;
import pl.jsql.api.dto.request.QueriesRequest;
import pl.jsql.api.dto.request.RequestsRequest;
import pl.jsql.api.dto.response.BuildsResponse;
import pl.jsql.api.dto.response.PaginatedDataResponse;
import pl.jsql.api.dto.response.PaginationResponse;
import pl.jsql.api.dto.response.QueryResponse;
import pl.jsql.api.model.hashing.Application;
import pl.jsql.api.model.hashing.Query;
import pl.jsql.api.model.stats.Build;
import pl.jsql.api.model.stats.Request;
import pl.jsql.api.model.user.User;
import pl.jsql.api.repo.ApplicationDao;
import pl.jsql.api.repo.BuildDao;
import pl.jsql.api.repo.QueryDao;
import pl.jsql.api.repo.RequestDao;
import pl.jsql.api.security.service.SecurityService;
import pl.jsql.api.service.pagination.Pagination;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
    private QueryDao queryDao;

    @Autowired
    private ApplicationDao applicationDao;

    @Autowired
    private Pagination pagination;

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
                buildsResponse.totalBuilds = buildDao.countBuildsForCompany(currentUser.company, buildRequest.dateFrom, buildRequest.dateTo, buildRequest.applications, buildRequest.developers);
                buildsResponse.todayBuilds = buildDao.countBuildsForCompany(currentUser.company, new Date(), new Date(), buildRequest.applications, buildRequest.developers);
                break;
            case APP_DEV:
                buildsResponse.totalBuilds = buildDao.countBuildsForDeveloper(currentUser, buildRequest.dateFrom, buildRequest.dateTo, buildRequest.applications);
                buildsResponse.todayBuilds = buildDao.countBuildsForDeveloper(currentUser, new Date(), new Date(), buildRequest.applications);
                break;
        }

        PaginationResponse paginationResponse = this.pagination.paginate(page, buildsResponse.totalBuilds);

        switch (securityService.getCurrentRole()) {
            case COMPANY_ADMIN:
            case APP_ADMIN:
                buildsResponse.builds = buildDao.selectBuildsForCompany(currentUser.company, buildRequest.dateFrom, buildRequest.dateTo, buildRequest.applications, buildRequest.developers, paginationResponse.pageRequest);
                break;
            case APP_DEV:
                buildsResponse.builds = buildDao.selectBuildsForDeveloper(currentUser, buildRequest.dateFrom, buildRequest.dateTo, buildRequest.applications, paginationResponse.pageRequest);
                break;
        }


        return new PaginatedDataResponse<>(buildsResponse, paginationResponse);

    }

    public PaginatedDataResponse<List<QueryResponse>> getQueries(QueriesRequest queriesRequest, Integer page) {

        User currentUser = securityService.getCurrentAccount();
        List<QueryResponse> queryResponses = new ArrayList<>();

        Integer count = null;

        switch (securityService.getCurrentRole()) {
            case COMPANY_ADMIN:
            case APP_ADMIN:
                count = queryDao.countQueriesForCompany(currentUser.company, queriesRequest.dateFrom, queriesRequest.dateTo, queriesRequest.applications, queriesRequest.developers, queriesRequest.dynamic, queriesRequest.used);
                break;
            case APP_DEV:
                count = queryDao.countQueriesForDeveloper(currentUser, queriesRequest.dateFrom, queriesRequest.dateTo, queriesRequest.applications, queriesRequest.dynamic, queriesRequest.used);
                break;
        }

        PaginationResponse paginationResponse = this.pagination.paginate(page, count);

        switch (securityService.getCurrentRole()) {
            case COMPANY_ADMIN:
            case APP_ADMIN:
                queryResponses = queryDao.selectQueriesForCompany(currentUser.company, queriesRequest.dateFrom, queriesRequest.dateTo, queriesRequest.applications, queriesRequest.developers, queriesRequest.dynamic, queriesRequest.used);
                break;
            case APP_DEV:
                queryResponses = queryDao.selectQueriesForDeveloper(currentUser, queriesRequest.dateFrom, queriesRequest.dateTo, queriesRequest.applications, queriesRequest.dynamic, queriesRequest.used);
                break;
        }

        return new PaginatedDataResponse<>(queryResponses, paginationResponse);

    }

    def getRequests(RequestsRequest request) {
        User currentUser = securityService.getCurrentAccount()
        def requests = requestDao.findByApplicationsAndDateBetween(request.dateFrom, request.dateTo, currentUser.company, request.applications)
        def data = []

        requests.each {
            req ->
                    data <<[
                    application  :req.application,
                    totalRequests:req.totalRequests,
                    date         :
            new SimpleDateFormat('dd-MM-YYYY').format(getDateByDayMonthAndYear(req.year, req.month, req.day)),
                    requests     :getRequestsByHour(req)
            ]
        }

        return [code:
        SUCCESS.getCode(), data:data]
    }

    def getRequestsByHour(def req) {

        List<List<Long>> counts = requestDao.countByHour(applicationDao.findById(req.application).get(), req.day, req.month, req.year)
        Map<String, Long> resp = new LinkedHashMap<>()

        for (List<Long> list : counts) {
            String hour = list.get(1).toString().length() > 1 ? list.get(1).toString() : "0" + list.get(1).toString()
            resp.put(hour + ":00", list.get(0))
        }

        return resp
    }

}
