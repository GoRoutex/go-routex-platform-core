package platform.core.common.service.persistence.utils;


import lombok.experimental.UtilityClass;
import platform.core.common.service.api.ApiResult;

@UtilityClass
public class ExceptionUtils {

    public ApiResult buildResultResponse(String responseCode, String description) {
        return ApiResult
                .builder()
                .responseCode(responseCode)
                .description(description)
                .build();
    }
}
