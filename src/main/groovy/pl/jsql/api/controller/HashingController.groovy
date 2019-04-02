package pl.jsql.api.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pl.jsql.api.controller.generic.ValidateController
import pl.jsql.api.dto.request.QueryUpdateRequest
import pl.jsql.api.dto.response.BasicResponse
import pl.jsql.api.enums.RoleTypeEnum
import pl.jsql.api.security.annotation.HashingSecurity
import pl.jsql.api.security.annotation.Security
import pl.jsql.api.service.ApiService
import pl.jsql.api.service.HashingService

import javax.validation.Valid

@CrossOrigin
@RestController
@RequestMapping("/api/request")
class HashingController extends ValidateController {

    @Autowired
    ApiService apiService

    @Autowired
    HashingService hashingService

    @Security(requireActiveSession = false)
    @HashingSecurity
    @GetMapping("/options")
    BasicResponse getOptions(@RequestHeader(value = "memberKey", required = true) String memberKey, @RequestHeader(value = "apiKey", required = true) String apiKey) {
        def response = apiService.getClientDatabaseOptions(memberKey, apiKey)
        return new BasicResponse(status: 200, data: response)
    }

    @Security(requireActiveSession = false)
    @HashingSecurity
    @GetMapping("/options/all")
    BasicResponse getAllOptions(@RequestHeader(value = "memberKey", required = true) String memberKey, @RequestHeader(value = "apiKey", required = true) String apiKey) {
        def response = hashingService.getClientOptions(apiKey)
        return new BasicResponse(status: 200, data: response)
    }

    @Security(requireActiveSession = false)
    @HashingSecurity
    @PostMapping("/hashes")
    BasicResponse hashQuery(@RequestBody def request, @RequestHeader(value = "memberKey", required = true) String memberKey, @RequestHeader(value = "apiKey", required = true) String apiKey) {
        def response = apiService.getRequestHashesResult(memberKey, apiKey, request)
        return new BasicResponse(status: 200, data: response)
    }

    @Security(requireActiveSession = false)
    @HashingSecurity
    @PostMapping("/queries")
    BasicResponse getHashedAsQuery(@RequestBody def request, @RequestHeader(value = "memberKey", required = true) String memberKey, @RequestHeader(value = "apiKey", required = true) String apiKey) {
        def response = apiService.getRequestQueriesResult(memberKey, apiKey, request)
        return new BasicResponse(status: 200, data: response)
    }

    @Security(roles = [RoleTypeEnum.ADMIN, RoleTypeEnum.APP_ADMIN, RoleTypeEnum.APP_DEV])
    @PatchMapping("/query/{id}")
    BasicResponse updateQuery(@PathVariable("id") Long id, @RequestBody @Valid QueryUpdateRequest queryUpdateRequest) {
        def response = apiService.updateQueriesById(id, queryUpdateRequest)
        return new BasicResponse(status: 200, data: response)
    }

    @Security(requireActiveSession = false)
    @HashingSecurity
    @PostMapping("/queries/grouped")
    BasicResponse getHashedAsQueryGrouped(@RequestBody def request, @RequestHeader(value = "memberKey", required = true) String memberKey, @RequestHeader(value = "apiKey", required = true) String apiKey) {
        def response = apiService.getRequestQueriesResult(memberKey, apiKey, request, true)
        return new BasicResponse(status: 200, data: response)
    }

}
