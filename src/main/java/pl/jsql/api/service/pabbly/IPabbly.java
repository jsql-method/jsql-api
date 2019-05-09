package pl.jsql.api.service.pabbly;

import pl.jsql.api.enums.PabblyStatus;

import java.util.Map;

public interface IPabbly {

    public void process(PabblyStatus eventType, Map<String, Object> request);

}
