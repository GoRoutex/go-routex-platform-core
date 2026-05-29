package platform.booking.service.infrastructure.persistence.jpa.booking.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity(name = "Booking_BookingSeatEntity")
@Table(name = "BOOKING_SEAT")
public class BookingSeatEntity extends AbstractAuditingEntity {

    @Id
    private String id;

    @Column(name = "BOOKING_ID", nullable = false)
    private String bookingId;

    @Column(name = "TRIP_ID", nullable = false)
    private String tripId;

    @Column(name = "SEAT_NO")
    private String seatNo;

    @Column(name = "PRICE")
    private BigDecimal price;

    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    private BookingSeatStatus status;

    @Column(name = "TICKET_ID")
    private String ticketId;

    @Column(name = "CREATOR")
    private String creator;

}
