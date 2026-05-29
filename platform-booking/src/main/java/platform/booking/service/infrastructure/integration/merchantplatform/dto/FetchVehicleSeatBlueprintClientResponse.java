package platform.booking.service.infrastructure.integration.merchantplatform.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.core.common.service.domain.seat.SeatFloor;
import platform.core.common.service.domain.vehicle.VehicleStatus;
import platform.core.common.service.api.BaseResponse;

import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class FetchVehicleSeatBlueprintClientResponse
        extends BaseResponse<FetchVehicleSeatBlueprintClientResponse.FetchVehicleSeatBlueprintClientResponseData> {

    @Getter
    @Setter
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FetchVehicleSeatBlueprintClientResponseData {
        private String vehicleId;
        private String merchantId;
        private String templateId;
        private Long seatCapacity;
        private boolean hasFloor;
        private VehicleStatus vehicleStatus;
        private List<SeatBlueprintItem> seats;
    }

    @Getter
    @Setter
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SeatBlueprintItem {
        private String id;
        private String seatCode;
        private SeatFloor floor;
        private int rowNo;
        private int columnNo;
    }
}
