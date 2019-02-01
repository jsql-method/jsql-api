package pl.jsql.api.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pl.jsql.api.security.annotation.Security
import pl.jsql.api.service.PaymentService
import pl.jsql.api.service.StatsService

@CrossOrigin
@RestController
@RequestMapping("/api/plan")
class PlanController {


    @Autowired
    PaymentService paymentService

    @Security
    @GetMapping
    def get(@RequestHeader(value = "Session", required = false) String session) {

        def response = paymentService.getPlan()

        return new ResponseEntity(response, HttpStatus.OK)
    }

}
