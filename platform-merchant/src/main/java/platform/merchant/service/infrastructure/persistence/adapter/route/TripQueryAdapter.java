package platform.merchant.service.infrastructure.persistence.adapter.route;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import platform.core.common.service.application.command.common.PagedResult;
import platform.core.common.service.application.readmodel.TripSearchView;
import platform.core.common.service.domain.trip.TripStatus;
import platform.merchant.service.application.specification.TripSpecification;
import platform.merchant.service.domain.assignment.model.TripAssignmentRecord;
import platform.merchant.service.domain.assignment.port.TripAssignmentRepositoryPort;
import platform.merchant.service.domain.merchant.model.Merchant;
import platform.merchant.service.domain.merchant.port.MerchantRepositoryPort;
import platform.merchant.service.domain.route.model.RouteAggregate;
import platform.merchant.service.domain.route.port.RouteAggregateRepositoryPort;
import platform.merchant.service.domain.trip.model.TripAggregate;
import platform.merchant.service.domain.trip.port.TripAggregateRepositoryPort;
import platform.merchant.service.domain.trip.port.TripQueryPort;
import platform.merchant.service.domain.trip.readmodel.TripFetchView;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TripQueryAdapter implements TripQueryPort {

    private final MerchantRepositoryPort merchantRepositoryPort;
    private final TripAggregateRepositoryPort tripAggregateRepositoryPort;
    private final TripAssignmentRepositoryPort tripAssignmentRepositoryPort;
    private final RouteAggregateRepositoryPort routeAggregateRepositoryPort;

    @Override
    public List<TripSearchView> searchAssignedTrips(
            String originName,
            String destinationName,
            String departureDate,
            int pageNumber,
            int pageSize
    ) {
        Specification<TripAggregate> specification = TripSpecification.originNameContainsIgnoreCase(originName)
                .and(TripSpecification.destinationNameContainsIgnoreCase(destinationName))
                .and(TripSpecification.hasRawDepartureDate(departureDate))
                .and(TripSpecification.assignedStatus(TripStatus.ASSIGNED));

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.ASC, "departureTime"));

        Page<TripAggregate> page = tripAggregateRepositoryPort.findAll(specification, pageable);
        List<TripAggregate> tripAggregates = page.getContent();

        List<String> tripIds = tripAggregates.stream()
                .map(TripAggregate::getId)
                .toList();

        Map<String, TripAssignmentRecord> assignmentRecordMap = tripAssignmentRepositoryPort.findLatestActiveByTripIds(tripIds);

        List<String> routeIds = tripAggregates.stream()
                .map(TripAggregate::getRouteId)
                .toList();

        Map<String, RouteAggregate> routeAggregateMap = routeAggregateRepositoryPort.findAllByIdIn(routeIds);

        return tripAggregates
                .stream()
                .map(trip -> {

                    TripAssignmentRecord assignmentRecord = assignmentRecordMap.get(trip.getId());
                    RouteAggregate routeAggregate = routeAggregateMap.get(trip.getRouteId());

                    return TripSearchView.builder()
                            .tripInformation(TripSearchView.TripInformation.builder()
                                    .id(trip.getId())
                                    .creator(trip.getCreator())
                                    .merchantId(trip.getMerchantId())
                                    .tripCode(trip.getTripCode())
                                    .departureTime(trip.getDepartureTime())
                                    .rawDepartureDate(trip.getRawDepartureDate())
                                    .rawDepartureTime(trip.getRawDepartureTime())
                                    .status(trip.getStatus())
                                    .build())
                            .assignment(TripSearchView.TripAssignment.builder()
                                    .ticketPrice(assignmentRecord.getTicketPrice())
                                    .driverId(assignmentRecord.getDriverId())
                                    .vehicleId(assignmentRecord.getVehicleId())
                                    .build())
                            .routeInformation(TripSearchView.RouteInformation.builder()
                                    .routeId(trip.getRouteId())
                                    .originCode(routeAggregate.getOriginCode())
                                    .originName(routeAggregate.getOriginName())
                                    .destinationCode(routeAggregate.getDestinationCode())
                                    .destinationName(routeAggregate.getDestinationName())
                                    .originProvinceId(routeAggregate.getOriginProvinceId())
                                    .destinationProvinceId(routeAggregate.getDestinationProvinceId())
                                    .originDepartmentId(routeAggregate.getOriginDepartmentId())
                                    .destinationDepartmentId(routeAggregate.getDestinationDepartmentId())
                                    .durationMinutes(routeAggregate.getDuration())
                                    .build())
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    public PagedResult<TripFetchView> fetchTrips(OffsetDateTime from,
                                                 OffsetDateTime to,
                                                 int pageNumber,
                                                 int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "departureTime"));

        Page<TripAggregate> page = tripAggregateRepositoryPort.findAllByFilter(from, to, pageable);

        List<TripAggregate> tripList = page.getContent();

        List<String> routeIds = tripList.stream()
                .map(TripAggregate::getRouteId)
                .toList();

        List<String> merchantIds = tripList.stream()
                .map(TripAggregate::getMerchantId)
                .toList();

        Map<String, Merchant> merchantMap = merchantRepositoryPort.findNamesByIds(merchantIds);
        Map<String, RouteAggregate> routeAggregateMap = routeAggregateRepositoryPort.findAllByIdIn(routeIds);

        List<TripFetchView> items = page.getContent().stream()
                .map(trip -> {
                    RouteAggregate routeAggregate = routeAggregateMap.get(trip.getRouteId());
                    Merchant merchantAggregate = merchantMap.get(trip.getMerchantId());
                    return TripFetchView.builder()
                            .id(trip.getId())
                            .tripCode(trip.getTripCode())
                            .routeId(trip.getRouteId())
                            .merchantId(trip.getMerchantId())
                            .merchantName(merchantAggregate.getDisplayName())
                            .creator(trip.getCreator())
                            .originCode(routeAggregate.getOriginCode())
                            .originName(routeAggregate.getOriginName())
                            .destinationCode(routeAggregate.getDestinationCode())
                            .destinationName(routeAggregate.getDestinationName())
                            .originProvinceId(routeAggregate.getOriginProvinceId())
                            .destinationProvinceId(routeAggregate.getDestinationProvinceId())
                            .originDepartmentId(routeAggregate.getOriginDepartmentId())
                            .originDepartmentName(routeAggregate.getOriginDepartmentName())
                            .destinationDepartmentName(routeAggregate.getDestinationDepartmentName())
                            .destinationDepartmentId(routeAggregate.getDestinationDepartmentId())
                            .departureTime(trip.getDepartureTime())
                            .rawDepartureDate(trip.getRawDepartureDate())
                            .rawDepartureTime(trip.getRawDepartureTime())
                            .durationMinutes(routeAggregate.getDuration())
                            .status(trip.getStatus())
                            .build();
                })
                .collect(Collectors.toList());

        return PagedResult.<TripFetchView>builder()
                .items(items)
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }
}
