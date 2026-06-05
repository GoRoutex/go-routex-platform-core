package platform.merchant.service.application.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import platform.core.common.service.infrastructure.kafka.event.EmailNotificationEvent;
import platform.core.common.service.infrastructure.kafka.model.KafkaEventMessage;
import platform.merchant.service.application.command.email.TicketEmailCommand;
import platform.merchant.service.application.service.EmailService;
import platform.merchant.service.infrastructure.persistence.utils.JsonUtils;
import vn.com.go.routex.identity.security.log.SystemLog;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final SystemLog sLog = SystemLog.getLogger(this.getClass());
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");

    @Value("${spring.kafka.events.notification-email}")
    private String emailEventName;

    @Override
    public void sendTicketConfirmation(TicketEmailCommand command) {
        sLog.info("[EMAIL-PUBLISHER] Preparing ticket confirmation email event for toEmail={}, ticketCode={}", command.toEmail(), command.ticketCode());

        Map<String, Object> variables = new HashMap<>();
        variables.put("customerName", command.customerName());
        variables.put("ticketCode", command.ticketCode());
        variables.put("seatNumber", command.seatNumber());
        variables.put("price", command.price());
        variables.put("routeName", command.routeName());
        variables.put("departureTime", command.departureTime().format(DATE_TIME_FORMATTER));
        variables.put("startPoint", command.startPoint());
        variables.put("endPoint", command.endPoint());
        variables.put("driverName", command.driverName());
        variables.put("driverPhone", command.driverPhone());
        variables.put("vehiclePlate", command.vehiclePlate());
        variables.put("vehicleType", command.vehicleType());

        EmailNotificationEvent emailEvent = EmailNotificationEvent.builder()
                .toEmail(command.toEmail())
                .subject("Xác nhận đặt vé thành công")
                .templateName("email/ticket-confirmation")
                .variables(variables)
                .build();

        KafkaEventMessage<EmailNotificationEvent> message = KafkaEventMessage.<EmailNotificationEvent>builder()
                .eventId(UUID.randomUUID().toString())
                .eventType(emailEventName)
                .aggregateId(command.toEmail())
                .occurredAt(OffsetDateTime.now())
                .payload(KafkaEventMessage.MessagePayload.<EmailNotificationEvent>builder()
                        .data(emailEvent)
                        .build())
                .build();

        try {
            String payload = JsonUtils.parseToJsonStr(message);
            sLog.info("[EMAIL-PUBLISHER] Publishing event payload to topic routex.notification.email");
            kafkaTemplate.send(emailEventName, command.toEmail(), payload);
            sLog.info("[EMAIL-PUBLISHER] Successfully published ticket confirmation email event to Kafka");
        } catch (Exception e) {
            sLog.error("[EMAIL-PUBLISHER] Failed to publish ticket confirmation email event to Kafka", e);
            throw new IllegalStateException("Failed to publish ticket confirmation email event", e);
        }
    }
}
