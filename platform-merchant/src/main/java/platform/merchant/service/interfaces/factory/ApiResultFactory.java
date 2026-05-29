package platform.merchant.service.interfaces.factory;

import org.springframework.stereotype.Component;
import platform.core.common.service.api.ApiResult;

import static platform.core.common.service.persistence.constant.ErrorConstant.SUCCESS_CODE;
import static platform.core.common.service.persistence.constant.ErrorConstant.SUCCESS_MESSAGE;


@Component
public class ApiResultFactory {
    public ApiResult buildSuccess() {
        return ApiResult.builder()
                .responseCode(SUCCESS_CODE)
                .description(SUCCESS_MESSAGE)
                .build();
    }
}
