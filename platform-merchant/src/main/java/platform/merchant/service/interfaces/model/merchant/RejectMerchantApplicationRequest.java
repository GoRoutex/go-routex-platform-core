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
public class RejectMerchantApplicationRequest extends BaseRequest {

    @Valid
    @NotNull
    private RejectMerchantApplicationRequestData data;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class RejectMerchantApplicationRequestData {
        @NotBlank
        private String applicationFormId;

        @NotBlank
        private String rejectedBy;

        @NotBlank
        private String rejectionReason;
    }
}
