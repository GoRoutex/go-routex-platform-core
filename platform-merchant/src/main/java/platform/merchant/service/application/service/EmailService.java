package platform.merchant.service.application.service;


import platform.merchant.service.application.command.email.TicketEmailCommand;

public interface EmailService {
    void sendTicketConfirmation(TicketEmailCommand command);
}
