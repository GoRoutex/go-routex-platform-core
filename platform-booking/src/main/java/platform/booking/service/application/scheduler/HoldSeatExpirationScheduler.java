package platform.booking.service.application.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import platform.booking.service.domain.paymentcontext.port.PaymentContextQueryPort;
import platform.core.common.service.common.RequestContext;
import platform.core.common.service.domain.booking.BookingSeatStatus;
import platform.core.common.service.domain.booking.BookingStatus;
import platform.core.common.service.domain.booking.model.Booking;
import platform.core.common.service.domain.booking.model.BookingSeat;
import platform.core.common.service.domain.booking.port.BookingRepositoryPort;
import platform.core.common.service.domain.booking.port.BookingSeatRepositoryPort;
import platform.core.common.service.domain.seat.SeatStatus;
import platform.core.common.service.domain.seat.model.TripSeat;
import platform.core.common.service.domain.seat.port.TripSeatRepositoryPort;
import vn.com.go.routex.identity.security.log.SystemLog;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class HoldSeatExpirationScheduler {

    private static final DateTimeFormatter REQUEST_DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

    private final BookingRepositoryPort bookingRepositoryPort;
    private final BookingSeatRepositoryPort bookingSeatRepositoryPort;
    private final TripSeatRepositoryPort tripSeatRepositoryPort;
    private final PaymentContextQueryPort paymentContextQueryPort;
    private final SystemLog sLog = SystemLog.getLogger(this.getClass());

    @Value("${booking.hold-expiration.batch-size:100}")
    private int batchSize;

    @Scheduled(
            fixedDelayString = "${booking.hold-expiration.fixed-delay-ms:15000}",
            initialDelayString = "${booking.hold-expiration.initial-delay-ms:15000}"
    )
    @Transactional
    public void releaseExpiredHeldSeats() {
        OffsetDateTime now = OffsetDateTime.now();
        List<Booking> expiredBookings = bookingRepositoryPort.findExpiredPendingPaymentBookingsForUpdate(now, batchSize);
        if (expiredBookings.isEmpty()) {
            return;
        }

        for (Booking booking : expiredBookings) {
            expireBooking(booking, now);
        }

        sLog.info("[BOOKING-SERVICE] Expired {} held booking(s) at {}", expiredBookings.size(), now);
    }

    private void expireBooking(Booking booking, OffsetDateTime now) {
        RequestContext context = schedulerContext(now);
        boolean alreadyPaid = paymentContextQueryPort.findByBookingCode(booking.getBookingCode(), context)
                .map(payment -> "PAID".equalsIgnoreCase(payment.getPaymentStatus()))
                .orElse(false);

        if (alreadyPaid) {
            sLog.info("[BOOKING-SERVICE] Skip expiration for bookingCode={} because payment is already PAID", booking.getBookingCode());
            return;
        }

        List<BookingSeat> bookingSeats = bookingSeatRepositoryPort.findAllByBookingId(booking.getId());
        if (!bookingSeats.isEmpty()) {
            List<String> seatNos = bookingSeats.stream()
                    .map(BookingSeat::getSeatNo)
                    .toList();

            List<TripSeat> tripSeats = tripSeatRepositoryPort.findAllByTripIdAndSeatNoInForUpdate(booking.getTripId(), seatNos);
            tripSeats.forEach(routeSeat -> {
                if (routeSeat.getStatus() == SeatStatus.HELD) {
                    routeSeat.setStatus(SeatStatus.AVAILABLE);
                }
            });
            tripSeatRepositoryPort.saveAll(tripSeats);

            List<BookingSeat> expiredSeats = bookingSeats.stream()
                    .map(bookingSeat -> (BookingSeat) BookingSeat.builder()
                            .id(bookingSeat.getId())
                            .bookingId(bookingSeat.getBookingId())
                            .tripId(bookingSeat.getTripId())
                            .seatNo(bookingSeat.getSeatNo())
                            .price(bookingSeat.getPrice())
                            .status(BookingSeatStatus.EXPIRED)
                            .ticketId(bookingSeat.getTicketId())
                            .creator(bookingSeat.getCreator())
                            .build())
                    .toList();
            bookingSeatRepositoryPort.saveAll(expiredSeats);
        }

        booking.setStatus(BookingStatus.EXPIRED);
        booking.setCancelledAt(now);
        booking.setNote("Hold expired without payment");
        bookingRepositoryPort.save(booking);
    }

    private RequestContext schedulerContext(OffsetDateTime now) {
        return RequestContext.builder()
                .requestId(UUID.randomUUID().toString())
                .requestDateTime(now.format(REQUEST_DATETIME_FORMAT))
                .channel("OFF")
                .build();
    }
}
