package platform.management.service.infrastructure.outbox.worker;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import platform.management.service.application.services.OutBoxRelayService;

@Component
@RequiredArgsConstructor
public class OutBoxRelayWorker {

    private final OutBoxRelayService outboxRelayService;

    @Scheduled(fixedDelayString = "${scheduled.polling.time}")
    public void relay() throws JsonProcessingException {
        outboxRelayService.pollingEvent();
    }
}
