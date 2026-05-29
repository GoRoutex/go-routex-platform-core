package platform.merchant.service.interfaces.model.merchant;

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
public class AcceptMerchantApplicationRequest extends BaseRequest {

    @Valid
    @NotNull
    private AcceptMerchantApplicationRequestData data;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class AcceptMerchantApplicationRequestData {
        @NotBlank
        private String applicationFormId;

        private String commission;

        @NotBlank
        private String approvedBy;
    }
}
