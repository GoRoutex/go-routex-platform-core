package platform.core.common.service.api;

import lombok.Builder;

import java.util.List;

@Builder
public record VehicleSeatBlueprintResponse(
        String blueprintId,
        String merchantId,
        String name,
        String vehicleType,
        Integer totalSeats,
        Integer numberOfFloors,
        List<SeatConfig> seatConfigs
) {
    @Builder
    public record SeatConfig(
            String seatNo,
            Integer floor,
            Integer x,
            Integer y,
            String type,
            String status
    ) {}
}
