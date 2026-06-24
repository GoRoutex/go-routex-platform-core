package platform.management.service.application.services;


import platform.management.service.application.command.trip.FetchAdminTripsQuery;
import platform.management.service.application.command.trip.FetchAdminTripsResult;
import platform.management.service.application.command.trip.FetchRoundTripDetailQuery;
import platform.management.service.application.command.trip.FetchRoundTripDetailResult;
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

    FetchRoundTripDetailResult fetchRoundTripDetail(FetchRoundTripDetailQuery query);

    FetchTripsResult fetchTrips(FetchTripsQuery query);

    FetchAdminTripsResult fetchAdminTrips(FetchAdminTripsQuery query);

    SearchRoundTripResult searchRoundTrip(SearchRoundTripQuery query);
}
