package platform.management.service.interfaces.models.media;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.core.common.service.api.BaseResponse;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class UploadMediaResponse extends BaseResponse<UploadMediaResponse.UploadMediaResponseData> {

    @Getter
    @Setter
    @NoArgsConstructor
    @SuperBuilder
    public static class UploadMediaResponseData {
        private String publicId;
        private String originalFilename;
        private String resourceType;
        private String format;
        private String url;
        private String secureUrl;
        private String folder;
        private Long bytes;
        private Integer width;
        private Integer height;
    }
}
