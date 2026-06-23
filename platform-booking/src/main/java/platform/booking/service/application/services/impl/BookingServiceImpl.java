package platform.booking.service.application.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import platform.booking.service.application.command.booking.CreateBookingCommand;
import platform.booking.service.application.services.BookingService;
import platform.booking.service.domain.tripcontext.model.TripBookingContext;
import platform.core.common.service.domain.booking.BookingSeatStatus;
import platform.core.common.service.domain.booking.BookingStatus;
import platform.core.common.service.domain.booking.model.Booking;
import platform.core.common.service.domain.booking.model.BookingSeat;
import platform.core.common.service.domain.booking.port.BookingLegRepositoryPort;
import platform.core.common.service.domain.booking.port.BookingRepositoryPort;
import platform.core.common.service.domain.booking.port.BookingSeatRepositoryPort;
import platform.core.common.service.domain.booking.model.BookingLeg;
import platform.core.common.service.domain.seat.model.TripSeat;
import platform.core.common.service.persistence.exception.BusinessException;
import platform.core.common.service.persistence.utils.ExceptionUtils;
import vn.com.go.routex.identity.security.log.SystemLog;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static platform.core.common.service.persistence.constant.ErrorConstant.INVALID_DATA_ERROR;
import static platform.core.common.service.persistence.constant.ErrorConstant.RECORD_NOT_FOUND;


@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepositoryPort bookingRepositoryPort;
    private final BookingLegRepositoryPort bookingLegRepositoryPort;
    private final BookingSeatRepositoryPort bookingSeatRepositoryPort;
    private final SystemLog sLog = SystemLog.getLogger(this.getClass());

    @Override
    @Transactional
    public Booking createBooking(CreateBookingCommand command, TripBookingContext tripContext, List<TripSeat> tripSeats) {
        sLog.info("[BOOK-SERVICE] Create Draft Booking Command: {}", command);

        if (tripContext == null) {
            throw new BusinessException(command.context().requestId(), command.context().requestDateTime(), command.context().channel(),
                    ExceptionUtils.buildResultResponse(RECORD_NOT_FOUND, "Trip booking context not found"));
        }
        if (tripContext.getTicketPrice() == null || tripContext.getVehicleId() == null || tripContext.getVehicleId().isBlank()) {
            throw new BusinessException(command.context().requestId(), command.context().requestDateTime(), command.context().channel(),
                    ExceptionUtils.buildResultResponse(INVALID_DATA_ERROR, "Trip booking context is missing ticket price or vehicle"));
        }

        BigDecimal basePrice = tripContext.getTicketPrice();
        BigDecimal totalAmount = basePrice.multiply(BigDecimal.valueOf(tripSeats.size()));

        Booking booking = Booking.builder()
                .id(UUID.randomUUID().toString())
                .bookingCode(bookingRepositoryPort.generateBookingCode())
                .merchantId(command.merchantId())
                .customerId(command.customerId())
                .customerName(command.customerName())
                .customerPhone(command.customerPhone())
                .customerEmail(command.customerEmail())
                .channel(command.context().channel())
                .seatCount(tripSeats.size())
                .totalAmount(totalAmount)
                .currency("VND")
                .status(BookingStatus.PENDING_PAYMENT)
                .heldAt(command.heldAt())
                .holdUntil(command.holdUntil())
                .creator(command.holdBy())
                .build();

        bookingRepositoryPort.save(booking);

        BookingLeg leg = BookingLeg.builder()
                .id(UUID.randomUUID().toString())
                .bookingId(booking.getId())
                .tripId(command.tripId())
                .vehicleId(tripContext.getVehicleId())
                .pickupType(command.pickupType())
                .pickupStopId(command.pickupStopId())
                .pickupAddress(command.pickupAddress())
                .dropOffType(command.dropOffType())
                .dropOffStopId(command.dropOffStopId())
                .dropOffAddress(command.dropOffAddress())
                .build();

        bookingLegRepositoryPort.save(leg);

        List<BookingSeat> bookingSeats = tripSeats.stream()
                .map(seat -> (BookingSeat) BookingSeat.builder()
                        .id(UUID.randomUUID().toString())
                        .bookingId(booking.getId())
                        .bookingLegId(leg.getId())
                        .seatNo(seat.getSeatNo())
                        .creator(command.holdBy())
                        .status(BookingSeatStatus.HELD)
                        .price(basePrice)
                        .build())
                .toList();

        bookingSeatRepositoryPort.saveAll(bookingSeats);

        sLog.info("[BOOK-SERVICE] Create Draft Booking successfully: {}", booking);
        return booking;
    }
}
