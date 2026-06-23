package platform.booking.service.infrastructure.persistence.adapter.booking;

import org.springframework.stereotype.Component;
import platform.booking.service.infrastructure.persistence.jpa.booking.entity.BookingEntity;
import platform.booking.service.infrastructure.persistence.jpa.booking.entity.BookingLegEntity;
import platform.booking.service.infrastructure.persistence.jpa.booking.entity.BookingSeatEntity;
import platform.core.common.service.domain.booking.model.Booking;
import platform.core.common.service.domain.booking.model.BookingLeg;
import platform.core.common.service.domain.booking.model.BookingSeat;

@Component
public class BookingPersistenceMapper {

    public Booking toDomain(BookingEntity entity) {
        return Booking.builder()
                .id(entity.getId())
                .bookingCode(entity.getBookingCode())
                .merchantId(entity.getMerchantId())
                .customerId(entity.getCustomerId())
                .customerName(entity.getCustomerName())
                .customerPhone(entity.getCustomerPhone())
                .customerEmail(entity.getCustomerEmail())
                .channel(entity.getChannel())
                .seatCount(entity.getSeatCount())
                .totalAmount(entity.getTotalAmount())
                .currency(entity.getCurrency())
                .status(entity.getStatus())
                .heldAt(entity.getHeldAt())
                .holdUntil(entity.getHoldUntil())
                .cancelledAt(entity.getCancelledAt())
                .note(entity.getNote())
                .creator(entity.getCreator())
                .build();
    }

    public BookingEntity toEntity(Booking booking) {
        return BookingEntity.builder()
                .id(booking.getId())
                .bookingCode(booking.getBookingCode())
                .merchantId(booking.getMerchantId())
                .customerId(booking.getCustomerId())
                .customerName(booking.getCustomerName())
                .customerPhone(booking.getCustomerPhone())
                .customerEmail(booking.getCustomerEmail())
                .channel(booking.getChannel())
                .seatCount(booking.getSeatCount())
                .totalAmount(booking.getTotalAmount())
                .currency(booking.getCurrency())
                .status(booking.getStatus())
                .heldAt(booking.getHeldAt())
                .holdUntil(booking.getHoldUntil())
                .cancelledAt(booking.getCancelledAt())
                .note(booking.getNote())
                .creator(booking.getCreator())
                .build();
    }

    public BookingSeat toDomain(BookingSeatEntity entity) {
        return BookingSeat.builder()
                .id(entity.getId())
                .bookingId(entity.getBookingId())
                .bookingLegId(entity.getBookingLegId())
                .seatNo(entity.getSeatNo())
                .price(entity.getPrice())
                .status(entity.getStatus())
                .ticketId(entity.getTicketId())
                .ticketCode(entity.getTicketCode())
                .creator(entity.getCreator())
                .build();
    }

    public BookingSeatEntity toEntity(BookingSeat bookingSeat) {
        return BookingSeatEntity.builder()
                .id(bookingSeat.getId())
                .bookingId(bookingSeat.getBookingId())
                .bookingLegId(bookingSeat.getBookingLegId())
                .seatNo(bookingSeat.getSeatNo())
                .price(bookingSeat.getPrice())
                .status(bookingSeat.getStatus())
                .ticketId(bookingSeat.getTicketId())
                .ticketCode(bookingSeat.getTicketCode())
                .creator(bookingSeat.getCreator())
                .build();
    }

    public BookingLeg toDomain(BookingLegEntity entity) {
        return BookingLeg.builder()
                .id(entity.getId())
                .bookingId(entity.getBookingId())
                .tripId(entity.getTripId())
                .vehicleId(entity.getVehicleId())
                .pickupType(entity.getPickupType())
                .pickupStopId(entity.getPickupStopId())
                .pickupAddress(entity.getPickupAddress())
                .dropOffType(entity.getDropOffType())
                .dropOffStopId(entity.getDropOffStopId())
                .dropOffAddress(entity.getDropOffAddress())
                .build();
    }

    public BookingLegEntity toEntity(BookingLeg domain) {
        return BookingLegEntity.builder()
                .id(domain.getId())
                .bookingId(domain.getBookingId())
                .tripId(domain.getTripId())
                .vehicleId(domain.getVehicleId())
                .pickupType(domain.getPickupType())
                .pickupStopId(domain.getPickupStopId())
                .pickupAddress(domain.getPickupAddress())
                .dropOffType(domain.getDropOffType())
                .dropOffStopId(domain.getDropOffStopId())
                .dropOffAddress(domain.getDropOffAddress())
                .build();
    }
}
