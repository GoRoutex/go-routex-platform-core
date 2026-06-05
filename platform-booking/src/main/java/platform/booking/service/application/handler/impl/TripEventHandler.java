package platform.booking.service.application.handler.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import platform.booking.service.application.handler.TripEvent;
import platform.booking.service.domain.vehicle.port.VehicleSeatBlueprintQueryPort;
import platform.core.common.service.api.BaseRequest;
import platform.core.common.service.common.RequestContext;
import platform.core.common.service.domain.seat.SeatStatus;
import platform.core.common.service.domain.seat.model.TripSeat;
import platform.core.common.service.domain.seat.port.TripSeatRepositoryPort;
import platform.core.common.service.domain.vehicle.model.VehicleSeatBlueprint;
import platform.core.common.service.infrastructure.event.DomainEvent;
import platform.core.common.service.infrastructure.kafka.event.TripSeatGeneratedEvent;
import platform.core.common.service.infrastructure.kafka.event.TripSellableEvent;
import platform.core.common.service.infrastructure.redis.models.TripCacheSeat;
import platform.core.common.service.persistence.exception.BusinessException;
import platform.core.common.service.persistence.utils.ExceptionUtils;
import vn.com.go.routex.identity.security.log.SystemLog;

import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static platform.core.common.service.persistence.constant.ErrorConstant.DUPLICATE_ERROR;
import static platform.core.common.service.persistence.constant.ErrorConstant.ROUTE_SEAT_EXIST;

@Component
@RequiredArgsConstructor
public class TripEventHandler implements TripEvent {

    private final VehicleSeatBlueprintQueryPort vehicleSeatBlueprintQueryPort;
    private final TripSeatRepositoryPort tripSeatRepositoryPort;
    private final ApplicationEventPublisher applicationEventPublisher;

    private final SystemLog sLog = SystemLog.getLogger(this.getClass());

    @Override
    @Transactional
    public void generateRouteSeat(DomainEvent event, BaseRequest context, TripSellableEvent payload) {
        RequestContext requestContext = RequestContext.builder()
                .requestId(context.getRequestId())
                .requestDateTime(context.getRequestDateTime())
                .channel(context.getChannel())
                .build();
        VehicleSeatBlueprint blueprint = vehicleSeatBlueprintQueryPort.fetchByVehicleId(payload.vehicleId(), requestContext);

        if (tripSeatRepositoryPort.existsByTripId(payload.tripId())) {
            throw new BusinessException(context.getRequestId(), context.getRequestDateTime(), context.getChannel(),
                    ExceptionUtils.buildResultResponse(DUPLICATE_ERROR, String.format(ROUTE_SEAT_EXIST, payload.tripId())));
        }

        sLog.info("[TRIP-SEAT] Generate seats tripId={} vehicleId={} seatCapacity={} hasFloor={}",
                payload.tripId(), payload.vehicleId(), blueprint.getSeatCapacity(), blueprint.isHasFloor());

        Map<String, VehicleSeatBlueprint.SeatBlueprintItem> blueprintMap = blueprint.getSeats().stream()
                .collect(Collectors.toMap(VehicleSeatBlueprint.SeatBlueprintItem::getId, Function.identity()));

        List<TripSeat> seats = blueprint.getSeats().stream()
                .map(seatBlueprint -> TripSeat.builder()
                        .id(UUID.randomUUID().toString())
                        .tripId(payload.tripId())
                        .seatNo(seatBlueprint.getSeatCode())
                        .status(SeatStatus.AVAILABLE)
                        .seatTemplateId(seatBlueprint.getId())
                        .creator(payload.creator())
                        .createdAt(OffsetDateTime.now())
                        .createdBy(payload.creator())
                        .build())
                .collect(Collectors.toList());

        List<TripSeat> savedSeats = tripSeatRepositoryPort.saveAll(seats);

        List<TripCacheSeat> cacheData = savedSeats.stream()
                .map(seat -> {
                    VehicleSeatBlueprint.SeatBlueprintItem seatBlueprint = blueprintMap.get(seat.getSeatTemplateId());
                    return TripCacheSeat.builder()
                            .tripId(seat.getTripId())
                            .seatId(seat.getId())
                            .seatNo(seat.getSeatNo())
                            .status(seat.getStatus())
                            .floor(seatBlueprint.getFloor())
                            .rowNo(seatBlueprint.getRowNo())
                            .colNo(seatBlueprint.getColumnNo())
                            .build();
                })
                .sorted(Comparator.comparing(TripCacheSeat::getSeatNo))
                .toList();

        sLog.info("[TRIP-CACHE] Trip Seat Cache Data: {}", cacheData);
        applicationEventPublisher.publishEvent(new TripSeatGeneratedEvent(payload.tripId(), cacheData));
    }
}
