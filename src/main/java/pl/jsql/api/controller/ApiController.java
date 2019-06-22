package pl.jsql.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.jsql.api.controller.generic.ValidateController;
import pl.jsql.api.dto.request.ReportErrorRequest;
import pl.jsql.api.dto.response.*;
import pl.jsql.api.security.annotation.HashingSecurity;
import pl.jsql.api.security.annotation.Security;
import pl.jsql.api.service.ApiService;
import pl.jsql.api.service.HashingService;
import pl.jsql.api.service.OptionsService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static pl.jsql.api.security.interceptor.HashingSecurityInterceptor.API_KEY_HEADER;
import static pl.jsql.api.security.interceptor.HashingSecurityInterceptor.DEV_KEY_HEADER;

@CrossOrigin
@RestController
@RequestMapping("/api/request")
public class ApiController extends ValidateController {

    @Autowired
    private OptionsService optionsService;

    @Autowired
    private ApiService apiService;

    @Autowired
    private HashingService hashingService;

    @Security(requireActiveSession = false)
    @HashingSecurity
    @GetMapping("/options")
    public ResponseEntity<BasicResponse<SimpleOptionsResponse>> getOptions(@RequestHeader(value = DEV_KEY_HEADER, required = true) String devKey, @RequestHeader(value = API_KEY_HEADER, required = true) String apiKey) {
        SimpleOptionsResponse simpleOptionsResponse = apiService.getClientDatabaseOptions();
        return new ResponseEntity<>(new BasicResponse<>(200, simpleOptionsResponse), HttpStatus.OK);
    }

    @Security(requireActiveSession = false)
    @HashingSecurity
    @GetMapping("/options/all")
    public ResponseEntity<BasicResponse<OptionsResponse>> getAllOptions(@RequestHeader(value = DEV_KEY_HEADER, required = true) String devKey, @RequestHeader(value = API_KEY_HEADER, required = true) String apiKey) {
        OptionsResponse response = hashingService.getClientOptions();
        return new ResponseEntity<>(new BasicResponse<>(200, response), HttpStatus.OK);
    }

    @Security(requireActiveSession = false)
    @HashingSecurity
    @PostMapping("/hashes")
    public ResponseEntity<BasicResponse<List<QueryPairResponse>>> hashQuery(@RequestBody List<String> request,
                                                                            @RequestHeader(value = DEV_KEY_HEADER, required = true) String devKey,
                                                                            @RequestHeader(value = API_KEY_HEADER, required = true) String apiKey) {
        List<QueryPairResponse> response = apiService.getRequestHashesResult(request);
        return new ResponseEntity<>(new BasicResponse<>(200, response), HttpStatus.OK);
    }

    @Security(requireActiveSession = false)
    @HashingSecurity
    @PostMapping("/queries")
    public ResponseEntity<BasicResponse<List<QueryPairResponse>>> getHashedAsQuery(@RequestBody List<String> request, @RequestHeader(value = DEV_KEY_HEADER, required = true) String devKey, @RequestHeader(value = API_KEY_HEADER, required = true) String apiKey) {
        List<QueryPairResponse> response = apiService.getRequestQueriesResult(request);
        return new ResponseEntity<>(new BasicResponse<>(200, response), HttpStatus.OK);
    }

    @Security(requireActiveSession = false)
    @HashingSecurity
    @PostMapping("/queries/grouped")
    public ResponseEntity<BasicResponse<List<QueryPairResponse>>> getHashedAsQueryGrouped(@RequestBody List<String> request, @RequestHeader(value = DEV_KEY_HEADER, required = true) String devKey, @RequestHeader(value = API_KEY_HEADER, required = true) String apiKey) {
        List<QueryPairResponse> response = apiService.getRequestQueriesResult(request, true);
        return new ResponseEntity<>(new BasicResponse<>(200, response), HttpStatus.OK);
    }

    @Security(requireActiveSession = false)
    @PostMapping("/purge")
    public ResponseEntity<BasicResponse<PurgeResponse>> getPurge() {
        PurgeResponse response = optionsService.getPurge();
        return new ResponseEntity<>(new BasicResponse<>(200, response), HttpStatus.OK);
    }

//    @Security(requireActiveSession = false)
//    @PostMapping("/cache-info")
//    public ResponseEntity<BasicResponse<List<CacheInfoResponse>>> getCacheInfo() {
//        List<CacheInfoResponse> response = optionsService.getCacheInfo();
//        return new ResponseEntity<>(new BasicResponse<>(200, response), HttpStatus.OK);
//    }

    @Security(requireActiveSession = false)
    @HashingSecurity
    @PostMapping("/error")
    public ResponseEntity reportError(@RequestBody ReportErrorRequest request,
                                      @RequestHeader(value = DEV_KEY_HEADER, required = true) String devKey,
                                      @RequestHeader(value = API_KEY_HEADER, required = true) String apiKey, HttpServletRequest servletRequest) {
       apiService.reportError(request, servletRequest.getRemoteAddr());
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

}
