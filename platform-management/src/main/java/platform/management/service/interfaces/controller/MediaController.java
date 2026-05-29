package platform.management.service.interfaces.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import platform.core.common.service.persistence.exception.BusinessException;
import platform.core.common.service.persistence.utils.ExceptionUtils;
import vn.com.go.routex.identity.security.log.SystemLog;
import platform.management.service.application.command.media.UploadMediaCommand;
import platform.management.service.application.command.media.UploadMediaResult;
import platform.management.service.application.services.MediaService;
import platform.management.service.infrastructure.persistence.utils.HttpUtils;
import platform.management.service.interfaces.factory.ApiResultFactory;
import platform.management.service.interfaces.models.media.UploadMediaRequest;
import platform.management.service.interfaces.models.media.UploadMediaResponse;
import platform.core.common.service.api.BaseRequest;

import static platform.core.common.service.persistence.constant.ApiConstant.API_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.API_VERSION;
import static platform.core.common.service.persistence.constant.ApiConstant.MANAGEMENT_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.MEDIA_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.UPLOAD;
import static platform.core.common.service.persistence.constant.ErrorConstant.INVALID_INPUT_ERROR;
import static platform.core.common.service.persistence.constant.ErrorConstant.INVALID_INPUT_MESSAGE;


@RestController
@RequestMapping(API_PATH + API_VERSION + MANAGEMENT_PATH + MEDIA_PATH)
@RequiredArgsConstructor
public class MediaController {

    private final MediaService mediaService;
    private final ObjectMapper objectMapper;

    private final SystemLog sLog = SystemLog.getLogger(this.getClass());
    private final ApiResultFactory apiResultFactory;

    @PostMapping(path = UPLOAD, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UploadMediaResponse> uploadMedia(
            @RequestParam("requestId") String requestId,
            @RequestParam("requestDateTime") String requestDateTime,
            @RequestParam("channel") String channel,
            @RequestPart(name = "file", required = false) MultipartFile file,
            @RequestPart(name = "data", required = false) String dataJson
    ) {
        BaseRequest request = BaseRequest.builder()
                .requestId(requestId)
                .requestDateTime(requestDateTime)
                .channel(channel)
                .build();
        UploadMediaRequest.UploadMediaRequestData requestData = resolveRequestData(request, file, dataJson);

        sLog.info("[MEDIA-UPLOAD] Upload Media Request: {}", request);
        UploadMediaCommand command = UploadMediaCommand
                .builder()
                .context(HttpUtils.toContext(request))
                .file(requestData.getFile())
                .folder(requestData.getFolder())
                .publicId(requestData.getPublicId())
                .build();

        UploadMediaResult result = mediaService.uploadMedia(command);


        UploadMediaResponse response = UploadMediaResponse.builder()
                .requestId(request.getRequestId())
                .requestDateTime(request.getRequestDateTime())
                .channel(request.getChannel())
                .result(apiResultFactory.buildSuccess())
                .data(UploadMediaResponse.UploadMediaResponseData.builder()
                        .publicId(result.publicId())
                        .originalFilename(result.originalFilename())
                        .resourceType(result.resourceType())
                        .format(result.format())
                        .url(result.url())
                        .secureUrl(result.secureUrl())
                        .folder(result.folder())
                        .bytes(result.bytes())
                        .width(result.width())
                        .height(result.height())
                        .build())
                .build();

        sLog.info("[MEDIA-UPLOAD] Upload Media Response: {}", request);

        return HttpUtils.buildResponse(request, response);
    }

    private UploadMediaRequest.UploadMediaRequestData resolveRequestData(
            BaseRequest request,
            MultipartFile file,
            String dataJson
    ) {
        UploadMediaRequest.UploadMediaRequestData requestData = null;
        if (dataJson != null && !dataJson.isBlank()) {
            requestData = parseDataPart(request, dataJson);
        }

        if (requestData == null) {
            throw new BusinessException(
                    request.getRequestId(),
                    request.getRequestDateTime(),
                    request.getChannel(),
                    ExceptionUtils.buildResultResponse(INVALID_INPUT_ERROR, INVALID_INPUT_MESSAGE + ": data")
            );
        }

        if (file != null) {
            requestData.setFile(file);
        }

        return requestData;
    }

    private UploadMediaRequest.UploadMediaRequestData parseDataPart(BaseRequest request, String dataJson) {
        try {
            return objectMapper.readValue(dataJson, UploadMediaRequest.UploadMediaRequestData.class);
        } catch (JsonProcessingException e) {
            throw new BusinessException(
                    request.getRequestId(),
                    request.getRequestDateTime(),
                    request.getChannel(),
                    ExceptionUtils.buildResultResponse(INVALID_INPUT_ERROR, INVALID_INPUT_MESSAGE + ": data")
            );
        }
    }
}
