package pl.jsql.api.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.*
import pl.jsql.api.controller.generic.ValidateController
import pl.jsql.api.dto.request.BuildsRequest
import pl.jsql.api.dto.request.QueriesRequest
import pl.jsql.api.dto.request.RequestsRequest
import pl.jsql.api.dto.response.BasicResponse
import pl.jsql.api.security.annotation.Security
import pl.jsql.api.service.StatsService

import javax.validation.Valid

@CrossOrigin
@RestController
@RequestMapping("/api/stats")
public class  StatsController extends ValidateController {

    @Autowired
    StatsService statsService

    @Security
    @PostMapping("/builds/{dateFrom}/{dateTo}")
    BasicResponse getStats(@PathVariable("dateFrom") @DateTimeFormat(pattern = "dd-MM-yyyy") Date dateFrom, @PathVariable("dateTo") @DateTimeFormat(pattern = "dd-MM-yyyy") Date dateTo, @RequestBody @Valid BuildsRequest buildRequest) {
        buildRequest.dateFrom = dateFrom
        buildRequest.dateTo = dateTo
        def response = statsService.getBuildsByUser(buildRequest)
        return new BasicResponse<>(200, response);
    }

    @Security
    @PostMapping("/queries/{dateFrom}/{dateTo}")
    BasicResponse getQueries(@PathVariable("dateFrom") @DateTimeFormat(pattern = "dd-MM-yyyy") Date dateFrom, @PathVariable("dateTo") @DateTimeFormat(pattern = "dd-MM-yyyy") Date dateTo, @RequestBody @Valid QueriesRequest queriesRequest) {
        queriesRequest.dateFrom = dateFrom
        queriesRequest.dateTo = dateTo
        def response = statsService.getQueries(queriesRequest)
        return new BasicResponse<>(200, response);
    }

    @Security
    @PostMapping("/requests/{dateFrom}/{dateTo}")
    BasicResponse getRequests(@PathVariable("dateFrom") @DateTimeFormat(pattern = "dd-MM-yyyy") Date dateFrom, @PathVariable("dateTo") @DateTimeFormat(pattern = "dd-MM-yyyy") Date dateTo, @RequestBody @Valid RequestsRequest requestsRequest) {
        requestsRequest.dateFrom = dateFrom
        requestsRequest.dateTo = dateTo
        def response = statsService.getRequests(requestsRequest)
        return new BasicResponse<>(200, response);
    }

}
