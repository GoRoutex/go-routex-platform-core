package platform.booking.service.infrastructure.integration.payment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import platform.booking.service.domain.paymentcontext.model.PaymentProcessingContext;
import platform.booking.service.domain.paymentcontext.port.PaymentContextQueryPort;
import platform.core.common.service.api.InternalPaymentContextService;
import platform.core.common.service.common.RequestContext;
import platform.core.common.service.domain.payment.model.PaymentAggregate;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PaymentContextInternalAdapter implements PaymentContextQueryPort {

    private final InternalPaymentContextService internalPaymentContextService;

    @Override
    public Optional<PaymentProcessingContext> findByBookingCode(String bookingCode, RequestContext context) {
        try {
            PaymentAggregate payment = internalPaymentContextService.getPaymentContext(bookingCode, context);
            if (payment == null || payment.getId() == null || payment.getId().isEmpty()) {
                return Optional.empty();
            }

            PaymentProcessingContext processingContext = PaymentProcessingContext.builder()
                    .paymentId(payment.getId())
                    .bookingCode(payment.getBookingCode())
                    .paymentStatus(payment.getStatus() != null ? payment.getStatus().name() : null)
                    .paidAt(payment.getPaidAt())
                    .build();
            return java.util.Optional.of(processingContext);
        } catch (Exception ex) {
            return java.util.Optional.empty();
        }
    }
}
