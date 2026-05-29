package platform.merchant.service.interfaces.model.merchant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.core.common.service.api.BaseResponse;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class FetchMerchantApplicationFormsResponse extends BaseResponse<FetchMerchantApplicationFormsResponse.FetchMerchantApplicationFormsResponseData> {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class FetchMerchantApplicationFormsResponseData {
        private List<FetchMerchantApplicationFormItemResponse> items;
        private int pageNumber;
        private int pageSize;
        private long totalElements;
        private int totalPages;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class FetchMerchantApplicationFormItemResponse {
        private String applicationId;
        private String formCode;
        private String displayName;
        private String legalName;
        private String taxCode;
        private String status;
        private String merchantId;
        private String submittedBy;
        private OffsetDateTime submittedAt;
        private String approvedBy;
        private OffsetDateTime approvedAt;
        private String rejectedBy;
        private String rejectionReason;
    }
}
