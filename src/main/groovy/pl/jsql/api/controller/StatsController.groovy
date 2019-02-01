package pl.jsql.api.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.jpa.domain.JpaSort
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pl.jsql.api.dto.BuildsRequest
import pl.jsql.api.dto.QueriesRequest
import pl.jsql.api.dto.RequestsRequest
import pl.jsql.api.security.annotation.HashingSecurity
import pl.jsql.api.security.annotation.Security
import pl.jsql.api.service.StatsService

@CrossOrigin
@RestController
@RequestMapping("/api")
class StatsController {


    @Autowired
    StatsService statsService

    @Security
    @PostMapping("/builds/{dateFrom}/{dateTo}")
    def getStats(
            @RequestHeader(value = "Session", required = false) String session,
            @PathVariable("dateFrom") @DateTimeFormat(pattern = "dd-MM-yyyy") Date dateFrom,
            @PathVariable("dateTo") @DateTimeFormat(pattern = "dd-MM-yyyy") Date dateTo,
            @RequestBody BuildsRequest buildRequest) {

        buildRequest.dateFrom = dateFrom
        buildRequest.dateTo = dateTo

        def response = statsService.getBuildsByUser(buildRequest)

        return new ResponseEntity(response, HttpStatus.OK)
    }

    @Security
    @PostMapping("/queries/{dateFrom}/{dateTo}")
    def getQueries(@RequestHeader(value = "Session", required = false) String session,
                   @PathVariable("dateFrom") @DateTimeFormat(pattern = "dd-MM-yyyy") Date dateFrom,
                   @PathVariable("dateTo") @DateTimeFormat(pattern = "dd-MM-yyyy") Date dateTo,
                   @RequestBody QueriesRequest queriesRequest) {

        queriesRequest.dateFrom = dateFrom
        queriesRequest.dateTo = dateTo

        def data = statsService.getQueries(queriesRequest)
        return new ResponseEntity(data, HttpStatus.OK)
    }

    @Security
    @PostMapping("/requests/{dateFrom}/{dateTo}")
    def getRequests(@RequestHeader(value = "Session", required = false) String session,
                    @PathVariable("dateFrom") @DateTimeFormat(pattern = "dd-MM-yyyy") Date dateFrom,
                    @PathVariable("dateTo") @DateTimeFormat(pattern = "dd-MM-yyyy") Date dateTo,
                    @RequestBody RequestsRequest requestsRequest) {

        requestsRequest.dateFrom = dateFrom
        requestsRequest.dateTo = dateTo

        def data = statsService.getRequests(requestsRequest)
        return new ResponseEntity(data, HttpStatus.OK)
    }

}
