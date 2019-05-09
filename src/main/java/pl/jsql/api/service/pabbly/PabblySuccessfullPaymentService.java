package pl.jsql.api.service.pabbly;

import org.springframework.stereotype.Service;
import pl.jsql.api.enums.PabblyStatus;

import java.util.Map;

@Service
public class PabblySuccessfullPaymentService implements IPabbly {

    @Override
    public void process(PabblyStatus eventType, Map<String, Object> request) {

    }

}
