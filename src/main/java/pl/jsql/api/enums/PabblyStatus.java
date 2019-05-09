package pl.jsql.api.enums;

public enum PabblyStatus {

    SUBSCRIPTION_CREATE, //done
    SUBSCRIPTION_DOWNGRADE,  //Brak testu
    SUBSCRIPTION_CANCEL_SCHEDULED, ///Nie ma potrzeby, Nie trzeba obsługiwać, potem poleci cancel w umówionym czasie
    SUBSCRIPTION_ACTIVATE, //done
    SUBSCRIPTION_CANCEL, //done
    SUBSCRIPTION_TRIAL_EXPIRED,   //done
    SUBSCRIPTION_UPGRADE, //done
    SUBSCRIPTION_EXPIRE,  ///Nie ma potrzeby
    SUBSCRIPTION_RENEW, //Nie ma potrzeby
    SUBSCRIPTION_DELETE,  //done
    TEST_WEBHOOK_URL,

    SUCCESSFULL_PAYMENT,
    PAYMENT_FAILURE,
    PAYMENT_REFUND,
    GET_CLIENT,
    HOSTED_VERIFY,
    SUBSCRIPTION_DELETED_MANUALLY;

}
