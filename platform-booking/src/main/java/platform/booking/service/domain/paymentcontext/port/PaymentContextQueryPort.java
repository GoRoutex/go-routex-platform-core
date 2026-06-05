package platform.booking.service.domain.paymentcontext.port;

import platform.booking.service.domain.paymentcontext.model.PaymentProcessingContext;
import platform.core.common.service.common.RequestContext;

import java.util.Optional;

public interface PaymentContextQueryPort {

    Optional<PaymentProcessingContext> findByBookingCode(String bookingCode, RequestContext context);
}
