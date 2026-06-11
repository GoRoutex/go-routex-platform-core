package platform.management.service.application.services;


import platform.management.service.application.command.trip.FetchTripQuery;
import platform.management.service.application.command.trip.FetchTripResult;
import platform.management.service.application.command.trip.FetchTripsQuery;
import platform.management.service.application.command.trip.FetchTripsResult;
import platform.management.service.application.command.trip.SearchRoundTripQuery;
import platform.management.service.application.command.trip.SearchRoundTripResult;
import platform.management.service.application.command.trip.SearchTripQuery;
import platform.management.service.application.command.trip.SearchTripResult;

public interface TripManagementService {
    SearchTripResult searchTrip(SearchTripQuery query);

    FetchTripResult fetchTripDetail(FetchTripQuery query);

    FetchTripsResult fetchTrips(FetchTripsQuery query);

    SearchRoundTripResult searchRoundTrip(SearchRoundTripQuery query);
}
