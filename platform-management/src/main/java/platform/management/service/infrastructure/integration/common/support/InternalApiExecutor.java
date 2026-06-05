package platform.management.service.infrastructure.integration.common.support;

import lombok.experimental.UtilityClass;
import platform.core.common.service.api.ApiResult;
import platform.core.common.service.api.BaseResponse;
import platform.core.common.service.common.RequestContext;
import platform.core.common.service.persistence.exception.BusinessException;
import platform.core.common.service.persistence.exception.CustomerFeignException;
import platform.core.common.service.persistence.utils.ExceptionUtils;

import java.util.function.Supplier;

import static platform.core.common.service.persistence.constant.ErrorConstant.SUCCESS_CODE;
import static platform.core.common.service.persistence.constant.ErrorConstant.SYSTEM_ERROR;
import static platform.core.common.service.persistence.constant.ErrorConstant.SYSTEM_ERROR_MESSAGE;

@UtilityClass
public class InternalApiExecutor {

    public <T> T execute(RequestContext context, Supplier<BaseResponse<T>> supplier) {
        try {
            BaseResponse<T> response = supplier.get();
            if (response == null) {
                throw toBusinessException(context, systemError());
            }

            ApiResult result = response.getResult();
            if (result != null && result.getResponseCode() != null && !SUCCESS_CODE.equals(result.getResponseCode())) {
                throw toBusinessException(context, result);
            }

            if (response.getData() == null) {
                throw toBusinessException(context, systemError());
            }

            return response.getData();
        } catch (CustomerFeignException ex) {
            throw toBusinessException(context, ex.getResult() == null ? systemError() : ex.getResult());
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            throw toBusinessException(context, systemError());
        }
    }

    private BusinessException toBusinessException(RequestContext context, ApiResult result) {
        return new BusinessException(context.requestId(), context.requestDateTime(), context.channel(), result);
    }

    private ApiResult systemError() {
        return ExceptionUtils.buildResultResponse(SYSTEM_ERROR, SYSTEM_ERROR_MESSAGE);
    }

}
