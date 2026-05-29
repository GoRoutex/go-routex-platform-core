package platform.merchant.service.interfaces.model.assignment;

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
public class AssignRouteBatchRequest extends BaseRequest {

    @Valid
    @NotNull
    private AssignRouteBatchRequestData data;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class AssignRouteBatchRequestData {

        @NotNull
        @NotBlank
        private String creator;

        @NotNull
        @NotEmpty
        private List<@Valid AssignmentItem> assignments;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class AssignmentItem {

        @NotNull
        @NotBlank
        private String tripId;

        @NotNull
        @NotBlank
        private String vehicleId;

        @NotNull
        @NotBlank
        private String driverId;
    }
}
