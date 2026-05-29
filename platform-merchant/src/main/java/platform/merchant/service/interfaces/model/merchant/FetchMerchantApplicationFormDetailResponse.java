package platform.merchant.service.interfaces.model.merchant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.core.common.service.api.BaseResponse;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class FetchMerchantApplicationFormDetailResponse extends BaseResponse<FetchMerchantApplicationFormDetailResponse.FetchMerchantApplicationFormDetailResponseData> {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class FetchMerchantApplicationFormDetailResponseData {
        private String applicationId;
        private String formCode;
        private String displayName;
        private String legalName;
        private String taxCode;
        private String businessLicense;
        private String businessLicenseUrl;
        private String country;
        private String province;
        private String city;
        private String postalCode;
        private String description;
        private String slug;
        private String merchantId;
        private String merchantName;
        private String status;
        private String submittedBy;
        private OffsetDateTime submittedAt;
        private String approvedBy;
        private OffsetDateTime approvedAt;
        private String rejectedBy;
        private String rejectionReason;
        private Contact contact;
        private BankInfo bankInfo;
        private OwnerInfo ownerInfo;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class Contact {
        private String contactName;
        private String contactPhone;
        private String contactEmail;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class BankInfo {
        private String bankName;
        private String bankBranch;
        private String bankAccountName;
        private String bankAccountNumber;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class OwnerInfo {
        private String ownerName;
        private String ownerFullName;
        private String ownerPhone;
        private String ownerEmail;
    }
}
