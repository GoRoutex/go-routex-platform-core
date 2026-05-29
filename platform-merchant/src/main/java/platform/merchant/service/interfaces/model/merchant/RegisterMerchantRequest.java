package platform.merchant.service.interfaces.model.merchant;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
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
public class RegisterMerchantRequest extends BaseRequest {

    @Valid
    @NotNull
    private RegisterMerchantRequestData data;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class RegisterMerchantRequestData {
        @NotBlank
        @NotNull
        private String code;

        @NotBlank
        @NotNull
        private String name;

        private String taxCode;

        @NotBlank
        @NotNull
        private String phone;

        @NotBlank
        @NotNull
        @Email
        private String email;

        @NotBlank
        @NotNull
        @NotBlank
        private String address;

        @NotBlank
        @NotNull
        private String representativeName;

        @NotNull
        @NotBlank
        private String representativePhone;

        @Email
        @NotNull
        @NotBlank
        private String representativeEmail;
    }

}
