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
public class GetMerchantDetailResponse extends BaseResponse<GetMerchantDetailResponse.GetMerchantDetailResponseData>
{

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class GetMerchantDetailResponseData {
        private String id;
        private String code;
        private String name;
        private String taxCode;
        private String phone;
        private String email;
        private String address;
        private String representativeName;
        private String representativePhone;
        private String representativeEmail;
        private String status;
        private String rejectionReason;
        private OffsetDateTime approvedAt;
        private OffsetDateTime createdAt;
        private OffsetDateTime updatedAt;
        private List<UploadMerchantDocumentResponse> documents;
    }
}
