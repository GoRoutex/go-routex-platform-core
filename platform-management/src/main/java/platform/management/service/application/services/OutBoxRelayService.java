package platform.management.service.application.services;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface OutBoxRelayService {

    void pollingEvent() throws JsonProcessingException;
}
