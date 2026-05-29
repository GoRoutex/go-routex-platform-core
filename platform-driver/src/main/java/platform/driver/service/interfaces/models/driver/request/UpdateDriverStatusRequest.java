package platform.driver.service.interfaces.models.driver.request;


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
public class UpdateDriverStatusRequest extends BaseRequest {

    @Valid
    @NotNull
    private UpdateDriverStatusRequestData data;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class UpdateDriverStatusRequestData {
        private String driverId;
        private String status;
        private String operationStatus;
    }
}
