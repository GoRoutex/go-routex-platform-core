package platform.management.service.application.services;


import platform.management.service.application.command.route.FetchTripQuery;
import platform.management.service.application.command.route.FetchTripResult;
import platform.management.service.application.command.route.FetchTripsQuery;
import platform.management.service.application.command.route.FetchTripsResult;
import platform.management.service.application.command.route.SearchTripQuery;
import platform.management.service.application.command.route.SearchTripResult;

public interface TripManagementService {
    SearchTripResult searchTrip(SearchTripQuery query);

    FetchTripResult fetchTripDetail(FetchTripQuery query);

    FetchTripsResult fetchTrips(FetchTripsQuery query);
}
