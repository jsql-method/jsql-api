package pl.jsql.api.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pl.jsql.api.controller.generic.ValidateController
import pl.jsql.api.dto.response.BasicResponse
import pl.jsql.api.security.annotation.Security
import pl.jsql.api.service.PaymentService
import pl.jsql.api.service.StatsService

@CrossOrigin
@RestController
@RequestMapping("/api/plan")
class PlanController extends ValidateController {

    @Autowired
    PaymentService paymentService

    @Security
    @GetMapping
    BasicResponse get() {
        def response = paymentService.getPlan()
        return new BasicResponse(status: 200, data: response)
    }

}
