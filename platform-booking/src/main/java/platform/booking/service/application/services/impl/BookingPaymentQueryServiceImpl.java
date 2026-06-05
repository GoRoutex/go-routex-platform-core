package platform.booking.service.application.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import platform.booking.service.application.services.BookingPaymentQueryService;
import platform.core.common.service.common.RequestContext;
import platform.core.common.service.domain.booking.model.Booking;
import platform.core.common.service.domain.booking.port.BookingRepositoryPort;
import platform.core.common.service.persistence.exception.BusinessException;
import platform.core.common.service.persistence.utils.ExceptionUtils;

import static platform.core.common.service.persistence.constant.ErrorConstant.RECORD_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class BookingPaymentQueryServiceImpl implements BookingPaymentQueryService {

    private final BookingRepositoryPort bookingRepositoryPort;

    @Override
    public Booking getBookingPaymentContext(String bookingCode, RequestContext context) {
        return bookingRepositoryPort.findByBookingCode(bookingCode)
                .orElseThrow(() -> new BusinessException(
                        context.requestId(),
                        context.requestDateTime(),
                        context.channel(),
                        ExceptionUtils.buildResultResponse(RECORD_NOT_FOUND, "Booking not found")
                ));
    }
}
