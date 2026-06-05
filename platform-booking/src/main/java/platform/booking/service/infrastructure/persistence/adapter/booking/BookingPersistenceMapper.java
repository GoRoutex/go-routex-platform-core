package platform.booking.service.infrastructure.persistence.adapter.booking;

import org.springframework.stereotype.Component;
import platform.booking.service.infrastructure.persistence.jpa.booking.entity.BookingEntity;
import platform.booking.service.infrastructure.persistence.jpa.booking.entity.BookingSeatEntity;
import platform.core.common.service.domain.booking.model.Booking;
import platform.core.common.service.domain.booking.model.BookingSeat;

@Component
public class BookingPersistenceMapper {

    public Booking toDomain(BookingEntity entity) {
        return Booking.builder()
                .id(entity.getId())
                .bookingCode(entity.getBookingCode())
                .tripId(entity.getTripId())
                .merchantId(entity.getMerchantId())
                .vehicleId(entity.getVehicleId())
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
                .pickupType(entity.getPickupType())
                .pickupStopId(entity.getPickupStopId())
                .pickupAddress(entity.getPickupAddress())
                .dropOffType(entity.getDropOffType())
                .dropOffStopId(entity.getDropOffStopId())
                .dropOffAddress(entity.getDropOffAddress())
                .build();
    }

    public BookingEntity toEntity(Booking booking) {
        return BookingEntity.builder()
                .id(booking.getId())
                .bookingCode(booking.getBookingCode())
                .tripId(booking.getTripId())
                .merchantId(booking.getMerchantId())
                .vehicleId(booking.getVehicleId())
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
                .pickupType(booking.getPickupType())
                .pickupStopId(booking.getPickupStopId())
                .pickupAddress(booking.getPickupAddress())
                .dropOffType(booking.getDropOffType())
                .dropOffStopId(booking.getDropOffStopId())
                .dropOffAddress(booking.getDropOffAddress())
                .build();
    }

    public BookingSeat toDomain(BookingSeatEntity entity) {
        return BookingSeat.builder()
                .id(entity.getId())
                .bookingId(entity.getBookingId())
                .tripId(entity.getTripId())
                .seatNo(entity.getSeatNo())
                .price(entity.getPrice())
                .status(entity.getStatus())
                .ticketId(entity.getTicketId())
                .creator(entity.getCreator())
                .build();
    }

    public BookingSeatEntity toEntity(BookingSeat bookingSeat) {
        return BookingSeatEntity.builder()
                .id(bookingSeat.getId())
                .bookingId(bookingSeat.getBookingId())
                .tripId(bookingSeat.getTripId())
                .seatNo(bookingSeat.getSeatNo())
                .price(bookingSeat.getPrice())
                .status(bookingSeat.getStatus())
                .ticketId(bookingSeat.getTicketId())
                .creator(bookingSeat.getCreator())
                .build();
    }
}
