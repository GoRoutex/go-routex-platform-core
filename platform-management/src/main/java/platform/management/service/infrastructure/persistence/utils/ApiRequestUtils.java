package platform.management.service.infrastructure.persistence.utils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.experimental.UtilityClass;
import platform.management.service.application.command.common.PageContext;
import platform.management.service.application.command.common.RequestContext;
import platform.management.service.infrastructure.persistence.config.RequestAttributes;
import platform.management.service.infrastructure.persistence.exception.BusinessException;
import platform.core.common.service.api.BaseRequest;

import java.util.List;

import static platform.management.service.infrastructure.persistence.constant.ApplicationConstant.DEFAULT_PAGE_NUMBER;
import static platform.management.service.infrastructure.persistence.constant.ApplicationConstant.DEFAULT_PAGE_SIZE;
import static platform.management.service.infrastructure.persistence.constant.ErrorConstant.INVALID_INPUT_ERROR;
import static platform.management.service.infrastructure.persistence.constant.ErrorConstant.INVALID_PAGE_NUMBER;
import static platform.management.service.infrastructure.persistence.constant.ErrorConstant.INVALID_PAGE_SIZE;

@UtilityClass
public class ApiRequestUtils {
    public List<Integer> validatePageContext(RequestContext context, PageContext query) {
        int pageSize = parseIntOrDefault(query.pageSize(), DEFAULT_PAGE_SIZE, "pageSize",
                context.requestId(), context.requestDateTime(), context.channel());
        int pageNumber = parseIntOrDefault(query.pageNumber(), DEFAULT_PAGE_NUMBER, "pageNumber",
                context.requestId(), context.requestDateTime(), context.channel());

        if (pageSize < 1 || pageSize > 100) {
            throw new BusinessException( context.requestId(),  context.requestDateTime(),  context.channel(),
                    ExceptionUtils.buildResultResponse(INVALID_INPUT_ERROR, INVALID_PAGE_SIZE));
        }
        if (pageNumber < 1) {
            throw new BusinessException( context.requestId(),  context.requestDateTime(),  context.channel(),
                    ExceptionUtils.buildResultResponse(INVALID_INPUT_ERROR, INVALID_PAGE_NUMBER));
        }

        return List.of(pageSize, pageNumber);

    }

    public BaseRequest getBaseRequestOrDefault(HttpServletRequest request) {

        String requestId =
                (String) request.getAttribute(RequestAttributes.REQUEST_ID);

        String requestDateTime =
                (String) request.getAttribute(RequestAttributes.REQUEST_DATE_TIME);

        String requestChannel =
                (String) request.getAttribute(RequestAttributes.CHANNEL);

        return BaseRequest.builder()
                .requestId(requestId)
                .requestDateTime(requestDateTime)
                .channel(requestChannel)
                .build();
    }

    public String getMerchantId(HttpServletRequest request) {
        String merchantId = (String) request.getAttribute(RequestAttributes.MERCHANT_ID);
        if (merchantId != null && !merchantId.isBlank()) {
            return merchantId.trim();
        }
        return null;
    }

    public String requireMerchantId(HttpServletRequest request, BaseRequest baseRequest) {
        String merchantId = getMerchantId(request);
        if (merchantId == null || merchantId.isBlank()) {
            throw new BusinessException(
                    baseRequest.getRequestId(),
                    baseRequest.getRequestDateTime(),
                    baseRequest.getChannel(),
                    ExceptionUtils.buildResultResponse(INVALID_INPUT_ERROR, "merchantId is required")
            );
        }
        return merchantId;
    }

    public BaseRequest getHeader(RequestContext context) {
        return BaseRequest.builder()
                .requestId(context.requestId())
                .requestDateTime(context.requestDateTime())
                .channel(context.channel())
                .build();
    }

    public int parseIntOrDefault(
            String v,
            int defaultValue,
            String field,
            String requestId,
            String requestDateTime,
            String channel
    ) {
        if (v == null || v.isBlank()) return defaultValue;
        return DateTimeUtils.parseIntOrThrow(v, field, requestId, requestDateTime, channel);
    }


    public String firstNonBlank(String value, String fallback) {
        return (value == null || value.isBlank()) ? fallback : value.trim();
    }
}
