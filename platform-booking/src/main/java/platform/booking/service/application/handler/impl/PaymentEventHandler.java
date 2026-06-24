package platform.booking.service.application.handler.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import platform.booking.service.application.handler.PaymentEvent;
import platform.booking.service.infrastructure.cache.mapper.TripCacheMapper;
import platform.booking.service.infrastructure.integration.merchantplatform.MerchantTicketGrpcAdapter;
import platform.core.common.service.api.BaseRequest;
import platform.core.common.service.application.service.OutBoxService;
import platform.core.common.service.domain.booking.BookingSeatStatus;
import platform.core.common.service.domain.booking.BookingStatus;
import platform.core.common.service.domain.booking.PaymentStatus;
import platform.core.common.service.domain.booking.model.Booking;
import platform.core.common.service.domain.booking.model.BookingLeg;
import platform.core.common.service.domain.booking.model.BookingSeat;
import platform.core.common.service.domain.booking.port.BookingLegRepositoryPort;
import platform.core.common.service.domain.booking.port.BookingRepositoryPort;
import platform.core.common.service.domain.booking.port.BookingSeatRepositoryPort;
import platform.core.common.service.domain.payment.model.PaymentAggregate;
import platform.core.common.service.domain.payment.port.PaymentRepositoryPort;
import platform.core.common.service.domain.seat.SeatStatus;
import platform.core.common.service.domain.seat.model.TripSeat;
import platform.core.common.service.domain.seat.port.TripSeatRepositoryPort;
import platform.core.common.service.domain.ticket.model.Ticket;
import platform.core.common.service.infrastructure.event.DomainEvent;
import platform.core.common.service.infrastructure.kafka.activity.RecentActivityPublisher;
import platform.core.common.service.infrastructure.kafka.event.PaymentFailedEvent;
import platform.core.common.service.infrastructure.kafka.event.PaymentSuccessEvent;
import platform.core.common.service.infrastructure.kafka.event.TicketIssuedEvent;
import platform.core.common.service.infrastructure.kafka.record.BookingAggregate;
import platform.core.common.service.infrastructure.redis.models.TripCacheSeat;
import platform.core.common.service.infrastructure.redis.service.TripSeatCacheService;
import platform.core.common.service.persistence.exception.BusinessException;
import platform.core.common.service.persistence.utils.ExceptionUtils;
import platform.merchant.service.domain.assignment.model.TripAssignmentRecord;
import platform.merchant.service.domain.assignment.port.TripAssignmentRepositoryPort;
import platform.merchant.service.domain.driver.model.DriverProfile;
import platform.merchant.service.domain.driver.port.DriverProfileRepositoryPort;
import platform.merchant.service.domain.trip.model.TripAggregate;
import platform.merchant.service.domain.trip.port.TripAggregateRepositoryPort;
import platform.merchant.service.domain.vehicle.model.VehicleProfile;
import platform.merchant.service.domain.vehicle.port.VehicleProfileRepositoryPort;
import vn.com.go.routex.identity.security.log.SystemLog;

import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static platform.core.common.service.persistence.constant.ErrorConstant.INVALID_DATA_ERROR;
import static platform.core.common.service.persistence.constant.ErrorConstant.PAYMENT_NOT_FOUND;
import static platform.core.common.service.persistence.constant.ErrorConstant.RECORD_NOT_FOUND;

@RequiredArgsConstructor
@Component
public class PaymentEventHandler implements PaymentEvent {

    private final BookingRepositoryPort bookingRepositoryPort;
    private final BookingLegRepositoryPort bookingLegRepositoryPort;
    private final TripSeatRepositoryPort tripSeatRepositoryPort;
    private final BookingSeatRepositoryPort bookingSeatRepositoryPort;
    private final MerchantTicketGrpcAdapter merchantTicketGrpcAdapter;
    private final PaymentRepositoryPort paymentRepositoryPort;
    private final TripAggregateRepositoryPort tripAggregateRepositoryPort;
    private final TripAssignmentRepositoryPort tripAssignmentRepositoryPort;
    private final DriverProfileRepositoryPort driverProfileRepositoryPort;
    private final VehicleProfileRepositoryPort vehicleProfileRepositoryPort;
    private final TripSeatCacheService tripSeatCacheService;
    private final OutBoxService outBoxService;
    private final TripCacheMapper tripCacheMapper;
    private final RecentActivityPublisher recentActivityPublisher;

    @Value("${spring.kafka.topics.booking}")
    private String bookingTopic;

    @Value("${spring.kafka.events.ticket-issued}")
    private String ticketIssuedEvent;

    private final SystemLog sLog = SystemLog.getLogger(this.getClass());

    @Override
    @Transactional
    public void updateSuccessPayment(DomainEvent event, BaseRequest context, PaymentSuccessEvent payload) {
        sLog.info("Updating success payment: {}", payload);
        BookingAggregate aggregate = loadAggregate(
                payload.bookingCode(),
                context.getRequestId(),
                context.getRequestDateTime(),
                context.getChannel()
        );

        // 1. Guard Clauses: Kiểm tra điều kiện dừng sớm
        if (isAlreadyProcessed(aggregate)) return;
        if (isInvalidStatus(aggregate)) return;
        validateUniqueBookedSeats(aggregate, context);

        // 2. Business Logic: Thực hiện nghiệp vụ chính
        OffsetDateTime paidAt = OffsetDateTime.now();
        List<Ticket> issuedTickets = processSuccessfulBooking(aggregate, context, paidAt);

        // 3. Persistence: Lưu trữ dữ liệu
        saveAggregate(aggregate, aggregate.bookingSeats(), aggregate.paymentAggregate(), aggregate.tripSeats());

        // 4. Cache & Integration: Cập nhật hạ tầng liên quan
        updateTripSeatCache(aggregate);
        publishTicketIssuedEvent(context, aggregate, issuedTickets, paidAt);
        publishPaymentSuccessActivities(event, context, payload, aggregate, issuedTickets);
        publishTicketIssuedActivity(event, context, aggregate, issuedTickets);
    }

    private boolean isAlreadyProcessed(BookingAggregate aggregate) {
        if (aggregate.booking().getStatus() == BookingStatus.CONFIRMED) {
            sLog.info("[BOOKING-SERVICE] Payment success already processed for bookingId={}", aggregate.booking().getId());
            return true;
        }
        return false;
    }

    private void validateUniqueBookedSeats(BookingAggregate aggregate, BaseRequest context) {
        Map<String, BookingLeg> legMap = aggregate.bookingLegs().stream()
                .collect(Collectors.toMap(BookingLeg::getId, leg -> leg));

        Map<String, Long> counts = aggregate.bookingSeats().stream()
                .collect(Collectors.groupingBy(
                        bookingSeat -> {
                            BookingLeg leg = legMap.get(bookingSeat.getBookingLegId());
                            String tripId = leg == null ? "" : leg.getTripId();
                            return tripId + ":" + bookingSeat.getSeatNo();
                        },
                        Collectors.counting()
                ));

        counts.entrySet().stream()
                .filter(entry -> entry.getValue() > 1)
                .findFirst()
                .ifPresent(entry -> {
                    throw new BusinessException(
                            context.getRequestId(),
                            context.getRequestDateTime(),
                            context.getChannel(),
                            ExceptionUtils.buildResultResponse(
                                    INVALID_DATA_ERROR,
                                    "Duplicate booked seat in payment event: " + entry.getKey()
                            )
                    );
                });
    }

    private boolean isInvalidStatus(BookingAggregate aggregate) {
        BookingStatus status = aggregate.booking().getStatus();
        if (status == BookingStatus.CANCELLED || status == BookingStatus.EXPIRED) {
            sLog.info("[BOOKING-SERVICE] Ignore payment for bookingId={} because status={}", aggregate.booking().getId(), status);
            return true;
        }
        return false;
    }

    private List<Ticket> processSuccessfulBooking(BookingAggregate aggregate, BaseRequest context, OffsetDateTime paidAt) {
        // Cập nhật trạng thái ghế trong Trip
        aggregate.tripSeats().forEach(seat -> seat.setStatus(SeatStatus.SOLD));
        // Xuất vé và gắn vào booking seats
        List<Ticket> tickets = createTickets(aggregate, paidAt);
        attachIssuedTickets(aggregate.bookingSeats(), tickets);

        // Cập nhật trạng thái thanh toán và booking
        aggregate.paymentAggregate().markPaid(paidAt);
        aggregate.booking().setStatus(BookingStatus.CONFIRMED);

        return tickets;
    }

    private void updateTripSeatCache(BookingAggregate aggregate) {
        aggregate.tripSeats().stream()
                .map(tripCacheMapper::toCacheModel)
                .collect(Collectors.groupingBy(TripCacheSeat::getTripId))
                .forEach((tripId, cacheSeats) -> tripSeatCacheService.updateSeatsStatus(
                        tripId,
                        cacheSeats.stream()
                                .sorted(Comparator.comparing(TripCacheSeat::getSeatNo))
                                .toList()
                ));
    }

    @Override
    @Transactional
    public void updateFailEvent(DomainEvent event, BaseRequest context, PaymentFailedEvent payload) {
        BookingAggregate aggregate = loadAggregate(
                payload.bookingCode(),
                context.getRequestId(),
                context.getRequestDateTime(),
                context.getChannel()
        );

        if (aggregate.booking().getStatus() == BookingStatus.CANCELLED
                || aggregate.booking().getStatus() == BookingStatus.EXPIRED) {
            sLog.info("[BOOKING-SERVICE] Payment failed event ignored for bookingId={} because current status={}",
                    aggregate.booking().getId(), aggregate.booking().getStatus());
            return;
        }

        aggregate.tripSeats().forEach(routeSeat -> routeSeat.setStatus(SeatStatus.AVAILABLE));
        List<BookingSeat> cancelledSeats = aggregate.bookingSeats().stream()
                .map(this::toCancelledBookingSeat)
                .toList();
        aggregate.booking().setStatus(BookingStatus.CANCELLED);
        PaymentAggregate paymentAggregate = aggregate.paymentAggregate();
        paymentAggregate.setStatus(PaymentStatus.FAILED);
        paymentAggregate.setFailedAt(OffsetDateTime.now());
        paymentAggregate.setFailureReason(payload.reason());
        saveAggregate(aggregate, cancelledSeats, paymentAggregate, aggregate.tripSeats());
        publishPaymentFailedActivities(event, context, payload, aggregate);
    }

    private BookingAggregate loadAggregate(
            String bookingCode,
            String requestId,
            String requestDateTime,
            String channel
    ) {
        Booking booking = bookingRepositoryPort.findByBookingCodeForUpdate(bookingCode)
                .orElseThrow(() -> new BusinessException(
                        requestId, requestDateTime, channel,
                        ExceptionUtils.buildResultResponse(RECORD_NOT_FOUND, "Booking not found")
                ));

        List<BookingSeat> bookingSeats = bookingSeatRepositoryPort.findAllByBookingId(booking.getId());
        if (bookingSeats.isEmpty()) {
            throw new BusinessException(
                    requestId, requestDateTime, channel,
                    ExceptionUtils.buildResultResponse(RECORD_NOT_FOUND, "Booking Seat not found")
            );
        }

        List<BookingLeg> bookingLegs = bookingLegRepositoryPort.findAllByBookingId(booking.getId());
        Map<String, BookingLeg> legMap = bookingLegs.stream().collect(Collectors.toMap(BookingLeg::getId, leg -> leg));

        PaymentAggregate paymentAggregate = paymentRepositoryPort.findByBookingCode(bookingCode)
                .orElseThrow(() -> new BusinessException(requestId, requestDateTime, channel,
                        ExceptionUtils.buildResultResponse(RECORD_NOT_FOUND, String.format(PAYMENT_NOT_FOUND, bookingCode))));

        List<TripSeat> tripSeats = bookingSeats.stream()
                .map(bookingSeat -> {
                    BookingLeg leg = legMap.get(bookingSeat.getBookingLegId());
                    return tripSeatRepositoryPort.findByTripIdAndSeatNo(leg.getTripId(), bookingSeat.getSeatNo())
                            .orElseThrow(() -> new BusinessException(
                                    requestId, requestDateTime, channel,
                                    ExceptionUtils.buildResultResponse(RECORD_NOT_FOUND, "Route Seat not found")
                            ));
                })
                .toList();

        return new BookingAggregate(booking, bookingLegs, bookingSeats, tripSeats, paymentAggregate);
    }

    private List<Ticket> createTickets(BookingAggregate aggregate, OffsetDateTime paidAt) {
        return merchantTicketGrpcAdapter.createTickets(aggregate, paidAt);
    }


    private void attachIssuedTickets(List<BookingSeat> bookingSeats, List<Ticket> tickets) {

        bookingSeats.forEach(bookingSeat -> {
            sLog.info("Ticket: {}", tickets);
            Ticket matchedTicket = tickets.stream()
                    .filter(ticket -> ticket.getBookingSeatId().equals(bookingSeat.getId()))
                    .findFirst()
                    .orElseThrow();

            bookingSeat.setStatus(BookingSeatStatus.RESERVED);
            bookingSeat.setTicketId(matchedTicket.getId());
            bookingSeat.setTicketCode(matchedTicket.getTicketCode());
        });
    }

    private BookingSeat toCancelledBookingSeat(BookingSeat bookingSeat) {
        return BookingSeat.builder()
                .id(bookingSeat.getId())
                .bookingId(bookingSeat.getBookingId())
                .bookingLegId(bookingSeat.getBookingLegId())
                .seatNo(bookingSeat.getSeatNo())
                .price(bookingSeat.getPrice())
                .status(BookingSeatStatus.CANCELLED)
                .ticketId(bookingSeat.getTicketId())
                .creator(bookingSeat.getCreator())
                .build();
    }

    private void saveAggregate(BookingAggregate aggregate, List<BookingSeat> bookingSeats, PaymentAggregate paymentAggregate, List<TripSeat> tripSeats) {
        bookingSeatRepositoryPort.saveAll(bookingSeats);
        bookingRepositoryPort.save(aggregate.booking());
        paymentRepositoryPort.save(paymentAggregate);
        tripSeatRepositoryPort.saveAll(tripSeats);
    }

    private void publishTicketIssuedEvent(BaseRequest context,
                                          BookingAggregate aggregate,
                                          List<Ticket> issuedTickets,
                                          OffsetDateTime paidAt) {
        String firstTripId = aggregate.bookingLegs().isEmpty() ? null : aggregate.bookingLegs().get(0).getTripId();
        TripAggregate trip = firstTripId != null ? tripAggregateRepositoryPort.findById(firstTripId).orElse(null) : null;
        TripTransportInfo transportInfo = resolveTripTransportInfo(firstTripId);
        TicketIssuedEvent payload = TicketIssuedEvent.builder()
                .bookingId(aggregate.booking().getId())
                .bookingCode(aggregate.booking().getBookingCode())
                .customerId(aggregate.booking().getCustomerId())
                .customerName(aggregate.booking().getCustomerName())
                .customerPhone(aggregate.booking().getCustomerPhone())
                .customerEmail(aggregate.booking().getCustomerEmail())
                .merchantId(aggregate.booking().getMerchantId())
                .tripId(firstTripId)
                .driverName(transportInfo.driverName())
                .driverPhone(transportInfo.driverPhone())
                .vehiclePlate(transportInfo.vehiclePlate())
                .departureTime(trip != null ? trip.getDepartureTime() : null)
                .totalAmount(aggregate.booking().getTotalAmount())
                .currency(aggregate.booking().getCurrency())
                .paidAt(paidAt)
                .tickets(issuedTickets.stream()
                        .map(ticket -> TicketIssuedEvent.TicketIssuedItem.builder()
                                .ticketId(ticket.getId())
                                .ticketCode(ticket.getTicketCode())
                                .bookingSeatId(ticket.getBookingSeatId())
                                .seatNumber(ticket.getSeatNumber())
                                .price(ticket.getPrice())
                                .status(ticket.getStatus())
                                .issuedAt(ticket.getIssuedAt())
                                .build())
                        .toList())
                .build();

        outBoxService.generateEvent(
                aggregate.booking().getId(),
                bookingTopic,
                ticketIssuedEvent,
                ticketIssuedEvent,
                payload,
                context
        );
    }

    private TripTransportInfo resolveTripTransportInfo(String tripId) {
        if (tripId == null || tripId.isBlank()) {
            return TripTransportInfo.empty();
        }
        TripAssignmentRecord assignment = tripAssignmentRepositoryPort.findActiveByTripId(tripId).orElse(null);
        if (assignment == null) {
            return TripTransportInfo.empty();
        }

        DriverProfile driver = assignment.getDriverId() == null
                ? null
                : driverProfileRepositoryPort.findById(assignment.getDriverId()).orElse(null);
        VehicleProfile vehicle = assignment.getVehicleId() == null
                ? null
                : vehicleProfileRepositoryPort.findById(assignment.getVehicleId()).orElse(null);

        return new TripTransportInfo(
                driver == null ? null : driver.getFullName(),
                driver == null ? null : driver.getPhoneNumber(),
                vehicle == null ? null : vehicle.getVehiclePlate()
        );
    }

    private record TripTransportInfo(String driverName, String driverPhone, String vehiclePlate) {
        private static TripTransportInfo empty() {
            return new TripTransportInfo(null, null, null);
        }
    }

    private void publishPaymentSuccessActivities(DomainEvent event,
                                                  BaseRequest context,
                                                  PaymentSuccessEvent payload,
                                                  BookingAggregate aggregate,
                                                  List<Ticket> issuedTickets) {
        Booking booking = aggregate.booking();
        Map<String, Object> metadata = metadata();
        metadata.put("bookingCode", booking.getBookingCode());
        metadata.put("paymentId", payload.paymentId());
        metadata.put("amount", payload.amount());
        metadata.put("currency", payload.currency());
        metadata.put("ticketCount", issuedTickets.size());

        String title = "Payment succeeded";
        String message = "Payment succeeded for booking " + booking.getBookingCode();

        recentActivityPublisher.publishAdminActivity(
                "PAYMENT_SUCCESS",
                booking.getId(),
                "INFO",
                "SUCCESS",
                "platform-booking",
                correlationId(event, context),
                title,
                message,
                booking.getCustomerId(),
                booking.getCustomerName(),
                "BOOKING",
                booking.getId(),
                booking.getBookingCode(),
                metadata
        );
        recentActivityPublisher.publishMerchantActivity(
                booking.getMerchantId(),
                "PAYMENT_SUCCESS",
                booking.getId(),
                "INFO",
                "SUCCESS",
                "platform-booking",
                correlationId(event, context),
                title,
                message,
                booking.getCustomerId(),
                booking.getCustomerName(),
                "BOOKING",
                booking.getId(),
                booking.getBookingCode(),
                metadata
        );
    }

    private void publishPaymentFailedActivities(DomainEvent event,
                                                BaseRequest context,
                                                PaymentFailedEvent payload,
                                                BookingAggregate aggregate) {
        Booking booking = aggregate.booking();
        Map<String, Object> metadata = metadata();
        metadata.put("bookingCode", booking.getBookingCode());
        metadata.put("paymentId", payload.paymentId());
        metadata.put("reason", payload.reason());

        String title = "Payment failed";
        String message = "Payment failed for booking " + booking.getBookingCode();

        recentActivityPublisher.publishAdminActivity(
                "PAYMENT_FAILED",
                booking.getId(),
                "WARNING",
                "FAILED",
                "platform-booking",
                correlationId(event, context),
                title,
                message,
                booking.getCustomerId(),
                booking.getCustomerName(),
                "BOOKING",
                booking.getId(),
                booking.getBookingCode(),
                metadata
        );
        recentActivityPublisher.publishMerchantActivity(
                booking.getMerchantId(),
                "PAYMENT_FAILED",
                booking.getId(),
                "WARNING",
                "FAILED",
                "platform-booking",
                correlationId(event, context),
                title,
                message,
                booking.getCustomerId(),
                booking.getCustomerName(),
                "BOOKING",
                booking.getId(),
                booking.getBookingCode(),
                metadata
        );
    }

    private void publishTicketIssuedActivity(DomainEvent event,
                                             BaseRequest context,
                                             BookingAggregate aggregate,
                                             List<Ticket> issuedTickets) {
        Booking booking = aggregate.booking();
        Map<String, Object> metadata = metadata();
        metadata.put("bookingCode", booking.getBookingCode());
        metadata.put("tripIds", aggregate.bookingLegs().stream().map(BookingLeg::getTripId).distinct().toList());
        metadata.put("ticketCount", issuedTickets.size());
        metadata.put("ticketCodes", issuedTickets.stream().map(Ticket::getTicketCode).toList());

        recentActivityPublisher.publishMerchantActivity(
                booking.getMerchantId(),
                "TICKET_ISSUED",
                booking.getId(),
                "INFO",
                "SUCCESS",
                "platform-booking",
                correlationId(event, context),
                "Tickets issued",
                "Tickets issued for booking " + booking.getBookingCode(),
                booking.getCustomerId(),
                booking.getCustomerName(),
                "BOOKING",
                booking.getId(),
                booking.getBookingCode(),
                metadata
        );
    }

    private Map<String, Object> metadata() {
        return new HashMap<>();
    }

    private String correlationId(DomainEvent event, BaseRequest context) {
        if (event != null && event.eventId() != null && !event.eventId().isBlank()) {
            return event.eventId();
        }
        return context == null ? null : context.getRequestId();
    }
}
