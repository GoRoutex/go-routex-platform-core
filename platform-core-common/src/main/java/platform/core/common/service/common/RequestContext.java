package platform.core.common.service.common;

import lombok.Builder;

@Builder
public record RequestContext(
        String requestId,
        String requestDateTime,
        String channel,
        String merchantId,
        String userId,
        String userEmail,
        String userPhone
) {
}
