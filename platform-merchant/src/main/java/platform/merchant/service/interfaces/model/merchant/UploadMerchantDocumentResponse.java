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
public class UploadMerchantDocumentResponse extends BaseResponse<UploadMerchantDocumentResponse.UploadMerchantDocumentResponseData> {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class UploadMerchantDocumentResponseData {
        private String id;
        private String merchantId;
        private String documentType;
        private String fileUrl;
        private String fileName;
        private String verifiedStatus;
        private OffsetDateTime createdAt;
    }
}
