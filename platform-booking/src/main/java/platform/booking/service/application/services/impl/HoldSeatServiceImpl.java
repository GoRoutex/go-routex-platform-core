package platform.booking.service.application.services.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import platform.booking.service.application.command.booking.CreateBookingCommand;
import platform.booking.service.application.command.seat.HoldRoundTripSeatCommand;
import platform.booking.service.application.command.seat.HoldRoundTripSeatResult;
import platform.booking.service.application.command.seat.HoldSeatCommand;
import platform.booking.service.application.command.seat.HoldSeatResult;
import platform.booking.service.application.services.BookingService;
import platform.booking.service.application.services.HoldSeatService;
import platform.booking.service.domain.tripcontext.model.TripBookingContext;
import platform.booking.service.domain.tripcontext.port.TripBookingContextQueryPort;
import platform.booking.service.infrastructure.integration.userservice.client.UserServiceInternalContextClient;
import platform.booking.service.infrastructure.integration.userservice.dto.FetchCustomerByUserIdClientResponse;
import platform.core.common.service.common.RequestContext;
import platform.core.common.service.domain.booking.BookingSeatStatus;
import platform.core.common.service.domain.booking.BookingStatus;
import platform.core.common.service.domain.booking.model.Booking;
import platform.core.common.service.domain.booking.model.BookingSeat;
import platform.core.common.service.domain.booking.port.BookingLegRepositoryPort;
import platform.core.common.service.domain.booking.port.BookingRepositoryPort;
import platform.core.common.service.domain.booking.port.BookingSeatRepositoryPort;
import platform.core.common.service.domain.booking.model.BookingLeg;
import platform.core.common.service.domain.seat.SeatStatus;
import platform.core.common.service.domain.seat.model.TripSeat;
import platform.core.common.service.domain.seat.port.TripSeatRepositoryPort;
import platform.core.common.service.infrastructure.redis.models.TripCacheSeat;
import platform.core.common.service.infrastructure.redis.service.TripSeatCacheService;
import platform.core.common.service.infrastructure.redisson.RedisDistributedLocker;
import platform.core.common.service.infrastructure.redisson.RedisDistributedService;
import platform.core.common.service.persistence.exception.BusinessException;
import platform.core.common.service.persistence.utils.ExceptionUtils;
import vn.com.go.routex.identity.security.jwt.JwtAuthenticatedUser;
import vn.com.go.routex.identity.security.log.SystemLog;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static platform.core.common.service.persistence.constant.ErrorConstant.INVALID_DATA_ERROR;
import static platform.core.common.service.persistence.constant.ErrorConstant.INVALID_INPUT_ERROR;
import static platform.core.common.service.persistence.constant.ErrorConstant.INVALID_SEAT_NO;
import static platform.core.common.service.persistence.constant.ErrorConstant.RECORD_NOT_FOUND;
import static platform.core.common.service.persistence.constant.ErrorConstant.SEAT_NOT_AVAILABLE;
import static platform.core.common.service.persistence.constant.ErrorConstant.SEAT_NOT_FOUND;
import static platform.core.common.service.persistence.constant.ErrorConstant.SYSTEM_ERROR;
import static platform.core.common.service.persistence.constant.ErrorConstant.SYSTEM_ERROR_MESSAGE;

@Service
@RequiredArgsConstructor
public class HoldSeatServiceImpl implements HoldSeatService {

    private final TripSeatRepositoryPort tripSeatRepositoryPort;
    private final TripBookingContextQueryPort tripBookingContextQueryPort;
    private final RedisDistributedService redisDistributedService;
    private final TripSeatCacheService tripSeatCacheService;
    private final BookingService bookingService;
    private final BookingRepositoryPort bookingRepositoryPort;
    private final BookingLegRepositoryPort bookingLegRepositoryPort;
    private final BookingSeatRepositoryPort bookingSeatRepositoryPort;
    private final UserServiceInternalContextClient userServiceClient;
    private static final String LOCK_PATTERN = "lock:trip:";
    private final SystemLog sLog = SystemLog.getLogger(this.getClass());

    @Override
    @Transactional
    public HoldSeatResult holdSeat(HoldSeatCommand command) {
        String currentUser = getCurrentUserId();
        String customerId = resolveCustomerId(currentUser);
        String holdToken = UUID.randomUUID().toString();

        sLog.info("[BOOK-SERVICE] Hold Seat Command: {}", command);
        List<String> distinctSeatNos = validateAndNormalizeSeat(command);
        TripBookingContext tripContext = tripBookingContextQueryPort.fetchByTripId(command.tripId(), command.context());

        return executeWithSeatLocks(command.context(), List.of(new SeatLockRequest(command.tripId(), distinctSeatNos)), () -> {
            validateCacheSeats(command.context(), command.tripId(), distinctSeatNos);
            List<TripSeat> tripSeats = getAndValidateRouteSeats(command, distinctSeatNos);
            OffsetDateTime now = OffsetDateTime.now();
            OffsetDateTime holdUntil = now.plusMinutes(5);
            return holdTripSeats(command, tripContext, holdToken, now, holdUntil, tripSeats, currentUser, customerId, true);
        });
    }

    @Override
    @Transactional
    public HoldRoundTripSeatResult holdRoundTripSeat(HoldRoundTripSeatCommand command) {
        sLog.info("[BOOK-SERVICE] Hold Round Trip Seat Command: {}", command);
        validateRoundTripLegs(command);

        String currentUser = getCurrentUserId();
        String customerId = resolveCustomerId(currentUser);

        HoldSeatCommand outboundCommand = toHoldSeatCommand(command, command.outboundTrip());
        HoldSeatCommand returnCommand = toHoldSeatCommand(command, command.returnTrip());
        List<String> outboundSeatNos = validateAndNormalizeSeat(outboundCommand);
        List<String> returnSeatNos = validateAndNormalizeSeat(returnCommand);

        TripBookingContext outboundContext = tripBookingContextQueryPort.fetchByTripId(outboundCommand.tripId(), command.context());
        TripBookingContext returnContext = tripBookingContextQueryPort.fetchByTripId(returnCommand.tripId(), command.context());

        return executeWithSeatLocks(command.context(), List.of(
                new SeatLockRequest(outboundCommand.tripId(), outboundSeatNos),
                new SeatLockRequest(returnCommand.tripId(), returnSeatNos)
        ), () -> {
            validateCacheSeats(command.context(), outboundCommand.tripId(), outboundSeatNos);
            validateCacheSeats(command.context(), returnCommand.tripId(), returnSeatNos);

            List<TripSeat> outboundSeats = getAndValidateRouteSeats(outboundCommand, outboundSeatNos);
            List<TripSeat> returnSeats = getAndValidateRouteSeats(returnCommand, returnSeatNos);
            OffsetDateTime now = OffsetDateTime.now();
            OffsetDateTime holdUntil = now.plusMinutes(5);
            String holdToken = UUID.randomUUID().toString();

            Booking booking = createRoundTripBooking(
                    command,
                    outboundCommand,
                    returnCommand,
                    outboundContext,
                    returnContext,
                    holdToken,
                    now,
                    holdUntil,
                    outboundSeats,
                    returnSeats,
                    currentUser,
                    customerId
            );

            updateSeatCache(outboundCommand.tripId(), outboundSeats);
            updateSeatCache(returnCommand.tripId(), returnSeats);

            return HoldRoundTripSeatResult.builder()
                    .outboundTrip(toHoldSeatResult(booking, outboundSeats, holdToken))
                    .returnTrip(toHoldSeatResult(booking, returnSeats, holdToken))
                    .build();
        });
    }

    private HoldSeatResult holdTripSeats(
            HoldSeatCommand command,
            TripBookingContext tripContext,
            String holdToken,
            OffsetDateTime now,
            OffsetDateTime holdUntil,
            List<TripSeat> tripSeats,
            String currentUser,
            String customerId,
            boolean updateCache
    ) {
        tripSeats.forEach(seat -> seat.setStatus(SeatStatus.HELD));
        tripSeatRepositoryPort.saveAll(tripSeats);
        Booking booking = createBooking(command, tripContext, holdToken, now, holdUntil, tripSeats, currentUser, customerId);

        if (updateCache) {
            updateSeatCache(command.tripId(), tripSeats);
        }

        return HoldSeatResult.builder()
                .booking(HoldSeatResult.HoldSeatBookingResult.builder()
                        .bookingId(booking.getId())
                        .bookingCode(booking.getBookingCode())
                        .holdUntil(booking.getHoldUntil())
                        .seatCount(booking.getSeatCount())
                        .totalAmount(booking.getTotalAmount())
                        .currency(booking.getCurrency())
                        .build())
                .seats(tripSeats.stream()
                        .sorted(Comparator.comparing(TripSeat::getSeatNo))
                        .map(seat -> HoldSeatResult.HoldSeatItemResult.builder()
                                .tripId(seat.getTripId())
                                .seatNo(seat.getSeatNo())
                                .status(seat.getStatus().name())
                                .holdToken(holdToken)
                                .build())
                        .toList())
                .build();
    }

    private void updateSeatCache(String tripId, List<TripSeat> tripSeats) {
        List<TripCacheSeat> updates = tripSeats
                .stream()
                .map(seat -> TripCacheSeat.builder()
                        .tripId(seat.getTripId())
                        .seatNo(seat.getSeatNo())
                        .status(seat.getStatus())
                        .build()).toList();

        tripSeatCacheService.updateSeatsStatus(tripId, updates);
    }

    private Booking createBooking(HoldSeatCommand command, TripBookingContext tripContext, String holdToken, OffsetDateTime heldAt, OffsetDateTime holdUntil, List<TripSeat> tripSeats, String currentUser, String finalCustomerId) {
        return bookingService.createBooking(CreateBookingCommand.builder()
                .context(command.context())
                .merchantId(tripContext.getMerchantId())
                .tripId(command.tripId())
                .holdBy(command.holdBy())
                .holdToken(holdToken)
                .heldAt(heldAt)
                .holdUntil(holdUntil)
                .customerName(command.customerName())
                .customerPhone(command.customerPhone())
                .customerEmail(command.customerEmail())
                .customerId(finalCustomerId)
                .build(), tripContext, tripSeats);
    }

    private Booking createRoundTripBooking(
            HoldRoundTripSeatCommand command,
            HoldSeatCommand outboundCommand,
            HoldSeatCommand returnCommand,
            TripBookingContext outboundContext,
            TripBookingContext returnContext,
            String holdToken,
            OffsetDateTime heldAt,
            OffsetDateTime holdUntil,
            List<TripSeat> outboundSeats,
            List<TripSeat> returnSeats,
            String currentUser,
            String customerId
    ) {
        validateTripContext(command.context(), outboundContext);
        validateTripContext(command.context(), returnContext);

        outboundSeats.forEach(seat -> seat.setStatus(SeatStatus.HELD));
        returnSeats.forEach(seat -> seat.setStatus(SeatStatus.HELD));
        List<TripSeat> allTripSeats = new ArrayList<>();
        allTripSeats.addAll(outboundSeats);
        allTripSeats.addAll(returnSeats);
        tripSeatRepositoryPort.saveAll(allTripSeats);

        BigDecimal outboundAmount = outboundContext.getTicketPrice().multiply(BigDecimal.valueOf(outboundSeats.size()));
        BigDecimal returnAmount = returnContext.getTicketPrice().multiply(BigDecimal.valueOf(returnSeats.size()));

        Booking booking = Booking.builder()
                .id(UUID.randomUUID().toString())
                .bookingCode(bookingRepositoryPort.generateBookingCode())
                .merchantId(outboundContext.getMerchantId())
                .customerId(customerId)
                .customerName(command.customerName())
                .customerPhone(command.customerPhone())
                .customerEmail(command.customerEmail())
                .channel(command.context().channel())
                .seatCount(allTripSeats.size())
                .totalAmount(outboundAmount.add(returnAmount))
                .currency("VND")
                .status(BookingStatus.PENDING_PAYMENT)
                .heldAt(heldAt)
                .holdUntil(holdUntil)
                .creator(command.holdBy())
                .build();

        bookingRepositoryPort.save(booking);

        BookingLeg outboundLeg = BookingLeg.builder()
                .id(UUID.randomUUID().toString())
                .bookingId(booking.getId())
                .tripId(outboundCommand.tripId())
                .vehicleId(outboundContext.getVehicleId())
                .pickupType(outboundCommand.pickupType())
                .pickupStopId(outboundCommand.pickupStopId())
                .pickupAddress(outboundCommand.pickupAddress())
                .dropOffType(outboundCommand.dropOffType())
                .dropOffStopId(outboundCommand.dropOffStopId())
                .dropOffAddress(outboundCommand.dropOffAddress())
                .build();

        BookingLeg returnLeg = BookingLeg.builder()
                .id(UUID.randomUUID().toString())
                .bookingId(booking.getId())
                .tripId(returnCommand.tripId())
                .vehicleId(returnContext.getVehicleId())
                .pickupType(returnCommand.pickupType())
                .pickupStopId(returnCommand.pickupStopId())
                .pickupAddress(returnCommand.pickupAddress())
                .dropOffType(returnCommand.dropOffType())
                .dropOffStopId(returnCommand.dropOffStopId())
                .dropOffAddress(returnCommand.dropOffAddress())
                .build();

        bookingLegRepositoryPort.saveAll(List.of(outboundLeg, returnLeg));

        List<BookingSeat> bookingSeats = new ArrayList<>();
        bookingSeats.addAll(toBookingSeats(booking, outboundLeg.getId(), outboundSeats, outboundContext.getTicketPrice(), currentUser));
        bookingSeats.addAll(toBookingSeats(booking, returnLeg.getId(), returnSeats, returnContext.getTicketPrice(), currentUser));
        bookingSeatRepositoryPort.saveAll(bookingSeats);

        sLog.info("[BOOK-SERVICE] Create Round Trip Draft Booking successfully: {}", booking);
        return booking;
    }

    private void validateTripContext(RequestContext context, TripBookingContext tripContext) {
        if (tripContext == null
                || tripContext.getTicketPrice() == null
                || tripContext.getVehicleId() == null
                || tripContext.getVehicleId().isBlank()) {
            throw new BusinessException(context.requestId(), context.requestDateTime(), context.channel(),
                    ExceptionUtils.buildResultResponse(INVALID_DATA_ERROR, "Trip booking context is missing ticket price or vehicle"));
        }
    }

    private List<BookingSeat> toBookingSeats(
            Booking booking,
            String bookingLegId,
            List<TripSeat> tripSeats,
            BigDecimal price,
            String currentUser
    ) {
        return tripSeats.stream()
                .map(seat -> (BookingSeat) BookingSeat.builder()
                        .id(UUID.randomUUID().toString())
                        .bookingId(booking.getId())
                        .bookingLegId(bookingLegId)
                        .seatNo(seat.getSeatNo())
                        .creator(currentUser)
                        .status(BookingSeatStatus.HELD)
                        .price(price)
                        .build())
                .toList();
    }

    private HoldSeatResult toHoldSeatResult(Booking booking, List<TripSeat> tripSeats, String holdToken) {
        return HoldSeatResult.builder()
                .booking(HoldSeatResult.HoldSeatBookingResult.builder()
                        .bookingId(booking.getId())
                        .bookingCode(booking.getBookingCode())
                        .holdUntil(booking.getHoldUntil())
                        .seatCount(booking.getSeatCount())
                        .totalAmount(booking.getTotalAmount())
                        .currency(booking.getCurrency())
                        .build())
                .seats(tripSeats.stream()
                        .sorted(Comparator.comparing(TripSeat::getSeatNo))
                        .map(seat -> HoldSeatResult.HoldSeatItemResult.builder()
                                .tripId(seat.getTripId())
                                .seatNo(seat.getSeatNo())
                                .status(seat.getStatus().name())
                                .holdToken(holdToken)
                                .build())
                        .toList())
                .build();
    }

    private String resolveCustomerId(String currentUser) {
        if (currentUser == null) {
            return null;
        }
        try {
            FetchCustomerByUserIdClientResponse response = userServiceClient.fetchCustomerByUserId(currentUser);
            if (response != null && response.getData() != null) {
                return response.getData().getId();
            }
        } catch (Exception e) {
            sLog.error("[BOOK-SERVICE] Failed to fetch customer for user {}", currentUser, e);
        }
        return null;
    }

    private void validateCacheSeats(RequestContext context, String tripId, List<String> distinctSeatNos) {
        Map<String, TripCacheSeat> mapCacheSeats = tripSeatCacheService.getSpecificSeat(tripId, distinctSeatNos);
        if (!mapCacheSeats.isEmpty()) {
            for (String seatNo : distinctSeatNos) {
                TripCacheSeat cacheSeat = mapCacheSeats.get(seatNo);
                if (cacheSeat != null && !SeatStatus.AVAILABLE.equals(cacheSeat.getStatus())) {
                    throw new BusinessException(context.requestId(), context.requestDateTime(), context.channel(),
                            ExceptionUtils.buildResultResponse(INVALID_DATA_ERROR, String.format(SEAT_NOT_AVAILABLE, seatNo)));
                }
            }
        }
    }

    private HoldSeatCommand toHoldSeatCommand(
            HoldRoundTripSeatCommand command,
            HoldRoundTripSeatCommand.HoldRoundTripSeatLegCommand legCommand
    ) {
        return HoldSeatCommand.builder()
                .context(command.context())
                .creator(command.creator())
                .tripId(legCommand.tripId())
                .seatNos(legCommand.seatNos())
                .holdBy(command.holdBy())
                .customerName(command.customerName())
                .customerPhone(command.customerPhone())
                .customerEmail(command.customerEmail())
                .pickupType(legCommand.pickupType())
                .pickupStopId(legCommand.pickupStopId())
                .pickupAddress(legCommand.pickupAddress())
                .dropOffType(legCommand.dropOffType())
                .dropOffStopId(legCommand.dropOffStopId())
                .dropOffAddress(legCommand.dropOffAddress())
                .build();
    }

    private void validateRoundTripLegs(HoldRoundTripSeatCommand command) {
        if (command.outboundTrip() == null || command.returnTrip() == null
                || command.outboundTrip().tripId() == null || command.outboundTrip().tripId().isBlank()
                || command.returnTrip().tripId() == null || command.returnTrip().tripId().isBlank()) {
            throw new BusinessException(
                    command.context().requestId(),
                    command.context().requestDateTime(),
                    command.context().channel(),
                    ExceptionUtils.buildResultResponse(INVALID_INPUT_ERROR, "Round trip requires outboundTrip and returnTrip")
            );
        }
        if (command.outboundTrip().tripId().equals(command.returnTrip().tripId())) {
            throw new BusinessException(
                    command.context().requestId(),
                    command.context().requestDateTime(),
                    command.context().channel(),
                    ExceptionUtils.buildResultResponse(INVALID_INPUT_ERROR, "Outbound trip and return trip must be different")
            );
        }
    }

    private List<TripSeat> getAndValidateRouteSeats(HoldSeatCommand command, List<String> distinctSeatNos) {
        List<TripSeat> tripSeats = tripSeatRepositoryPort.findAllByTripIdAndSeatNoInForUpdate(command.tripId(), distinctSeatNos);
        if (tripSeats.size() != distinctSeatNos.size()) {
            throw new BusinessException(
                    command.context().requestId(),
                    command.context().requestDateTime(),
                    command.context().channel(),
                    ExceptionUtils.buildResultResponse(RECORD_NOT_FOUND, SEAT_NOT_FOUND)
            );
        }

        for (TripSeat seat : tripSeats) {
            if (!SeatStatus.AVAILABLE.equals(seat.getStatus())) {
                throw new BusinessException(
                        command.context().requestId(),
                        command.context().requestDateTime(),
                        command.context().channel(),
                        ExceptionUtils.buildResultResponse(INVALID_INPUT_ERROR, String.format(SEAT_NOT_AVAILABLE, seat.getSeatNo()))
                );
            }
        }
        return tripSeats;
    }
    private <T> T executeWithSeatLocks(
            RequestContext context,
            List<SeatLockRequest> seatLockRequests,
            Supplier<T> action
    ) {

        List<String> lockKeys = seatLockRequests
                .stream()
                .flatMap(request -> request.seatNos().stream()
                        .map(seatNo -> LOCK_PATTERN + request.tripId() + ":seat:" + seatNo))
                .distinct()
                .sorted()
                .toList();

        RedisDistributedLocker multiLock = redisDistributedService.getMultiLock(lockKeys);

        try {
            if(!multiLock.tryLock(5, 10, TimeUnit.SECONDS)) {
                throw new BusinessException(context.requestId(), context.requestDateTime(), context.channel(),
                        ExceptionUtils.buildResultResponse(INVALID_INPUT_ERROR, "Specific seat is processed by other user"));
            }

            try {
                return action.get();
            } finally {
                if(multiLock.isHeldByCurrentThread()) {
                    multiLock.unlock();
                }
            }
        } catch(InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException(context.requestId(), context.requestDateTime(), context.channel(),
                    ExceptionUtils.buildResultResponse(SYSTEM_ERROR, SYSTEM_ERROR_MESSAGE));
        }
    }

    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName())) {
            if(authentication.getPrincipal() instanceof JwtAuthenticatedUser principal) {
                return principal.userId();
            }
        }
        return null;
    }
    private List<String> validateAndNormalizeSeat(HoldSeatCommand command) {
        if (command.seatNos() == null || command.seatNos().isEmpty()) {
            throw new BusinessException(
                    command.context().requestId(),
                    command.context().requestDateTime(),
                    command.context().channel(),
                    ExceptionUtils.buildResultResponse(INVALID_INPUT_ERROR, INVALID_SEAT_NO)
            );
        }

        List<String> distinctSeatNos = command.seatNos().stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .map(String::toUpperCase)
                .filter(seatNo -> !seatNo.isEmpty())
                .distinct()
                .sorted()
                .toList();

        if (distinctSeatNos.isEmpty()) {
            throw new BusinessException(
                    command.context().requestId(),
                    command.context().requestDateTime(),
                    command.context().channel(),
                    ExceptionUtils.buildResultResponse(INVALID_INPUT_ERROR, INVALID_SEAT_NO)
            );
        }

        return distinctSeatNos;
    }

    private record SeatLockRequest(String tripId, List<String> seatNos) {
    }
}
