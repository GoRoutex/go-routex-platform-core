package platform.management.service.application.command.route;

import lombok.Builder;

import java.util.List;

@Builder
public record FetchTripsResult(
        List<FetchTripResult> items,
        int pageNumber,
        int pageSize,
        long totalElements,
        int totalPages
) {
}
