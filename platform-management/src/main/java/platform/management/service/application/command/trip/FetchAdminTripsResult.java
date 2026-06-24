package platform.management.service.application.command.trip;

import lombok.Builder;

import java.util.List;

@Builder
public record FetchAdminTripsResult(
        List<FetchTripResult> items,
        int pageNumber,
        int pageSize,
        long totalElements,
        int totalPages,
        long totalTrips,
        long plannedTrips,
        long cancelledTrips
) {
}
