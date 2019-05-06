package pl.jsql.api.controller;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.jsql.api.controller.generic.ValidateController;
import pl.jsql.api.dto.request.PabblyPaymentRequest;
import pl.jsql.api.dto.response.BasicResponse;
import pl.jsql.api.dto.response.MessageResponse;
import pl.jsql.api.enums.PabblyStatus;
import pl.jsql.api.model.payment.Webhook;
import pl.jsql.api.repo.WebhookDao;
import pl.jsql.api.security.annotation.Security;
import pl.jsql.api.service.PaymentService;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private WebhookDao webhookDao;

    @Autowired
    private PaymentService paymentService;

    @Security(requireActiveSession = false)
    @PostMapping
    public ResponseEntity create(@RequestBody Object pabblyPaymentRequest) {

        try {

            System.out.println("pabblyPaymentRequest :"+pabblyPaymentRequest);

            System.out.println("pabblyPaymentRequest json : "+new Gson().toJson(pabblyPaymentRequest));

            HashMap<String, Object> request = (HashMap<String, Object>) pabblyPaymentRequest;

            Webhook webhook = new Webhook();
            webhook.requestText = new Gson().toJson(pabblyPaymentRequest);
            webhook.pabblyStatus = PabblyStatus.valueOf(request.get("event_type").toString().toUpperCase());
            webhookDao.save(webhook);

            if(webhook.pabblyStatus != PabblyStatus.TEST_WEBHOOK_URL){

                try {
                    paymentService.activeOrUnactivePlan(request);
                }catch (Exception e){
                    System.out.println("ERROR PAYMENT2");
                    e.printStackTrace();
                }

            }

        }catch (Exception e){

            System.out.println("ERROR PAYMENT");
            e.printStackTrace();

        }

        return new ResponseEntity<>(new BasicResponse<>(200, new MessageResponse()), HttpStatus.OK);

    }

}
