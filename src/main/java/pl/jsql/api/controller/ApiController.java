package pl.jsql.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.jsql.api.controller.generic.ValidateController;
import pl.jsql.api.dto.response.BasicResponse;
import pl.jsql.api.dto.response.OptionsResponse;
import pl.jsql.api.dto.response.QueryPairResponse;
import pl.jsql.api.dto.response.SimpleOptionsResponse;
import pl.jsql.api.security.annotation.HashingSecurity;
import pl.jsql.api.security.annotation.Security;
import pl.jsql.api.service.ApiService;
import pl.jsql.api.service.HashingService;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/request")
public class ApiController extends ValidateController {

    @Autowired
    private ApiService apiService;

    @Autowired
    private HashingService hashingService;

    @Security(requireActiveSession = false)
    @HashingSecurity
    @GetMapping("/options")
    public BasicResponse<SimpleOptionsResponse> getOptions(@RequestHeader(value = "devKey", required = true) String devKey, @RequestHeader(value = "apiKey", required = true) String apiKey) {
        SimpleOptionsResponse simpleOptionsResponse = apiService.getClientDatabaseOptions();
        return new BasicResponse<>(200, simpleOptionsResponse);
    }

    @Security(requireActiveSession = false)
    @HashingSecurity
    @GetMapping("/options/all")
    public BasicResponse<OptionsResponse> getAllOptions(@RequestHeader(value = "devKey", required = true) String devKey, @RequestHeader(value = "apiKey", required = true) String apiKey) {
        OptionsResponse response = hashingService.getClientOptions();
        return new BasicResponse<>(200, response);
    }

    @Security(requireActiveSession = false)
    @HashingSecurity
    @PostMapping("/hashes")
    public BasicResponse<List<QueryPairResponse>> hashQuery(@RequestBody List<String> request, @RequestHeader(value = "devKey", required = true) String devKey, @RequestHeader(value = "apiKey", required = true) String apiKey) {
        List<QueryPairResponse> response = apiService.getRequestHashesResult(request);
        return new BasicResponse<>(200, response);
    }

    @Security(requireActiveSession = false)
    @HashingSecurity
    @PostMapping("/queries")
    public BasicResponse<List<QueryPairResponse>> getHashedAsQuery(@RequestBody List<String> request, @RequestHeader(value = "devKey", required = true) String devKey, @RequestHeader(value = "apiKey", required = true) String apiKey) {
        List<QueryPairResponse> response = apiService.getRequestQueriesResult(request);
        return new BasicResponse<>(200, response);
    }

    @Security(requireActiveSession = false)
    @HashingSecurity
    @PostMapping("/queries/grouped")
    public BasicResponse<List<QueryPairResponse>> getHashedAsQueryGrouped(@RequestBody List<String> request, @RequestHeader(value = "devKey", required = true) String devKey, @RequestHeader(value = "apiKey", required = true) String apiKey) {
        List<QueryPairResponse> response = apiService.getRequestQueriesResult(request, true);
        return new BasicResponse<>(200, response);
    }
}
