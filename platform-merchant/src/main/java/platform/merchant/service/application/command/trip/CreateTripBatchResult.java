package platform.merchant.service.application.command.trip;

import lombok.Builder;

import java.util.List;

@Builder
public record CreateTripBatchResult(
        String routeId,
        List<String> tripIds
) {
}
