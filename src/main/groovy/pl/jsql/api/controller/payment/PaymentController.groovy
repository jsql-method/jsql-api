package pl.jsql.api.controller.payment

import com.fasterxml.jackson.core.type.TypeReference
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ClassPathResource
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pl.jsql.api.model.payment.Webhook
import pl.jsql.api.repo.WebhookDao
import pl.jsql.api.security.annotation.Security
import pl.jsql.api.service.PaymentService

@CrossOrigin
@RestController
@RequestMapping("/api/payment")
class PaymentController {

    @Autowired
    WebhookDao webhookDao

    @Autowired
    PaymentService paymentService

    @Security(requireActiveSession = false)
    @PostMapping
    def create(@RequestBody def request) {

        Webhook webhook = new Webhook()
        webhook.requestText = request
        webhookDao.save(webhook)

        paymentService.activeOrUnactivePlan(request)

        return new ResponseEntity([code: 200, data: null], HttpStatus.OK)
    }

}
