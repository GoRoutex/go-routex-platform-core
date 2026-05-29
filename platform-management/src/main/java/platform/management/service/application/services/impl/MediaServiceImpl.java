package platform.management.service.application.services.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vn.com.go.routex.identity.security.log.SystemLog;
import platform.management.service.application.command.common.RequestContext;
import platform.management.service.application.command.media.UploadMediaCommand;
import platform.management.service.application.command.media.UploadMediaResult;
import platform.management.service.application.services.MediaService;
import platform.management.service.infrastructure.persistence.config.CloudinaryProperties;
import platform.management.service.infrastructure.persistence.exception.BusinessException;
import platform.management.service.infrastructure.persistence.utils.ExceptionUtils;

import java.io.IOException;
import java.util.Map;

import static platform.management.service.infrastructure.persistence.constant.ErrorConstant.CLOUDINARY_CONFIG_MESSAGE;
import static platform.management.service.infrastructure.persistence.constant.ErrorConstant.FILE_UPLOAD_ERROR;
import static platform.management.service.infrastructure.persistence.constant.ErrorConstant.FILE_UPLOAD_ERROR_MESSAGE;
import static platform.management.service.infrastructure.persistence.constant.ErrorConstant.INVALID_FILE_MESSAGE;
import static platform.management.service.infrastructure.persistence.constant.ErrorConstant.INVALID_INPUT_ERROR;


@RequiredArgsConstructor
@Service
public class MediaServiceImpl implements MediaService {

    private final Cloudinary cloudinary;
    private final CloudinaryProperties cloudinaryProperties;
    private final SystemLog sLog = SystemLog.getLogger(this.getClass());

    @Override
    @Transactional(readOnly = true)
    public UploadMediaResult uploadMedia(UploadMediaCommand command) {
        MultipartFile file = command.file();
        validateMultipartFile(command.context(), file);
        validateCloudinaryConfiguration(command.context());

        String folder = resolveFolder(command.folder());

        try {
            Map<?, ?> uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "resource_type", "image",
                            "folder", folder,
                            "use_filename", Boolean.TRUE,
                            "unique_filename", Boolean.TRUE,
                            "overwrite", Boolean.FALSE,
                            "public_id", resolvePublicId(command.publicId())
                    )
            );


            UploadMediaResult result = UploadMediaResult.builder()
                    .publicId(stringValue(uploadResult, "public_id"))
                    .originalFilename(resolveOriginalFilename(file, uploadResult))
                    .resourceType(stringValue(uploadResult, "resource_type"))
                    .format(stringValue(uploadResult, "format"))
                    .url(stringValue(uploadResult, "url"))
                    .secureUrl(stringValue(uploadResult, "secure_url"))
                    .folder(folder)
                    .bytes(longValue(uploadResult))
                    .width(integerValue(uploadResult, "width"))
                    .height(integerValue(uploadResult, "height"))
                    .build();


            sLog.info("[MEDIA-UPLOAD] Upload Media Result: {}", result);
            return result;

        } catch(IOException exception) {
            sLog.error("[MEDIA_UPLOAD] requestId={} upload failed", command.context().requestId(), exception);
            throw new BusinessException(command.context().requestId(), command.context().requestDateTime(), command.context().channel(),
                    ExceptionUtils.buildResultResponse(FILE_UPLOAD_ERROR, FILE_UPLOAD_ERROR_MESSAGE));
        }
    }


    private Integer integerValue(Map<?, ?> payload, String key) {
        Object value = payload.get(key);
        if (value instanceof Number number) {
            return number.intValue();
        }
        return null;
    }


    private Long longValue(Map<?, ?> payload) {
        Object value = payload.get("bytes");
        if (value instanceof Number number) {
            return number.longValue();
        }
        return null;
    }

    private String resolveOriginalFilename(MultipartFile file, Map<?, ?> uploadResult) {
        String originalFilename = stringValue(uploadResult, "original_filename");
        if (originalFilename != null && !originalFilename.isBlank()) {
            return originalFilename;
        }
        return file.getOriginalFilename();
    }

    private String stringValue(Map<?, ?> payload, String key) {
        Object value = payload.get(key);
        return value == null ? null : value.toString();
    }


    private void validateMultipartFile(RequestContext request, MultipartFile file) {
        if(file == null || file.isEmpty()) {
            throw new BusinessException(request.requestId(), request.requestDateTime(), request.channel(),
                    ExceptionUtils.buildResultResponse(INVALID_INPUT_ERROR, INVALID_FILE_MESSAGE));
        }

        String contentType = file.getContentType();
        if(contentType == null || !contentType.startsWith("image/")) {
            throw new BusinessException(request.requestId(), request.requestDateTime(), request.channel(),
                    ExceptionUtils.buildResultResponse(INVALID_INPUT_ERROR, INVALID_FILE_MESSAGE));
        }
    }


    private String resolveFolder(String folder) {
        if(!isBlank(folder)) {
            return folder.trim();
        }

        if(!isBlank(cloudinaryProperties.getDefaultFolder())){
            return cloudinaryProperties.getDefaultFolder().trim();
        }

        return "routex";
    }

    private void validateCloudinaryConfiguration(RequestContext request) {
        if(isBlank(cloudinaryProperties.getApiKey())
        || isBlank(cloudinaryProperties.getApiSecret())
        || isBlank(cloudinaryProperties.getCloudName())) {
            throw new BusinessException(request.requestId(), request.requestDateTime(), request.channel(),
                    ExceptionUtils.buildResultResponse(FILE_UPLOAD_ERROR, CLOUDINARY_CONFIG_MESSAGE));
        }
    }


    private String resolvePublicId(String publicId) {
        if(!isBlank(publicId)) {
            return publicId.trim();
        }

        return null;
    }

    private boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}
