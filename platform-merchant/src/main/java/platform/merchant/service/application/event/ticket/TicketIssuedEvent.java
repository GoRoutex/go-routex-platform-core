package platform.merchant.service.application.event.ticket;

import lombok.Getter;
import platform.merchant.service.application.command.email.TicketEmailCommand;

@Getter
public class TicketIssuedEvent {
    private final TicketEmailCommand command;

    public TicketIssuedEvent(TicketEmailCommand command) {
        this.command = command;
    }
}
