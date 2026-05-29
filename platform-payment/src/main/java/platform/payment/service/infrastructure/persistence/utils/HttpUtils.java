package platform.payment.service.infrastructure.persistence.utils;

import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import platform.core.common.service.common.RequestContext;
import platform.core.common.service.persistence.exception.BusinessException;
import platform.core.common.service.api.BaseRequest;
import platform.core.common.service.api.BaseResponse;
import platform.core.common.service.persistence.utils.ExceptionUtils;

import static platform.core.common.service.persistence.constant.ErrorConstant.TIMEOUT_ERROR;
import static platform.core.common.service.persistence.constant.ErrorConstant.TIMEOUT_ERROR_MESSAGE;


@UtilityClass
public class HttpUtils {

    public RequestContext toContext(BaseRequest request) {
        return toContext(request, null);
    }

    public RequestContext toContext(BaseRequest request, String merchantId) {
        return RequestContext.builder()
                .requestId(request.getRequestId())
                .requestDateTime(request.getRequestDateTime())
                .channel(request.getChannel())
                .merchantId(merchantId)
                .build();
    }

    public <T, R extends BaseResponse<T>> ResponseEntity<R> buildResponse(BaseRequest request, R response) {
        if (response == null) {
            throw new BusinessException(
                    request.getRequestId(),
                    request.getRequestDateTime(),
                    request.getChannel(),
                    ExceptionUtils.buildResultResponse(TIMEOUT_ERROR, TIMEOUT_ERROR_MESSAGE)
            );
        }

        response.setRequestId(request.getRequestId());
        response.setRequestDateTime(request.getRequestDateTime());
        response.setChannel(request.getChannel());

        return ResponseEntity
                .status(response.getData() == null ? HttpStatus.BAD_REQUEST : HttpStatus.OK)
                .body(response);
    }
}
