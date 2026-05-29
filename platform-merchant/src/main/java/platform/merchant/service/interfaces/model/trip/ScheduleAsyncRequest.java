package platform.merchant.service.interfaces.model.trip;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.core.common.service.api.BaseRequest;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class ScheduleAsyncRequest extends BaseRequest {

    @Valid
    @NotNull
    private String userEmail;

    @Valid
    @NotNull
    private ScheduleAsyncRequestData data;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class ScheduleAsyncRequestData {

        @NotBlank
        private String routeId;

        @NotEmpty
        private List<@Valid DemandEntry> demands;

        @NotEmpty
        private List<Integer> operatingHours;

        @NotNull
        private Double operatingCostPerTrip;

        @NotNull
        private Integer maxTripsAllowed;

        @NotNull
        private Double minLoadFactor;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class DemandEntry {
        @NotBlank
        private String date;
        @NotNull
        private Double demand;
    }
}
