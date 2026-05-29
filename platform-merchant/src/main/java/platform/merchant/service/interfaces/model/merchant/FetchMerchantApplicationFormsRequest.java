package platform.merchant.service.interfaces.model.merchant;

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
public class FetchMerchantApplicationFormsRequest extends BaseRequest {

    @Valid
    @NotNull
    private FetchMerchantApplicationFormsRequestData data;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class FetchMerchantApplicationFormsRequestData {
        private String pageSize;
        private String pageNumber;
    }
}
