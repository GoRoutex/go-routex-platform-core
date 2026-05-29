package platform.booking.service.domain.paymentcontext.port;

import platform.core.common.service.common.RequestContext;
import platform.booking.service.domain.paymentcontext.model.PaymentProcessingContext;

import java.util.Optional;

public interface PaymentContextQueryPort {

    Optional<PaymentProcessingContext> findByBookingCode(String bookingCode, RequestContext context);
}
