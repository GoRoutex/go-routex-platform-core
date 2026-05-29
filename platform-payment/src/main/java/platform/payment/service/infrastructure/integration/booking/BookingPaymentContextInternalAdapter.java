package platform.payment.service.infrastructure.integration.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import platform.core.common.service.api.BookingPaymentContextResponse;
import platform.core.common.service.api.InternalBookingPaymentContextService;
import platform.core.common.service.common.RequestContext;
import platform.core.common.service.domain.booking.BookingStatus;
import platform.payment.service.domain.booking.model.BookingPaymentContext;
import platform.payment.service.domain.booking.port.BookingPaymentQueryPort;
import platform.core.common.service.persistence.exception.BusinessException;
import platform.core.common.service.persistence.utils.ExceptionUtils;

import java.math.BigDecimal;

import static platform.core.common.service.persistence.constant.ErrorConstant.BOOKING_CODE_NOT_FOUND;
import static platform.core.common.service.persistence.constant.ErrorConstant.RECORD_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class BookingPaymentContextInternalAdapter implements BookingPaymentQueryPort {

    private final InternalBookingPaymentContextService internalBookingPaymentContextService;

    @Override
    public BookingPaymentContext getBookingPaymentContext(String bookingCode, RequestContext context) {
        BookingPaymentContextResponse response = internalBookingPaymentContextService.getBookingPaymentContext(bookingCode, context);

        if (response == null || response.bookingId().isEmpty()) {
            throw bookingNotFound(bookingCode, context);
        }

        return BookingPaymentContext.builder()
                .bookingId(response.bookingId())
                .bookingCode(response.bookingCode())
                .totalAmount(response.totalAmount() == null ? BigDecimal.ZERO : response.totalAmount())
                .currency(response.currency())
                .bookingStatus(response.bookingStatus() == null || response.bookingStatus().isEmpty() ? null : BookingStatus.valueOf(response.bookingStatus()))
                .holdUntil(response.holdUntil())
                .build();
    }

    private BusinessException bookingNotFound(String bookingCode, RequestContext context) {
        return new BusinessException(
                context.requestId(),
                context.requestDateTime(),
                context.channel(),
                ExceptionUtils.buildResultResponse(RECORD_NOT_FOUND, String.format(BOOKING_CODE_NOT_FOUND, bookingCode))
        );
    }
}
