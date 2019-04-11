package pl.jsql.api.controller;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.jsql.api.controller.generic.ValidateController;
import pl.jsql.api.dto.request.PabblyPaymentRequest;
import pl.jsql.api.dto.response.BasicResponse;
import pl.jsql.api.dto.response.MessageResponse;
import pl.jsql.api.model.payment.Webhook;
import pl.jsql.api.repo.WebhookDao;
import pl.jsql.api.security.annotation.Security;
import pl.jsql.api.service.PaymentService;

@CrossOrigin
@RestController
@RequestMapping("/api/payment")
public class PaymentController extends ValidateController {

    @Autowired
    private WebhookDao webhookDao;

    @Autowired
    private PaymentService paymentService;

    @Security(requireActiveSession = false)
    @PostMapping
    public BasicResponse<MessageResponse> create(@RequestBody PabblyPaymentRequest pabblyPaymentRequest) {

        Webhook webhook = new Webhook();
        webhook.requestText = new Gson().toJson(pabblyPaymentRequest);
        webhookDao.save(webhook);

        paymentService.activeOrUnactivePlan(pabblyPaymentRequest);

        return new BasicResponse<>(200, new MessageResponse());

    }

}
