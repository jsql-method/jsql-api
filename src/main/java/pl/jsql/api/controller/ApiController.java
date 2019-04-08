package pl.jsql.api.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*
import pl.jsql.api.controller.generic.ValidateController
import pl.jsql.api.dto.request.QueryUpdateRequest
import pl.jsql.api.dto.response.BasicResponse
import pl.jsql.api.dto.response.DatabaseOptionsResponse;
import pl.jsql.api.enums.RoleTypeEnum
import pl.jsql.api.security.annotation.HashingSecurity
import pl.jsql.api.security.annotation.Security
import pl.jsql.api.service.ApiService
import pl.jsql.api.service.HashingService

import javax.validation.Valid

@CrossOrigin
@RestController
@RequestMapping("/api/request")
public class  ApiController extends ValidateController {

    @Autowired
    ApiService apiService

    @Autowired
    HashingService hashingService

    @Security(requireActiveSession = false)
    @HashingSecurity
    @GetMapping("/options")
    BasicResponse<DatabaseOptionsResponse> getOptions(@RequestHeader(value = "devKey", required = true) String devKey, @RequestHeader(value = "apiKey", required = true) String apiKey) {
        DatabaseOptionsResponse databaseOptionsResponse = apiService.getClientDatabaseOptions()
        return new BasicResponse(200, databaseOptionsResponse);
    }

    @Security(requireActiveSession = false)
    @HashingSecurity
    @GetMapping("/options/all")
    BasicResponse getAllOptions(@RequestHeader(value = "devKey", required = true) String devKey, @RequestHeader(value = "apiKey", required = true) String apiKey) {
        def response = hashingService.getClientOptions()
        return new BasicResponse(status: 200, data: response)
    }

    @Security(requireActiveSession = false)
    @HashingSecurity
    @PostMapping("/hashes")
    BasicResponse hashQuery(@RequestBody List<String> request, @RequestHeader(value = "devKey", required = true) String devKey, @RequestHeader(value = "apiKey", required = true) String apiKey) {
        def response = apiService.getRequestHashesResult(request)
        return new BasicResponse(status: 200, data: response)
    }

    @Security(requireActiveSession = false)
    @HashingSecurity
    @PostMapping("/queries")
    BasicResponse getHashedAsQuery(@RequestBody List<String> request, @RequestHeader(value = "devKey", required = true) String devKey, @RequestHeader(value = "apiKey", required = true) String apiKey) {
        def response = apiService.getRequestQueriesResult(request)
        return new BasicResponse(status: 200, data: response)
    }

    @Security(requireActiveSession = false)
    @HashingSecurity
    @PostMapping("/queries/grouped")
    BasicResponse getHashedAsQueryGrouped(@RequestBody List<String> request, @RequestHeader(value = "devKey", required = true) String devKey, @RequestHeader(value = "apiKey", required = true) String apiKey) {
        def response = apiService.getRequestQueriesResult(request, true)
        return new BasicResponse(status: 200, data: response)
    }

}
