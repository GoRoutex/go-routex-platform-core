package platform.core.common.service.application.service;

import platform.core.common.service.api.BaseRequest;

public interface OutBoxService {
    void generateEvent(String aggregateId, String topic, String eventName, String eventKey, Object payload, BaseRequest context);
}
