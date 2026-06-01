package platform.core.common.service.persistence.exception;

import lombok.Getter;
import platform.core.common.service.api.ApiResult;

@Getter
public class CustomerFeignException extends RuntimeException {

    private final int httpStatus;
    private final ApiResult result;

    public CustomerFeignException(int httpStatus, ApiResult result) {
        super(result == null ? null : result.getDescription());
        this.httpStatus = httpStatus;
        this.result = result;
    }
}
