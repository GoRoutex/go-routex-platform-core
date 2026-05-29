package platform.driver.service.infrastructure.persistence.utils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.experimental.UtilityClass;
import platform.core.common.service.api.BaseRequest;
import platform.core.common.service.common.RequestAttributes;

@UtilityClass
public class ApiRequestUtils {
    public static BaseRequest getBaseRequestOrDefault(HttpServletRequest request) {

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
}
