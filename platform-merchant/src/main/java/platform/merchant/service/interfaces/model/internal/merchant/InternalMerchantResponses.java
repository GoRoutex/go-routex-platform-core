package platform.merchant.service.interfaces.model.internal.merchant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.merchant.service.domain.merchant.ApplicationFormStatus;
import platform.merchant.service.domain.merchant.MerchantStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public final class InternalMerchantResponses {

    private InternalMerchantResponses() {
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class MerchantData {
        private String id;
        private String code;
        private String slug;
        private String displayName;
        private String legalName;
        private String taxCode;
        private String businessLicenseNumber;
        private String businessLicenseUrl;
        private String phone;
        private String email;
        private String logoUrl;
        private String description;
        private String address;
        private String ward;
        private String province;
        private String country;
        private String postalCode;
        private String representativeName;
        private String contactName;
        private String contactPhone;
        private String contactEmail;
        private String ownerFullName;
        private String ownerPhone;
        private String ownerEmail;
        private String bankAccountName;
        private String bankAccountNumber;
        private String bankName;
        private String bankBranch;
        private BigDecimal commissionRate;
        private MerchantStatus status;
        private OffsetDateTime approvedAt;
        private String approvedBy;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class MerchantPage {
        private List<MerchantData> items;
        private Pagination pagination;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class MerchantApplicationFormData {
        private String id;
        private String formCode;
        private String displayName;
        private String legalName;
        private String taxCode;
        private String businessLicense;
        private String businessLicenseUrl;
        private String logoUrl;
        private String description;
        private String slug;
        private String submittedBy;
        private OffsetDateTime submittedAt;
        private String approvedBy;
        private OffsetDateTime approvedAt;
        private String rejectedBy;
        private String rejectionReason;
        private String country;
        private String province;
        private String ward;
        private String address;
        private String postalCode;
        private String contactName;
        private String contactPhone;
        private String contactEmail;
        private String ownerName;
        private String ownerFullName;
        private String ownerPhone;
        private String ownerEmail;
        private String bankAccountName;
        private String bankAccountNumber;
        private String bankName;
        private String bankBranch;
        private ApplicationFormStatus status;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class MerchantApplicationFormPage {
        private List<MerchantApplicationFormData> items;
        private Pagination pagination;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class Pagination {
        private int pageNumber;
        private int pageSize;
        private long totalElements;
        private int totalPages;
    }
}
