package platform.core.common.service.domain.booking.port;



import platform.core.common.service.domain.booking.model.Booking;

import java.util.Optional;

public interface BookingRepositoryPort {

    Optional<Booking> findById(String bookingId);

    Optional<Booking> findByBookingCodeForUpdate(String bookingCode);

    Optional<Booking> findByBookingCode(String bookingCode);

    Optional<Booking> findById(String bookingId, String merchantId);

    Optional<Booking> findByRouteId(String routeId);

    java.util.List<Booking> findExpiredPendingPaymentBookingsForUpdate(java.time.OffsetDateTime holdUntil, int limit);

    Booking save(Booking booking);

    String generateBookingCode();
}
