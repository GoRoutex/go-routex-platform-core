package platform.merchant.service.application.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import platform.core.common.service.api.InternalBookingContextService;
import platform.core.common.service.api.TripBookingContextResponse;
import platform.core.common.service.api.VehicleSeatBlueprintResponse;
import platform.core.common.service.common.RequestContext;
import platform.merchant.service.interfaces.model.internal.booking.InternalBookingContextResponses;

@Service
@RequiredArgsConstructor
public class CommonInternalBookingContextServiceAdapter implements InternalBookingContextService {

    private final InternalBookingContextServiceImpl internalBookingContextService;

    @Override
    public TripBookingContextResponse getTripBookingContext(String tripId, RequestContext context) {
        InternalBookingContextResponses.TripBookingContextData data =
                internalBookingContextService.fetchTripBookingContext(tripId, context);
        return TripBookingContextResponse.builder()
                .tripId(data.getTripId())
                .routeId(data.getRouteId())
                .merchantId(data.getMerchantId())
                .vehicleId(data.getVehicleId())
                .status(data.getTripStatus())
                .basePrice(data.getTicketPrice())
                .build();
    }

    @Override
    public VehicleSeatBlueprintResponse getVehicleSeatBlueprint(String vehicleId, RequestContext context) {
        InternalBookingContextResponses.VehicleSeatBlueprintData data =
                internalBookingContextService.fetchVehicleSeatBlueprint(vehicleId, context);
        return VehicleSeatBlueprintResponse.builder()
                .blueprintId(data.getTemplateId())
                .merchantId(data.getMerchantId())
                .vehicleType(data.getVehicleStatus() == null ? null : data.getVehicleStatus().name())
                .totalSeats(data.getSeatCapacity() == null ? null : data.getSeatCapacity().intValue())
                .numberOfFloors(data.isHasFloor() ? 2 : 1)
                .seatConfigs(data.getSeats().stream()
                        .map(seat -> VehicleSeatBlueprintResponse.SeatConfig.builder()
                                .seatNo(seat.getSeatCode())
                                .floor(seat.getFloor() == null ? null : seat.getFloor().ordinal() + 1)
                                .x(seat.getColumnNo())
                                .y(seat.getRowNo())
                                .build())
                        .toList())
                .build();
    }
}
