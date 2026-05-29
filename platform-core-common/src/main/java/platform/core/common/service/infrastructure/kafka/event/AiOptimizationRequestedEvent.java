package platform.core.common.service.infrastructure.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiOptimizationRequestedEvent {
    private String jobId;
    private String merchantId;
    private String routeId;
    private List<DemandEntry> demands;
    private List<Integer> operatingHours;
    private double operatingCostPerTrip;
    private int maxTripsAllowed;
    private double minLoadFactor;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DemandEntry {
        private String date;
        private double demand;
    }
}

