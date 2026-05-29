package platform.booking.service.interfaces.models.ticket;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
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
public class FetchTicketsRequest extends BaseRequest {

    @Valid
    @NotNull
    private FetchTicketsRequestData data;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class FetchTicketsRequestData {
        @NotBlank
        private String customerId;

        @NotBlank
        private String pageNumber;

        @NotBlank
        private String pageSize;
    }
}
