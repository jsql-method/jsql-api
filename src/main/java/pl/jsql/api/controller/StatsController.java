package pl.jsql.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.jsql.api.controller.generic.ValidateController;
import pl.jsql.api.dto.request.BuildsRequest;
import pl.jsql.api.dto.request.QueriesRequest;
import pl.jsql.api.dto.request.RequestsRequest;
import pl.jsql.api.dto.response.*;
import pl.jsql.api.security.annotation.Security;
import pl.jsql.api.service.StatsService;

import javax.validation.Valid;

@CrossOrigin
@RestController
@RequestMapping("/api/stats")
public class StatsController extends ValidateController {

    @Autowired
    private StatsService statsService;

    @Security
    @PostMapping("/builds")
    public BasicResponse<PaginatedDataResponse<BuildsResponse>> getBuilds(@RequestParam("page") Integer page, @RequestBody @Valid BuildsRequest buildRequest) {
        PaginatedDataResponse<BuildsResponse> response = statsService.getBuilds(buildRequest, page);
        return new BasicResponse<>(200, response);
    }

    @Security
    @PostMapping("/queries")
    public BasicResponse<PaginatedDataResponse<QueriesResponse>> getQueries(@RequestParam("page") Integer page, @RequestBody @Valid QueriesRequest queriesRequest) {
        PaginatedDataResponse<QueriesResponse> response = statsService.getQueries(queriesRequest, page);
        return new BasicResponse<>(200, response);
    }

    @Security
    @PostMapping("/requests")
    public BasicResponse<PaginatedDataResponse<RequestsResponse>> getRequests(@RequestParam("page") Integer page, @RequestBody @Valid RequestsRequest requestsRequest) {
        PaginatedDataResponse<RequestsResponse> response = statsService.getRequests(requestsRequest, page);
        return new BasicResponse<>(200, response);
    }

}
