package platform.management.service.interfaces.models.media;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.web.multipart.MultipartFile;
import platform.core.common.service.api.BaseRequest;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UploadMediaRequest extends BaseRequest {

    @Valid
    @NotNull
    private UploadMediaRequestData data;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UploadMediaRequestData {
        @NotNull
        private MultipartFile file;
        private String folder;
        private String publicId;
    }
}