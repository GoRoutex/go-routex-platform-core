package platform.driver.service.application.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import platform.core.common.service.domain.ticket.TicketStatus;
import platform.core.common.service.domain.ticket.model.Ticket;
import platform.core.common.service.domain.ticket.port.TicketRepositoryPort;
import platform.core.common.service.domain.trip.TripStatus;
import platform.core.common.service.persistence.exception.BusinessException;
import platform.core.common.service.persistence.utils.ExceptionUtils;
import platform.driver.service.application.dto.passengers.PassengerCheckinCommand;
import platform.driver.service.application.dto.passengers.PassengerCheckinResult;
import platform.driver.service.application.dto.passengers.TripLifecycleCommand;
import platform.driver.service.application.dto.passengers.TripLifecycleResult;
import platform.driver.service.application.services.TripPassengerService;
import platform.merchant.service.domain.trip.model.TripAggregate;
import platform.merchant.service.domain.trip.port.TripAggregateRepositoryPort;

import java.time.OffsetDateTime;
import java.util.List;

import static platform.core.common.service.persistence.constant.ErrorConstant.INVALID_INPUT_ERROR;
import static platform.core.common.service.persistence.constant.ErrorConstant.RECORD_NOT_FOUND;
import static platform.core.common.service.persistence.constant.ErrorConstant.TICKET_NOT_FOUND;
import static platform.core.common.service.persistence.constant.ErrorConstant.TRIP_NOT_FOUND;


@RequiredArgsConstructor
@Service
public class TripPassengerServiceImpl implements TripPassengerService {


    private final TicketRepositoryPort ticketRepositoryPort;
    private final TripAggregateRepositoryPort tripAggregateRepositoryPort;

    @Override
    @Transactional
    public PassengerCheckinResult checkInAction(PassengerCheckinCommand command) {
        Ticket ticket = loadTicket(command);
        if (ticket.getStatus() == TicketStatus.CANCELLED || ticket.getStatus() == TicketStatus.EXPIRED || ticket.getStatus() == TicketStatus.COMPLETED) {
            throw invalidState(command, "Ticket cannot be checked in while status is " + ticket.getStatus());
        }
        if (ticket.getStatus() == TicketStatus.ISSUED) {
            OffsetDateTime now = OffsetDateTime.now();
            ticket.setStatus(TicketStatus.CHECKED_IN);
            ticket.setCheckedInAt(now);
            ticket.setCheckedInBy(command.performedBy());
            ticket.setUpdatedAt(now);
            ticket.setUpdatedBy(command.performedBy());
            ticket = ticketRepositoryPort.save(ticket);
        }
        return toResult(ticket);
    }

    @Override
    @Transactional
    public PassengerCheckinResult boardAction(PassengerCheckinCommand command) {
        Ticket ticket = loadTicket(command);
        if (ticket.getStatus() == TicketStatus.CANCELLED || ticket.getStatus() == TicketStatus.EXPIRED || ticket.getStatus() == TicketStatus.COMPLETED) {
            throw invalidState(command, "Ticket cannot be boarded while status is " + ticket.getStatus());
        }
        OffsetDateTime now = OffsetDateTime.now();
        if (ticket.getStatus() == TicketStatus.ISSUED) {
            ticket.setCheckedInAt(now);
            ticket.setCheckedInBy(command.performedBy());
        }
        if (ticket.getStatus() != TicketStatus.BOARDED) {
            ticket.setStatus(TicketStatus.BOARDED);
            ticket.setBoardedAt(now);
            ticket.setBoardedBy(command.performedBy());
            ticket.setUpdatedAt(now);
            ticket.setUpdatedBy(command.performedBy());
            ticket = ticketRepositoryPort.save(ticket);
        }
        return toResult(ticket);
    }

    @Override
    @Transactional
    public TripLifecycleResult startTrip(TripLifecycleCommand command) {
        TripAggregate trip = loadTrip(command);
        if (trip.getStatus() == TripStatus.CANCELLED || trip.getStatus() == TripStatus.COMPLETED) {
            throw invalidState(command, "Trip cannot be started while status is " + trip.getStatus());
        }

        OffsetDateTime now = OffsetDateTime.now();
        List<Ticket> tickets = ticketRepositoryPort.findAllByTripId(command.tripId());
        int expiredTickets = 0;
        int unchangedTickets = 0;
        for (Ticket ticket : tickets) {
            if (ticket.getStatus() == TicketStatus.ISSUED || ticket.getStatus() == TicketStatus.CHECKED_IN) {
                ticket.setStatus(TicketStatus.EXPIRED);
                ticket.setUpdatedAt(now);
                ticket.setUpdatedBy(command.performedBy());
                expiredTickets++;
            } else {
                unchangedTickets++;
            }
        }

        trip.setStatus(TripStatus.DEPARTED);
        trip.setUpdatedAt(now);
        trip.setUpdatedBy(command.performedBy());
        tripAggregateRepositoryPort.save(trip);
        ticketRepositoryPort.saveAll(tickets);

        return TripLifecycleResult.builder()
                .tripId(command.tripId())
                .tripStatus(trip.getStatus())
                .totalTickets(tickets.size())
                .boardedTickets((int) tickets.stream().filter(t -> t.getStatus() == TicketStatus.BOARDED).count())
                .completedTickets((int) tickets.stream().filter(t -> t.getStatus() == TicketStatus.COMPLETED).count())
                .expiredTickets(expiredTickets)
                .unchangedTickets(unchangedTickets)
                .build();
    }

    @Override
    @Transactional
    public TripLifecycleResult completeTrip(TripLifecycleCommand command) {
        TripAggregate trip = loadTrip(command);
        if (trip.getStatus() == TripStatus.CANCELLED) {
            throw invalidState(command, "Trip cannot be completed while status is " + trip.getStatus());
        }

        OffsetDateTime now = OffsetDateTime.now();
        List<Ticket> tickets = ticketRepositoryPort.findAllByTripId(command.tripId());
        int completedTickets = 0;
        int expiredTickets = 0;
        int unchangedTickets = 0;
        for (Ticket ticket : tickets) {
            if (ticket.getStatus() == TicketStatus.BOARDED) {
                ticket.setStatus(TicketStatus.COMPLETED);
                ticket.setUpdatedAt(now);
                ticket.setUpdatedBy(command.performedBy());
                completedTickets++;
            } else if (ticket.getStatus() == TicketStatus.ISSUED || ticket.getStatus() == TicketStatus.CHECKED_IN) {
                ticket.setStatus(TicketStatus.EXPIRED);
                ticket.setUpdatedAt(now);
                ticket.setUpdatedBy(command.performedBy());
                expiredTickets++;
            } else {
                unchangedTickets++;
            }
        }

        trip.setStatus(TripStatus.COMPLETED);
        trip.setUpdatedAt(now);
        trip.setUpdatedBy(command.performedBy());
        tripAggregateRepositoryPort.save(trip);
        ticketRepositoryPort.saveAll(tickets);

        return TripLifecycleResult.builder()
                .tripId(command.tripId())
                .tripStatus(trip.getStatus())
                .totalTickets(tickets.size())
                .boardedTickets((int) tickets.stream().filter(t -> t.getStatus() == TicketStatus.BOARDED).count())
                .completedTickets(completedTickets)
                .expiredTickets(expiredTickets)
                .unchangedTickets(unchangedTickets)
                .build();
    }

    private Ticket loadTicket(PassengerCheckinCommand command) {
        return ticketRepositoryPort.findById(command.ticketId())
                .orElseThrow(() -> new BusinessException(command.context().requestId(), command.context().requestDateTime(), command.context().channel(),
                        ExceptionUtils.buildResultResponse(RECORD_NOT_FOUND, String.format(TICKET_NOT_FOUND, command.ticketId()))));
    }

    private TripAggregate loadTrip(TripLifecycleCommand command) {
        return tripAggregateRepositoryPort.findById(command.tripId())
                .orElseThrow(() -> new BusinessException(command.context().requestId(), command.context().requestDateTime(), command.context().channel(),
                        ExceptionUtils.buildResultResponse(RECORD_NOT_FOUND, String.format(TRIP_NOT_FOUND, command.tripId()))));
    }

    private BusinessException invalidState(PassengerCheckinCommand command, String message) {
        return new BusinessException(command.context().requestId(), command.context().requestDateTime(), command.context().channel(),
                ExceptionUtils.buildResultResponse(INVALID_INPUT_ERROR, message));
    }

    private BusinessException invalidState(TripLifecycleCommand command, String message) {
        return new BusinessException(command.context().requestId(), command.context().requestDateTime(), command.context().channel(),
                ExceptionUtils.buildResultResponse(INVALID_INPUT_ERROR, message));
    }

    private PassengerCheckinResult toResult(Ticket ticket) {
        return PassengerCheckinResult.builder()
                .ticketCode(ticket.getTicketCode())
                .customerName(ticket.getCustomerName())
                .seatNumber(ticket.getSeatNumber())
                .tripId(ticket.getTripId())
                .status(ticket.getStatus())
                .checkedInAt(ticket.getCheckedInAt())
                .boardedAt(ticket.getBoardedAt())
                .build();
    }
}
