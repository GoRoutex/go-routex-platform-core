package platform.driver.service.interfaces.models.passengers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.core.common.service.api.BaseRequest;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class TripLifecycleRequest extends BaseRequest {

    @Valid
    @NotNull
    private TripLifecycleRequestData data;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class TripLifecycleRequestData {
        private String tripId;
        private String performedBy;
        private String deviceId;
    }
}
