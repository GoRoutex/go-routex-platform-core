package platform.core.common.service.domain.booking.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.core.common.service.domain.booking.BookingSeatStatus;
import platform.core.common.service.persistence.AbstractAuditingEntity;

import java.math.BigDecimal;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class BookingSeat extends AbstractAuditingEntity {
    private String id;
    private String bookingId;
    private String tripId;
    private String seatNo;
    private BigDecimal price;
    private BookingSeatStatus status;
    private String creator;
    private String ticketId;
    private String ticketCode;
}
