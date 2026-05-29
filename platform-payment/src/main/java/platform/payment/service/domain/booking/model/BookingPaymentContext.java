package platform.payment.service.domain.booking.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import platform.core.common.service.domain.booking.BookingStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingPaymentContext {
    private String bookingId;
    private String bookingCode;
    private BigDecimal totalAmount;
    private String currency;
    private BookingStatus bookingStatus;
    private OffsetDateTime holdUntil;
}
