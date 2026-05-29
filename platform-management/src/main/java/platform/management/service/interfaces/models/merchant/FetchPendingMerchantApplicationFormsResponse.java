package platform.management.service.interfaces.models.merchant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.merchant.service.domain.merchant.ApplicationFormStatus;
import platform.core.common.service.api.BaseResponse;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class FetchPendingMerchantApplicationFormsResponse extends BaseResponse<FetchPendingMerchantApplicationFormsResponse.FetchPendingMerchantApplicationFormsPage> {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class FetchPendingMerchantApplicationFormsPage {
        private List<PendingMerchantApplicationFormData> items;
        private Pagination pagination;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class PendingMerchantApplicationFormData {
        private String id;
        private String formCode;
        private String displayName;
        private String legalName;
        private String taxCode;
        private String businessLicense;
        private String businessLicenseUrl;
        private String logoUrl;
        private String country;
        private String province;
        private String ward;
        private String address;
        private String postalCode;
        private String description;
        private String slug;
        private String submittedBy;
        private OffsetDateTime submittedAt;
        private ApplicationFormStatus status;
        private String contactName;
        private String contactPhone;
        private String contactEmail;
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
