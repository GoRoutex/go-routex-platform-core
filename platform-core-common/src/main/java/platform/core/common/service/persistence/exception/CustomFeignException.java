package platform.core.common.service.persistence.exception;

import platform.core.common.service.api.ApiResult;

public class CustomFeignException extends RuntimeException {
    private final String requestId;
    private final String requestDateTime;
    private final String channel;
    private final ApiResult resultResponse;
    private final String targetService;
    private final int status;
    private final String description;
    private final Exception originalException;

    public CustomFeignException(String requestId, String requestDateTime, String channel, ApiResult resultResponse, String targetService, int status, String description, Exception originalException) {
        super(description, originalException);
        this.requestId = requestId;
        this.requestDateTime = requestDateTime;
        this.channel = channel;
        this.resultResponse = resultResponse;
        this.targetService = targetService;
        this.status = status;
        this.description = description;
        this.originalException = originalException;
    }
}
