package pl.jsql.api.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pl.jsql.api.dto.QueryUpdateRequest
import pl.jsql.api.enums.RoleTypeEnum
import pl.jsql.api.security.annotation.HashingSecurity
import pl.jsql.api.security.annotation.Security
import pl.jsql.api.service.ApiService

@CrossOrigin
@RestController
@RequestMapping("/api/request")
class HashingController {

    @Autowired
    ApiService apiService

    @Security(requireActiveSession = false)
    @HashingSecurity
    @GetMapping("/options")
    def getOptions(
            @RequestHeader(value = "memberKey", required = false) String memberKey,
            @RequestHeader(value = "apiKey", required = false) String apiKey) {

        def data = apiService.getClientDatabaseOptions(memberKey, apiKey)

        return new ResponseEntity(data, HttpStatus.OK)
    }

    @Security(requireActiveSession = false)
    @HashingSecurity
    @GetMapping("/options/all")
    def getAllOptions(
            @RequestHeader(value = "memberKey", required = false) String memberKey,
            @RequestHeader(value = "apiKey", required = false) String apiKey) {

        def data = apiService.hashingService.getClientOptions(apiKey)

        return new ResponseEntity(data, HttpStatus.OK)
    }

    @Security(requireActiveSession = false)
    @HashingSecurity
    @PostMapping("/hashes")
    def hashQuery(@RequestBody def request,
                  @RequestHeader(value = "memberKey", required = false) String memberKey,
                  @RequestHeader(value = "apiKey", required = false) String apiKey) {

        def data = apiService.getRequestHashesResult(memberKey, apiKey, request)

        return new ResponseEntity(data, HttpStatus.OK)
    }

    @Security(requireActiveSession = false)
    @HashingSecurity
    @PostMapping("/queries")
    def getHashedAsQuery(@RequestBody def request,
                         @RequestHeader(value = "memberKey", required = false) String memberKey,
                         @RequestHeader(value = "apiKey", required = false) String apiKey) {

        def data = apiService.getRequestQueriesResult(memberKey, apiKey, request)
        return new ResponseEntity(data, HttpStatus.OK)
    }

    @Security(roles = [RoleTypeEnum.ADMIN, RoleTypeEnum.APP_ADMIN, RoleTypeEnum.APP_DEV])
    @PatchMapping("/query/{id}")
    def updateQuery(@PathVariable("id") Long id,
                    @RequestBody QueryUpdateRequest queryUpdateRequest,
                    @RequestHeader(value = "Session", required = false) String session) {

        def response = apiService.updateQueriesById(id, queryUpdateRequest)

        return new ResponseEntity(response, HttpStatus.OK)
    }

    @Security(requireActiveSession = false)
    @HashingSecurity
    @PostMapping("/queries/grouped")
    def getHashedAsQueryGrouped(@RequestBody def request,
                                @RequestHeader(value = "memberKey", required = false) String memberKey,
                                @RequestHeader(value = "apiKey", required = false) String apiKey) {

        def data = apiService.getRequestQueriesResult(memberKey, apiKey, request, true)
        return new ResponseEntity(data, HttpStatus.OK)
    }


}
