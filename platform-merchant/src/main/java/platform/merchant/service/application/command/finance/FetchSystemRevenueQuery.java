package platform.merchant.service.application.command.finance;

import lombok.Builder;
import platform.core.common.service.common.RequestContext;

import java.time.OffsetDateTime;

@Builder
public record FetchSystemRevenueQuery(
        OffsetDateTime startDate,
        OffsetDateTime endDate,
        RequestContext context
) {}
