package platform.management.service.application.command.media;


import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;
import platform.core.common.service.common.RequestContext;

@Builder
public record UploadMediaCommand(
        RequestContext context,
        MultipartFile file,
        String folder,
        String publicId
) {
}
