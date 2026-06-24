package platform.merchant.service.application.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import platform.core.common.service.api.InternalMerchantTicketService;
import platform.core.common.service.common.RequestContext;
import platform.core.common.service.domain.ticket.model.Ticket;
import platform.core.common.service.persistence.exception.BusinessException;
import platform.core.common.service.persistence.utils.ExceptionUtils;
import platform.merchant.service.application.command.ticket.CreateTicketCommand;
import platform.merchant.service.application.command.ticket.CreateTicketResult;
import platform.merchant.service.application.service.TicketService;
import platform.merchant.service.domain.trip.port.TripAggregateRepositoryPort;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import static platform.core.common.service.persistence.constant.ErrorConstant.RECORD_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class InternalMerchantTicketServiceImpl implements InternalMerchantTicketService {

    private final TicketService ticketService;
    private final TripAggregateRepositoryPort tripAggregateRepositoryPort;

    @Override
    @Transactional
    public List<Ticket> createTickets(List<Ticket> ticketsToCreate, RequestContext context) {
        if (ticketsToCreate == null || ticketsToCreate.isEmpty()) {
            return List.of();
        }

        List<CreateTicketCommand> commands = ticketsToCreate.stream()
                .map(ticket -> toCreateTicketCommand(ticket, context))
                .toList();

        List<CreateTicketResult> results = ticketService.createTickets(commands);

        for (int i = 0; i < ticketsToCreate.size(); i++) {
            Ticket ticket = ticketsToCreate.get(i);
            CreateTicketResult result = results.get(i);
            ticket.setId(result.ticketId());
            ticket.setTicketCode(result.ticketCode());
            ticket.setStatus(result.status());
            if (ticket.getIssuedAt() == null) {
                ticket.setIssuedAt(OffsetDateTime.now());
            }
        }

        return ticketsToCreate;
    }

    private CreateTicketCommand toCreateTicketCommand(Ticket ticket, RequestContext context) {
        String merchantId = resolveMerchantId(ticket);

        return CreateTicketCommand.builder()
                .context(context)
                .bookingId(ticket.getBookingId())
                .bookingSeatId(ticket.getBookingSeatId())
                .merchantId(merchantId)
                .tripId(ticket.getTripId())
                .vehicleId(ticket.getVehicleId())
                .seatNumber(ticket.getSeatNumber())
                .customerId(ticket.getCustomerId())
                .customerName(ticket.getCustomerName())
                .customerPhone(ticket.getCustomerPhone())
                .customerEmail(ticket.getCustomerEmail())
                .price(ticket.getPrice() == null ? BigDecimal.ZERO : ticket.getPrice())
                .issuedAt(ticket.getIssuedAt())
                .creator(ticket.getCreatedBy())
                .pickupType(ticket.getPickupType())
                .pickupStopId(ticket.getPickupStopId())
                .pickupAddress(ticket.getPickupAddress())
                .dropOffType(ticket.getDropOffType())
                .dropOffStopId(ticket.getDropOffStopId())
                .dropOffAddress(ticket.getDropOffAddress())
                .build();
    }

    private String resolveMerchantId(Ticket ticket) {
        if (ticket.getMerchantId() != null && !ticket.getMerchantId().isBlank()) {
            return ticket.getMerchantId();
        }

        return tripAggregateRepositoryPort.findById(ticket.getTripId())
                .map(trip -> {
                    ticket.setMerchantId(trip.getMerchantId());
                    return trip.getMerchantId();
                })
                .orElseThrow(() -> new BusinessException(ExceptionUtils.buildResultResponse(
                        RECORD_NOT_FOUND,
                        "Trip not found: " + ticket.getTripId()
                )));
    }
}
