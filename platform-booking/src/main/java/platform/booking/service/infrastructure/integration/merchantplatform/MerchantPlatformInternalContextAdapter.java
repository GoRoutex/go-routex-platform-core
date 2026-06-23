package platform.booking.service.infrastructure.integration.merchantplatform;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import platform.booking.service.domain.tripcontext.model.TripBookingContext;
import platform.booking.service.domain.tripcontext.port.TripBookingContextQueryPort;
import platform.booking.service.domain.vehicle.port.VehicleSeatBlueprintQueryPort;
import platform.core.common.service.api.InternalBookingContextService;
import platform.core.common.service.api.TripBookingContextResponse;
import platform.core.common.service.api.VehicleSeatBlueprintResponse;
import platform.core.common.service.common.RequestContext;
import platform.core.common.service.domain.seat.SeatFloor;
import platform.core.common.service.domain.vehicle.model.VehicleSeatBlueprint;
import platform.core.common.service.persistence.constant.ErrorConstant;
import platform.core.common.service.persistence.exception.BusinessException;
import platform.core.common.service.persistence.utils.ExceptionUtils;
import vn.com.go.routex.identity.security.log.SystemLog;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class MerchantPlatformInternalContextAdapter implements TripBookingContextQueryPort, VehicleSeatBlueprintQueryPort {

    private final InternalBookingContextService internalBookingContextService;
    private final SystemLog sLog = SystemLog.getLogger(this.getClass());

    @Override
    public TripBookingContext fetchByTripId(String tripId, RequestContext context) {
        TripBookingContextResponse response = internalBookingContextService.getTripBookingContext(tripId, context);
        if (response == null) {
            throw new BusinessException(
                    context != null ? context.requestId() : null,
                    context != null ? context.requestDateTime() : null,
                    context != null ? context.channel() : null,
                    ExceptionUtils.buildResultResponse(ErrorConstant.RECORD_NOT_FOUND, "Trip booking context not found for " + tripId)
            );
        }
        
        return TripBookingContext.builder()
                .tripId(response.tripId())
                .routeId(response.routeId())
                .merchantId(response.merchantId())
                .vehicleId(response.vehicleId())
                .ticketPrice(response.basePrice() == null ? BigDecimal.ZERO : response.basePrice())
                .originName(null) // Not available in DTO
                .destinationName(null) // Not available in DTO
                .routeStatus(null) // Not available in DTO
                .tripStatus(response.status())
                .build();
    }

    @Override
    public VehicleSeatBlueprint fetchByVehicleId(String vehicleId, RequestContext context) {
        VehicleSeatBlueprintResponse response = internalBookingContextService.getVehicleSeatBlueprint(vehicleId, context);

        sLog.info("Seat Blueprint Response: {}", response);

        if (response == null) {
            throw new BusinessException(
                    context != null ? context.requestId() : null,
                    context != null ? context.requestDateTime() : null,
                    context != null ? context.channel() : null,
                    ExceptionUtils.buildResultResponse(ErrorConstant.RECORD_NOT_FOUND, "Vehicle seat blueprint not found for " + vehicleId)
            );
        }
        
        return VehicleSeatBlueprint.builder()
                .vehicleId(vehicleId)
                .merchantId(response.merchantId())
                .templateId(response.blueprintId())
                .seatCapacity(response.totalSeats() != null ? response.totalSeats().longValue() : null)
                .hasFloor(response.numberOfFloors() != null && response.numberOfFloors() > 1)
                .vehicleStatus(null) // Not available in DTO
                .seats(response.seatConfigs().stream()
                        .map(seat -> VehicleSeatBlueprint.SeatBlueprintItem.builder()
                                .id(seat.id() == null || seat.id().isBlank() ? seat.seatNo() : seat.id())
                                .seatCode(seat.seatNo())
                                .floor(seat.floor() == null ? null : (seat.floor() == 1 ? SeatFloor.DOWN : SeatFloor.UP))
                                .rowNo(seat.y())
                                .columnNo(seat.x())
                                .build())
                        .toList())
                .build();
    }
}
