package platform.booking.service.application.services.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import vn.com.go.routex.identity.security.jwt.JwtAuthenticatedUser;
import vn.com.go.routex.identity.security.log.SystemLog;
import platform.booking.service.application.command.booking.CreateBookingCommand;
import platform.booking.service.application.command.seat.HoldSeatCommand;
import platform.booking.service.application.command.seat.HoldSeatResult;
import platform.booking.service.application.services.BookingService;
import platform.booking.service.application.services.HoldSeatService;
import platform.core.common.service.domain.booking.model.Booking;
import platform.core.common.service.domain.seat.SeatStatus;
import platform.core.common.service.domain.seat.model.TripSeat;
import platform.core.common.service.domain.seat.port.TripSeatRepositoryPort;
import platform.booking.service.domain.tripcontext.model.TripBookingContext;
import platform.booking.service.domain.tripcontext.port.TripBookingContextQueryPort;
import platform.core.common.service.infrastructure.redis.models.TripCacheSeat;
import platform.core.common.service.infrastructure.redis.service.TripSeatCacheService;
import platform.core.common.service.infrastructure.redisson.RedisDistributedLocker;
import platform.core.common.service.infrastructure.redisson.RedisDistributedService;
import platform.booking.service.infrastructure.integration.userservice.client.UserServiceInternalContextClient;
import platform.booking.service.infrastructure.integration.userservice.dto.FetchCustomerByUserIdClientResponse;
import platform.core.common.service.persistence.exception.BusinessException;
import platform.core.common.service.persistence.utils.ExceptionUtils;

import java.time.OffsetDateTime;
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
    private final UserServiceInternalContextClient userServiceClient;
    private static final String LOCK_PATTERN = "lock:trip:";
    private final SystemLog sLog = SystemLog.getLogger(this.getClass());

    @Override
    @Transactional
    public HoldSeatResult holdSeat(HoldSeatCommand command) {

        String currentUser = getCurrentUserId();
        String customerId = null;
        if (currentUser != null) {
            try {
                FetchCustomerByUserIdClientResponse response = userServiceClient.fetchCustomerByUserId(currentUser);
                if (response != null && response.getData() != null) {
                    customerId = response.getData().getId();
                }
            } catch (Exception e) {
                sLog.error("[BOOK-SERVICE] Failed to fetch customer for user {}", currentUser, e);
            }
        }
        final String finalCustomerId = customerId;

        String holdToken = UUID.randomUUID().toString();
        sLog.info("[BOOK-SERVICE] Hold Seat Command: {}", command);
        List<String> distinctSeatNos = validateAndNormalizeSeat(command);
        TripBookingContext tripContext = tripBookingContextQueryPort.fetchByTripId(command.tripId(), command.context());
        return executeWithSeatLocks(command, distinctSeatNos, () -> {
            Map<String, TripCacheSeat> mapCacheSeats = tripSeatCacheService.getSpecificSeat(command.tripId(), distinctSeatNos);
            if(!mapCacheSeats.isEmpty()) {
                for(String seatNo : distinctSeatNos) {
                    TripCacheSeat cacheSeat = mapCacheSeats.get(seatNo);
                    if(cacheSeat != null && !SeatStatus.AVAILABLE.equals(cacheSeat.getStatus())) {
                        throw new BusinessException(command.context().requestId(), command.context().requestDateTime(), command.context().channel(),
                                ExceptionUtils.buildResultResponse(INVALID_DATA_ERROR, String.format(SEAT_NOT_AVAILABLE, seatNo)));
                    }
                }
            }

            List<TripSeat> tripSeats = getAndValidateRouteSeats(command, distinctSeatNos);
            OffsetDateTime now = OffsetDateTime.now();
            OffsetDateTime holdUntil = now.plusMinutes(5);
            tripSeats.forEach(seat -> seat.setStatus(SeatStatus.HELD));
            tripSeatRepositoryPort.saveAll(tripSeats);
            updateSeatCache(command.tripId(), tripSeats);
            Booking booking = createBooking(command, tripContext, holdToken, now, holdUntil, tripSeats, currentUser, finalCustomerId);

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
        });
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
                .pickupType(command.pickupType())
                .pickupStopId(command.pickupStopId())
                .pickupAddress(command.pickupAddress())
                .dropoffType(command.dropoffType())
                .dropoffStopId(command.dropoffStopId())
                .dropoffAddress(command.dropoffAddress())
                .build(), tripContext, tripSeats);
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
    private HoldSeatResult executeWithSeatLocks(
            HoldSeatCommand command,
            List<String> seatNos,
            Supplier<HoldSeatResult> action
    ) {

        List<String> lockKeys = seatNos
                .stream()
                .map(seatNo -> LOCK_PATTERN + command.tripId() + ":seat:" + seatNo)
                .toList();

        RedisDistributedLocker multiLock = redisDistributedService.getMultiLock(lockKeys);

        try {
            if(!multiLock.tryLock(5, 10, TimeUnit.SECONDS)) {
                throw new BusinessException(command.context().requestId(), command.context().requestDateTime(), command.context().channel(),
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
            throw new BusinessException(command.context().requestId(), command.context().requestDateTime(), command.context().channel(),
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
}
