package platform.merchant.service.interfaces.model.internal.booking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import platform.core.common.service.domain.seat.SeatFloor;
import platform.core.common.service.domain.vehicle.VehicleStatus;

import java.math.BigDecimal;
import java.util.List;

public class InternalBookingContextResponses {

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TripBookingContextData {
        private String tripId;
        private String routeId;
        private String merchantId;
        private String vehicleId;
        private BigDecimal ticketPrice;
        private String originName;
        private String destinationName;
        private String routeStatus;
        private String tripStatus;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VehicleSeatBlueprintData {
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
    @Builder
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
