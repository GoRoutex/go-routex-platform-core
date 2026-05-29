package platform.merchant.service.interfaces.model.provinces;

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
public class CreateProvinceRequest extends BaseRequest {

    @Valid
    @NotNull
    private CreateProvinceRequestData data;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class CreateProvinceRequestData {
        @NotNull
        @NotBlank
        private String name;

        @NotNull
        @NotBlank
        private String code;
    }
}

