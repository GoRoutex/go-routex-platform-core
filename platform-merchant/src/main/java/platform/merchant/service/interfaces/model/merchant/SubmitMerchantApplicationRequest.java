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
public class SubmitMerchantApplicationRequest extends BaseRequest {

    @Valid
    @NotNull
    private SubmitMerchantApplicationRequestData data;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class SubmitMerchantApplicationRequestData {
        @NotBlank
        private String displayName;
        @NotBlank
        private String legalName;
        @NotBlank
        private String taxCode;
        @NotBlank
        private String businessLicense;
        private String businessLicenseUrl;
        private String logoUrl;
        private String description;
        private String slug;

        @Valid
        @NotNull
        private AddressInfo addressInfo;
        @Valid
        @NotNull
        private Contact contact;

        @Valid
        @NotNull
        private BankInfo bankInfo;

        @Valid
        @NotNull
        private OwnerInfo ownerInfo;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class AddressInfo {
        @NotBlank
        @NotNull
        private String address;
        @NotBlank
        @NotNull
        private String country;
        @NotBlank
        @NotNull
        private String province;
        @NotBlank
        @NotNull
        private String ward;
        @NotBlank
        @NotNull
        private String city;
        private String postalCode;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class Contact {
        @NotBlank
        private String contactName;

        @NotBlank
        private String contactPhone;

        @Email
        @NotBlank
        private String contactEmail;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class BankInfo {
        @NotBlank
        private String bankName;

        @NotBlank
        private String bankBranch;

        @NotBlank
        private String bankAccountName;

        @NotBlank
        private String bankAccountNumber;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class OwnerInfo {
        @NotBlank
        private String ownerName;

        @NotBlank
        private String ownerFullName;

        @NotBlank
        private String ownerPhone;

        @Email
        @NotBlank
        private String ownerEmail;
    }
}
