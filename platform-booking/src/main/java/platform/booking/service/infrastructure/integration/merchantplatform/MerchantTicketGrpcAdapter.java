package platform.booking.service.infrastructure.integration.merchantplatform;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import platform.booking.service.infrastructure.persistence.utils.DateTimeUtils;
import platform.core.common.service.api.InternalMerchantTicketService;
import platform.core.common.service.common.RequestContext;
import platform.core.common.service.domain.ticket.TicketStatus;
import platform.core.common.service.domain.ticket.model.Ticket;
import platform.core.common.service.infrastructure.kafka.record.BookingAggregate;
import vn.com.go.routex.identity.security.log.SystemLog;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MerchantTicketGrpcAdapter {

    private final InternalMerchantTicketService internalMerchantTicketService;
    private final SystemLog sLog = SystemLog.getLogger(this.getClass());

    public List<Ticket> createTickets(BookingAggregate aggregate, OffsetDateTime paidAt) {
        OffsetDateTime issuedAt = paidAt != null ? paidAt : OffsetDateTime.now();

        RequestContext context = RequestContext.builder()
                .requestId(UUID.randomUUID().toString())
                .requestDateTime(DateTimeUtils.getCurrentRequestDateTime())
                .channel("INTERNAL")
                .build();

        List<Ticket> ticketsToCreate = aggregate.bookingSeats().stream()
                .map(bookingSeat -> Ticket.builder()
                        .bookingId(aggregate.booking().getId())
                        .bookingSeatId(bookingSeat.getId())
                        .vehicleId(aggregate.booking().getVehicleId())
                        .tripId(aggregate.booking().getTripId())
                        .seatNumber(bookingSeat.getSeatNo())
                        .customerName(aggregate.booking().getCustomerName())
                        .customerPhone(aggregate.booking().getCustomerPhone())
                        .customerEmail(aggregate.booking().getCustomerEmail())
                        .price(bookingSeat.getPrice())
                        .status(TicketStatus.ISSUED)
                        .issuedAt(issuedAt)
                        .pickupType(aggregate.booking().getPickupType())
                        .pickupStopId(aggregate.booking().getPickupStopId())
                        .pickupAddress(aggregate.booking().getPickupAddress())
                        .dropoffType(aggregate.booking().getDropoffType())
                        .dropoffStopId(aggregate.booking().getDropoffStopId())
                        .dropoffAddress(aggregate.booking().getDropoffAddress())
                        .build())
                .collect(Collectors.toList());

        sLog.info("[INTERNAL] Sending CreateTickets request");
        try {
            return internalMerchantTicketService.createTickets(ticketsToCreate, context);
        } catch (Exception e) {
            sLog.error("[INTERNAL] Error calling merchant platform to create tickets", e);
            throw new RuntimeException("Giao dịch tạo vé không thành công qua internal call: " + e.getMessage(), e);
        }
    }
}
