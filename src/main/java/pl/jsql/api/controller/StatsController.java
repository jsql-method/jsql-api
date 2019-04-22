package pl.jsql.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.jsql.api.controller.generic.ValidateController;
import pl.jsql.api.dto.request.BuildsRequest;
import pl.jsql.api.dto.request.QueriesRequest;
import pl.jsql.api.dto.request.RequestsRequest;
import pl.jsql.api.dto.response.*;
import pl.jsql.api.enums.RoleTypeEnum;
import pl.jsql.api.security.annotation.Security;
import pl.jsql.api.service.StatsService;

import javax.validation.Valid;
import java.text.ParseException;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/stats")
public class StatsController extends ValidateController {

    @Autowired
    private StatsService statsService;

    @Security(roles = {RoleTypeEnum.COMPANY_ADMIN, RoleTypeEnum.APP_ADMIN, RoleTypeEnum.APP_DEV})
    @PostMapping("/chart/builds")
    public BasicResponse<List<BuildsChartDataResponse>> getBuildsChart(@RequestBody @Valid BuildsRequest buildRequest) throws ParseException {
        List<BuildsChartDataResponse> response = statsService.getBuildsChart(buildRequest);
        return new BasicResponse<>(200, response);
    }

    @Security(roles = {RoleTypeEnum.COMPANY_ADMIN, RoleTypeEnum.APP_ADMIN, RoleTypeEnum.APP_DEV})
    @PostMapping("/builds")
    public BasicResponse<PaginatedDataResponse<BuildsResponse>> getBuilds(@RequestHeader(value = "page") Integer page, @RequestBody @Valid BuildsRequest buildRequest) throws ParseException {
        PaginatedDataResponse<BuildsResponse> response = statsService.getBuilds(buildRequest, page);
        return new BasicResponse<>(200, response);
    }

    @Security(roles = {RoleTypeEnum.COMPANY_ADMIN, RoleTypeEnum.APP_ADMIN, RoleTypeEnum.APP_DEV})
    @PostMapping("/chart/queries")
    public BasicResponse<List<QueriesChartDataResponse>> getQueriesChart(@RequestBody @Valid QueriesRequest queriesRequest) throws ParseException {
        List<QueriesChartDataResponse> response = statsService.getQueriesChart(queriesRequest);
        return new BasicResponse<>(200, response);
    }

    @Security(roles = {RoleTypeEnum.COMPANY_ADMIN, RoleTypeEnum.APP_ADMIN, RoleTypeEnum.APP_DEV})
    @PostMapping("/queries")
    public BasicResponse<PaginatedDataResponse<QueriesResponse>> getQueries(@RequestHeader(value = "page") Integer page, @RequestBody @Valid QueriesRequest queriesRequest) throws ParseException {
        PaginatedDataResponse<QueriesResponse> response = statsService.getQueries(queriesRequest, page);
        return new BasicResponse<>(200, response);
    }

    @Security(roles = {RoleTypeEnum.COMPANY_ADMIN, RoleTypeEnum.APP_ADMIN, RoleTypeEnum.APP_DEV})
    @PostMapping("/chart/requests")
    public BasicResponse<List<RequestsChartDataResponse>> getRequestsChart(@RequestBody @Valid RequestsRequest requestsRequest) throws ParseException {
        List<RequestsChartDataResponse> response = statsService.getRequestsChart(requestsRequest);
        return new BasicResponse<>(200, response);
    }

    @Security(roles = {RoleTypeEnum.COMPANY_ADMIN, RoleTypeEnum.APP_ADMIN, RoleTypeEnum.APP_DEV})
    @PostMapping("/requests")
    public BasicResponse<PaginatedDataResponse<RequestsResponse>> getRequests(@RequestHeader(value = "page") Integer page, @RequestBody @Valid RequestsRequest requestsRequest) throws ParseException {
        PaginatedDataResponse<RequestsResponse> response = statsService.getRequests(requestsRequest, page);
        return new BasicResponse<>(200, response);
    }

}
