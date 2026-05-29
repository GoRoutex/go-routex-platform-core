package platform.payment.service.infrastructure.kafka.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import platform.core.common.service.common.RequestContext;
import platform.core.common.service.domain.payment.model.PaymentAggregate;
import platform.core.common.service.infrastructure.kafka.event.PaymentFailedEvent;
import platform.core.common.service.infrastructure.kafka.event.PaymentSuccessEvent;
import platform.payment.service.domain.payment.port.PaymentEventPublisherPort;
import platform.payment.service.infrastructure.persistence.utils.ApiRequestUtils;

@Component
@RequiredArgsConstructor
public class PaymentKafkaEventPublisherAdapter implements PaymentEventPublisherPort {

    private final platform.core.common.service.application.service.OutBoxService outBoxService;

    @Value("${spring.kafka.topics.payments}")
    private String paymentTopics;

    @Value("${spring.kafka.events.payment-succeeded}")
    private String paymentSucceededEvent;

    @Value("${spring.kafka.events.payment-failed}")
    private String paymentFailedEvent;

    @Override
    public void publishPaymentSucceeded(RequestContext context, PaymentAggregate paymentAggregate) {
        PaymentSuccessEvent payload = PaymentSuccessEvent.builder()
                .paymentId(paymentAggregate.getId())
                .bookingCode(paymentAggregate.getBookingCode())
                .amount(paymentAggregate.getAmount())
                .currency(paymentAggregate.getCurrency())
                .status(paymentAggregate.getStatus())
                .build();

        outBoxService.generateEvent(payload.bookingCode(), paymentTopics, paymentSucceededEvent, payload.paymentId(), payload, ApiRequestUtils.getHeader(context));
    }

    @Override
    public void publishPaymentFailed(RequestContext context, PaymentAggregate paymentAggregate, String reason) {
        PaymentFailedEvent payload = PaymentFailedEvent.builder()
                .paymentId(paymentAggregate.getId())
                .bookingCode(paymentAggregate.getBookingCode())
                .status(paymentAggregate.getStatus())
                .reason(reason)
                .build();

        outBoxService.generateEvent(payload.bookingCode(), paymentTopics, paymentFailedEvent, payload.paymentId(), payload, ApiRequestUtils.getHeader(context));
    }

}
