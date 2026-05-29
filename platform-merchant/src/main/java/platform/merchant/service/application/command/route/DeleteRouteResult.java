package platform.merchant.service.application.command.route;

import lombok.Builder;

import java.time.OffsetDateTime;

@Builder
public record DeleteRouteResult(
        String creator,
        String routeId,
        String status,
        OffsetDateTime updatedAt
) {
}
