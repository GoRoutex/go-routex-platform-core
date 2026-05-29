package platform.core.common.service.persistence.exception;

import lombok.EqualsAndHashCode;
import platform.core.common.service.api.ApiResult;
import platform.core.common.service.common.RequestContext;

@EqualsAndHashCode(callSuper = true)
public class BusinessException extends BaseException {

    public BusinessException(String requestId, String requestDateTime, String channel, ApiResult result) {
        super(requestId, requestDateTime, channel, result);
    }

    public BusinessException(RequestContext context, ApiResult result) {
        super(context.requestId(), context.requestDateTime(), context.channel(), result);
    }

    public BusinessException(ApiResult result) { super(result); }
}
