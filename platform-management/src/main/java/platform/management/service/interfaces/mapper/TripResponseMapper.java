package platform.management.service.interfaces.mapper;

import org.springframework.stereotype.Component;
import platform.management.service.application.command.trip.FetchRoundTripDetailResult;
import platform.management.service.application.command.trip.FetchTripResult;
import platform.management.service.application.command.trip.RoutePointResult;
import platform.management.service.application.command.trip.SearchRoundTripResult;
import platform.management.service.application.command.trip.SearchTripItemResult;
import platform.management.service.interfaces.models.trip.FetchRoundTripDetailResponse;
import platform.management.service.interfaces.models.trip.FetchTripResponse;
import platform.management.service.interfaces.models.trip.SearchRoundTripResponse;
import platform.management.service.interfaces.models.trip.SearchTripResponse;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TripResponseMapper {

    public FetchTripResponse.FetchTripResponseData toFetchRouteResponseData(FetchTripResult item) {
        return FetchTripResponse.FetchTripResponseData.builder()
                .id(item.id())
                .creator(item.creator())
                .tripCode(item.tripCode())
                .originCode(item.originCode())
                .originName(item.originName())
                .merchantDisplayName(item.merchantDisplayName())
                .destinationCode(item.destinationCode())
                .destinationName(item.destinationName())
                .originProvinceId(item.originProvinceId())
                .destinationProvinceId(item.destinationProvinceId())
                .originDepartmentId(item.originDepartmentId())
                .destinationDepartmentId(item.destinationDepartmentId())
                .departureTime(item.departureTime())
                .rawDepartureDate(item.rawDepartureDate())
                .rawDepartureTime(item.rawDepartureTime())
                .duration(item.durationMinutes())
                .rawArrivalTime(calculateArrivalTime(item.rawDepartureTime(), item.durationMinutes()))
                .status(item.status())
                .vehicleId(item.vehicleId())
                .vehiclePlate(item.vehiclePlate())
                .driverId(item.driverId())
                .driverName(item.driverName())
                .hasFloor(item.hasFloor())
                .assignedAt(item.assignedAt())
                .routePoints(item.routePoints() == null ? null : item.routePoints().stream()
                        .map(this::toSearchRoutePoint)
                        .toList())
                .build();
    }

    public FetchTripResponse.FetchTripResponseData toPublicFetchTripResponseData(FetchTripResult item) {
        return FetchTripResponse.FetchTripResponseData.builder()
                .id(item.id())
                .tripCode(item.tripCode())
                .originCode(item.originCode())
                .originName(item.originName())
                .merchantId(item.merchantId())
                .merchantName(item.merchantName())
                .merchantDisplayName(item.merchantDisplayName())
                .destinationCode(item.destinationCode())
                .destinationName(item.destinationName())
                .originProvinceId(item.originProvinceId())
                .destinationProvinceId(item.destinationProvinceId())
                .originDepartmentId(item.originDepartmentId())
                .originDepartmentName(item.originDepartmentName())
                .destinationDepartmentName(item.destinationDepartmentName())
                .destinationDepartmentId(item.destinationDepartmentId())
                .rawDepartureTime(item.rawDepartureTime())
                .rawDepartureDate(item.rawDepartureDate())
                .duration(item.durationMinutes())
                .rawArrivalTime(calculateArrivalTime(item.rawDepartureTime(), item.durationMinutes()))
                .departureTime(item.departureTime())
                .status(item.status())
                .vehicleId(item.vehicleId())
                .vehiclePlate(item.vehiclePlate())
                .driverId(item.driverId())
                .driverName(item.driverName())
                .hasFloor(item.hasFloor())
                .ticketPrice(item.ticketPrice())
                .availableSeats(item.availableSeat())
                .routePoints(item.routePoints() == null ? null : item.routePoints().stream()
                        .map(this::toSearchRoutePoint)
                        .toList())
                .build();
    }

    public FetchTripResponse.FetchTripResponseData toFetchTripDetailResponseData(FetchTripResult item) {
        return FetchTripResponse.FetchTripResponseData.builder()
                .id(item.id())
                .creator(item.creator())
                .tripCode(item.tripCode())
                .merchantId(item.merchantId())
                .merchantName(item.merchantName())
                .merchantDisplayName(item.merchantDisplayName())
                .originCode(item.originCode())
                .originName(item.originName())
                .destinationCode(item.destinationCode())
                .destinationName(item.destinationName())
                .originProvinceId(item.originProvinceId())
                .destinationProvinceId(item.destinationProvinceId())
                .originDepartmentId(item.originDepartmentId())
                .originDepartmentName(item.originDepartmentName())
                .destinationDepartmentId(item.destinationDepartmentId())
                .destinationDepartmentName(item.destinationDepartmentName())
                .rawDepartureTime(item.rawDepartureTime())
                .rawDepartureDate(item.rawDepartureDate())
                .duration(item.durationMinutes())
                .rawArrivalTime(calculateArrivalTime(item.rawDepartureTime(), item.durationMinutes()))
                .departureTime(item.departureTime())
                .status(item.status())
                .vehicleId(item.vehicleId())
                .vehiclePlate(item.vehiclePlate())
                .driverId(item.driverId())
                .driverName(item.driverName())
                .hasFloor(item.hasFloor())
                .assignedAt(item.assignedAt())
                .availableSeats(item.availableSeat())
                .ticketPrice(item.ticketPrice())
                .routePoints(item.routePoints() == null ? null : item.routePoints().stream()
                        .map(this::toSearchRoutePoint)
                        .toList())
                .build();
    }

    public FetchRoundTripDetailResponse.FetchRoundTripDetailResponseData toFetchRoundTripDetailResponseData(FetchRoundTripDetailResult item) {
        return FetchRoundTripDetailResponse.FetchRoundTripDetailResponseData.builder()
                .outboundTrip(toFetchTripDetailResponseData(item.outboundTrip()))
                .returnTrip(toFetchTripDetailResponseData(item.returnTrip()))
                .build();
    }

    public SearchRoundTripResponse.SearchRoundTripResponseData toSearchRoundTripResponseData(SearchRoundTripResult item) {
        return SearchRoundTripResponse.SearchRoundTripResponseData.builder()
                .outboundTrips(item.outBoundTrip().stream()
                        .map(out -> SearchTripResponse.SearchTripResponseData
                                .builder()
                                .id(out.id())
                                .merchantId(out.merchantId())
                                .merchantName(out.merchantName())
                                .vehicleId(out.vehicleId())
                                .driverId(out.driverId())
                                .routeId(out.routeId())
                                .originCode(out.originCode())
                                .originName(out.originName())
                                .destinationCode(out.destinationCode())
                                .destinationName(out.destinationName())
                                .originProvinceId(out.originProvinceId())
                                .destinationProvinceId(out.destinationProvinceId())
                                .originDepartmentName(out.originDepartmentName())
                                .destinationDepartmentName(out.destinationDepartmentName())
                                .originDepartmentId(out.originDepartmentId())
                                .destinationDepartmentId(out.destinationDepartmentId())
                                .ticketPrice(out.ticketPrice())
                                .availableSeats(out.availableSeats())
                                .departureTime(out.departureTime())
                                .rawDepartureDate(out.rawDepartureDate())
                                .rawDepartureTime(out.rawDepartureTime())
                                .rawArrivalTime(calculateArrivalTime(out.rawDepartureTime(), out.durationMinutes()))
                                .vehiclePlate(out.vehiclePlate())
                                .hasFloor(out.hasFloor())
                                .tripCode(out.tripCode())
                                .routePoints(toSearchRoutePoints(out.routePoints()))
                                .build())
                        .collect(Collectors.toList()))
                .returnTrips(item.returnTrip().stream()
                        .map(inbound -> SearchTripResponse.SearchTripResponseData.builder()
                                .id(inbound.id())
                                .merchantId(inbound.merchantId())
                                .merchantName(inbound.merchantName())
                                .vehicleId(inbound.vehicleId())
                                .driverId(inbound.driverId())
                                .routeId(inbound.routeId())
                                .originCode(inbound.originCode())
                                .originName(inbound.originName())
                                .destinationCode(inbound.destinationCode())
                                .destinationName(inbound.destinationName())
                                .originProvinceId(inbound.originProvinceId())
                                .destinationProvinceId(inbound.destinationProvinceId())
                                .originDepartmentName(inbound.originDepartmentName())
                                .destinationDepartmentName(inbound.destinationDepartmentName())
                                .originDepartmentId(inbound.originDepartmentId())
                                .destinationDepartmentId(inbound.destinationDepartmentId())
                                .ticketPrice(inbound.ticketPrice())
                                .availableSeats(inbound.availableSeats())
                                .departureTime(inbound.departureTime())
                                .rawDepartureDate(inbound.rawDepartureDate())
                                .rawDepartureTime(inbound.rawDepartureTime())
                                .rawArrivalTime(calculateArrivalTime(inbound.rawDepartureTime(), inbound.durationMinutes()))
                                .vehiclePlate(inbound.vehiclePlate())
                                .hasFloor(inbound.hasFloor())
                                .tripCode(inbound.tripCode())
                                .routePoints(toSearchRoutePoints(inbound.routePoints()))
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

    public SearchTripResponse.SearchTripResponseData toSearchTripResponseData(SearchTripItemResult item) {
        return SearchTripResponse.SearchTripResponseData.builder()
                .id(item.id())
                .merchantId(item.merchantId())
                .merchantName(item.merchantName())
                .vehicleId(item.vehicleId())
                .driverId(item.driverId())
                .routeId(item.routeId())
                .originCode(item.originCode())
                .originName(item.originName())
                .destinationCode(item.destinationCode())
                .destinationName(item.destinationName())
                .originProvinceId(item.originProvinceId())
                .destinationProvinceId(item.destinationProvinceId())
                .originDepartmentName(item.originDepartmentName())
                .destinationDepartmentName(item.destinationDepartmentName())
                .originDepartmentId(item.originDepartmentId())
                .destinationDepartmentId(item.destinationDepartmentId())
                .ticketPrice(item.ticketPrice())
                .availableSeats(item.availableSeats())
                .departureTime(item.departureTime())
                .rawDepartureDate(item.rawDepartureDate())
                .rawDepartureTime(item.rawDepartureTime())
                .rawArrivalTime(calculateArrivalTime(item.rawDepartureTime(), item.durationMinutes()))
                .vehiclePlate(item.vehiclePlate())
                .hasFloor(item.hasFloor())
                .tripCode(item.tripCode())
                .routePoints(toSearchRoutePoints(item.routePoints()))
                .build();
    }

    private List<SearchTripResponse.SearchRoutePoints> toSearchRoutePoints(List<RoutePointResult> routePoints) {
        return routePoints == null ? List.of() : routePoints.stream()
                .map(this::toSearchRoutePoint)
                .toList();
    }

    public SearchTripResponse.SearchRoutePoints toSearchRoutePoint(RoutePointResult point) {
        return SearchTripResponse.SearchRoutePoints.builder()
                .id(point.id())
                .stopOrder(point.stopOrder())
                .routeId(point.routeId())
                .note(point.note())
                .departmentId(point.departmentId())
                .stopName(point.stopName())
                .stopAddress(point.stopAddress())
                .stopCity(point.stopCity())
                .stopLatitude(point.stopLatitude())
                .stopLongitude(point.stopLongitude())
                .timeAtDepartment(point.timeAtDepartment())
                .build();
    }

    private String calculateArrivalTime(String rawDepartureTime, Long durationMinutes) {
        if (rawDepartureTime == null || durationMinutes == null) return null;
        try {
            String[] parts = rawDepartureTime.split(":");
            int hours = Integer.parseInt(parts[0]);
            int minutes = Integer.parseInt(parts[1]);

            int totalMinutes = hours * 60 + minutes + durationMinutes.intValue();
            int arrivalHours = (totalMinutes / 60) % 24;
            int arrivalMinutes = totalMinutes % 60;

            return String.format("%02d:%02d", arrivalHours, arrivalMinutes);
        } catch (Exception e) {
            return null;
        }
    }
}
