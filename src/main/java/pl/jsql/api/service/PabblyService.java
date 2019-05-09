package pl.jsql.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.jsql.api.enums.PabblyStatus;
import pl.jsql.api.service.pabbly.*;

import javax.transaction.Transactional;
import java.util.Map;

@Transactional
@Service
public class PabblyService {

    @Autowired
    private PabblySubscriptionCreateService pabblySubscriptionCreateService;

    @Autowired
    private PabblySubscriptionDowngradeService pabblySubscriptionDowngradeService;

    @Autowired
    private PabblySubscriptionCancelScheduledService pabblySubscriptionCancelScheduledService;

    @Autowired
    private PabblySubscriptionActivateService pabblySubscriptionActivateService;

    @Autowired
    private PabblySubscriptionCancelService pabblySubscriptionCancelService;

    @Autowired
    private PabblySubscriptionTrialExpiredService pabblySubscriptionTrialExpiredService;

    @Autowired
    private PabblySubscriptionUpgradeService pabblySubscriptionUpgradeService;

    @Autowired
    private PabblySubscriptionExpireService pabblySubscriptionExpireService;

    @Autowired
    private PabblySubscriptionRenewService pabblySubscriptionRenewService;

    @Autowired
    private PabblySubscriptionDeleteService pabblySubscriptionDeleteService;

    @Autowired
    private PabblySuccessfullPaymentService pabblySuccessfullPaymentService;

    @Autowired
    private PabblyPaymentFailureService pabblyPaymentFailureService;

    @Autowired
    private PabblyPaymentRefundService pabblyPaymentRefundService;

    @Autowired
    private PabblyOnDeleteSubscriptionService pabblyOnDeleteSubscriptionService;

    public void processPaymentStatus(Map<String, Object> request) {

        String eventTypeStr = (String) request.get("event_type");
        PabblyStatus eventType = PabblyStatus.valueOf(eventTypeStr.toUpperCase());

        switch (eventType) {
            case SUBSCRIPTION_CREATE:
                pabblySubscriptionCreateService.process(eventType, request);
                break;
            case SUBSCRIPTION_DOWNGRADE:
                pabblySubscriptionDowngradeService.process(eventType, request);
                break;
            case SUBSCRIPTION_CANCEL_SCHEDULED:
                pabblySubscriptionCancelScheduledService.process(eventType, request);
                break;
            case SUBSCRIPTION_ACTIVATE:
                pabblySubscriptionActivateService.process(eventType, request);
                break;
            case SUBSCRIPTION_CANCEL:
                pabblySubscriptionCancelService.process(eventType, request);
                break;
            case SUBSCRIPTION_TRIAL_EXPIRED:
                pabblySubscriptionTrialExpiredService.process(eventType, request);
                break;
            case SUBSCRIPTION_UPGRADE:
                pabblySubscriptionUpgradeService.process(eventType, request);
                break;
            case SUBSCRIPTION_EXPIRE:
                pabblySubscriptionExpireService.process(eventType, request);
                break;
            case SUBSCRIPTION_RENEW:
                pabblySubscriptionRenewService.process(eventType, request);
                break;
            case SUBSCRIPTION_DELETE:
                pabblySubscriptionDeleteService.process(eventType, request);
                break;
            case SUCCESSFULL_PAYMENT:
                pabblySuccessfullPaymentService.process(eventType, request);
                break;
            case PAYMENT_FAILURE:
                pabblyPaymentFailureService.process(eventType, request);
                break;
            case PAYMENT_REFUND:
                pabblyPaymentRefundService.process(eventType, request);
                break;

        }

    }


    public void deleteSubscription(String email) {
        pabblyOnDeleteSubscriptionService.deleteSubscription(email);
    }

}
