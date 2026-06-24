package platform.merchant.service.domain.trip.port;


import platform.core.common.service.application.command.common.PagedResult;
import platform.core.common.service.application.readmodel.TripSearchView;
import platform.core.common.service.domain.trip.TripStatus;
import platform.merchant.service.domain.trip.readmodel.TripFetchView;

import java.time.OffsetDateTime;
import java.util.List;

public interface TripQueryPort {
    List<TripSearchView> searchAssignedTrips(
            String origin,
            String destination,
            String departureDate,
            int pageNumber,
            int pageSize
    );

    PagedResult<TripFetchView> fetchTrips(
            OffsetDateTime from,
            OffsetDateTime to,
            int pageNumber,
            int pageSize
    );

    PagedResult<TripFetchView> fetchAdminTrips(
            OffsetDateTime from,
            OffsetDateTime to,
            int pageNumber,
            int pageSize
    );

    long countAdminTrips(
            OffsetDateTime from,
            OffsetDateTime to,
            TripStatus status
    );

}
