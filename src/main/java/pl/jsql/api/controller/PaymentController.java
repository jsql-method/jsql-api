package pl.jsql.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.jsql.api.controller.generic.ValidateController;
import pl.jsql.api.dto.response.BasicResponse;
import pl.jsql.api.model.payment.Webhook;
import pl.jsql.api.repo.WebhookDao;
import pl.jsql.api.security.annotation.Security;
import pl.jsql.api.service.PaymentService;

@CrossOrigin
@RestController
@RequestMapping("/api/payment")
public class  PaymentController extends ValidateController {

    @Autowired
    WebhookDao webhookDao

    @Autowired
    PaymentService paymentService

    @Security(requireActiveSession = false)
    @PostMapping
    BasicResponse create(@RequestBody def request) {

        Webhook webhook = new Webhook()
        webhook.requestText = request
        webhookDao.save(webhook)
        paymentService.activeOrUnactivePlan(request)

        return new BasicResponse<>(200, response);

    }

}
