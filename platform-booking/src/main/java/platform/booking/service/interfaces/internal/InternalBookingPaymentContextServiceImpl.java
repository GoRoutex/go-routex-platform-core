package platform.booking.service.interfaces.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import platform.core.common.service.api.BookingPaymentContextResponse;
import platform.core.common.service.api.InternalBookingPaymentContextService;
import platform.core.common.service.common.RequestContext;
import vn.com.go.routex.identity.security.log.SystemLog;
import platform.booking.service.application.services.BookingPaymentQueryService;
import platform.core.common.service.domain.booking.model.Booking;

@Service
@RequiredArgsConstructor
public class InternalBookingPaymentContextServiceImpl implements InternalBookingPaymentContextService {

    private final BookingPaymentQueryService bookingPaymentQueryService;
    private final SystemLog sLog = SystemLog.getLogger(this.getClass());

    @Override
    public BookingPaymentContextResponse getBookingPaymentContext(String bookingCode, RequestContext context) {
        sLog.info("[INTERNAL] Received fetchBookingPaymentContext request for bookingCode: {}", bookingCode);
        
        Booking booking = bookingPaymentQueryService.getBookingPaymentContext(bookingCode, context);

        return BookingPaymentContextResponse.builder()
                .bookingId(booking.getId())
                .bookingCode(booking.getBookingCode())
                .totalAmount(booking.getTotalAmount())
                .currency(booking.getCurrency())
                .bookingStatus(booking.getStatus() != null ? booking.getStatus().name() : null)
                .holdUntil(booking.getHoldUntil())
                .build();
    }
}
