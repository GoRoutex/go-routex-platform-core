package platform.merchant.service.application.event.ticket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import platform.merchant.service.application.service.EmailService;

@Slf4j
@Component
@RequiredArgsConstructor
public class TicketEventListener {

    private final EmailService emailService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleTicketIssuedEvent(TicketIssuedEvent event) {
        log.info("[EVENT-LISTENER] Handling TicketIssuedEvent for ticket code: {}", event.getCommand().ticketCode());
        try {
            emailService.sendTicketConfirmation(event.getCommand());
        } catch (Exception e) {
            log.error("[EVENT-LISTENER] Error sending email for ticket code: {}", event.getCommand().ticketCode(), e);
        }
    }
}
