package platform.merchant.service.infrastructure.persistence.adapter.route;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import platform.merchant.service.application.specification.TripSpecification;
import platform.merchant.service.domain.assignment.model.TripAssignmentRecord;
import platform.merchant.service.domain.assignment.port.TripAssignmentRepositoryPort;
import platform.core.common.service.application.command.common.PagedResult;
import platform.merchant.service.domain.route.model.RouteAggregate;
import platform.merchant.service.domain.route.port.RouteAggregateRepositoryPort;
import platform.core.common.service.domain.trip.TripStatus;
import platform.merchant.service.domain.trip.model.TripAggregate;
import platform.merchant.service.domain.trip.port.TripAggregateRepositoryPort;
import platform.merchant.service.domain.trip.port.TripQueryPort;
import platform.merchant.service.domain.trip.readmodel.TripFetchView;
import platform.core.common.service.application.readmodel.TripSearchView;
import platform.core.common.service.persistence.exception.BusinessException;
import platform.merchant.service.infrastructure.persistence.jpa.trip.entity.TripEntity;
import platform.merchant.service.infrastructure.persistence.jpa.trip.repository.TripEntityRepository;
import platform.core.common.service.persistence.utils.ExceptionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static platform.core.common.service.persistence.constant.ErrorConstant.RECORD_NOT_FOUND;
import static platform.core.common.service.persistence.constant.ErrorConstant.ROUTE_NOT_FOUND;
import static platform.core.common.service.persistence.constant.ErrorConstant.TRIP_ASSIGNMENT_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class TripQueryAdapter implements TripQueryPort {

    private final TripAggregateRepositoryPort tripAggregateRepositoryPort;
    private final TripAssignmentRepositoryPort tripAssignmentRepositoryPort;
    private final RouteAggregateRepositoryPort routeAggregateRepositoryPort;

    @Override
    public List<TripSearchView> searchAssignedTrips(
            String merchantId,
            String originName,
            String destinationName,
            int pageNumber,
            int pageSize
    ) {
        Specification<TripAggregate> specification = Specification.where(TripSpecification.hasMerchantId(merchantId))
                .and(TripSpecification.originNameContainsIgnoreCase(originName))
                .and(TripSpecification.destinationNameContainsIgnoreCase(destinationName))
                .and(TripSpecification.assignedStatus(TripStatus.ASSIGNED));

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.ASC, "departureDate"));

        return tripAggregateRepositoryPort.findAll(specification, pageable)
                .getContent()
                .stream()
                .map(trip -> {

                    TripAssignmentRecord assignmentRecord = tripAssignmentRepositoryPort.findActiveByTripId(trip.getId(), trip.getMerchantId())
                            .orElseThrow(() -> new BusinessException(ExceptionUtils.buildResultResponse(RECORD_NOT_FOUND, TRIP_ASSIGNMENT_NOT_FOUND)));

                    RouteAggregate routeAggregate = routeAggregateRepositoryPort.findById(trip.getRouteId())
                            .orElseThrow(() -> new BusinessException(ExceptionUtils.buildResultResponse(RECORD_NOT_FOUND, String.format(ROUTE_NOT_FOUND, trip.getRouteId()))));

                    return TripSearchView.builder()
                            .id(trip.getId())
                            .driverId(assignmentRecord.getDriverId())
                            .vehicleId(assignmentRecord.getVehicleId())
                            .merchantId(trip.getMerchantId())
                            .tripCode(trip.getTripCode())
                            .originCode(routeAggregate.getOriginCode())
                            .originName(routeAggregate.getOriginName())
                            .destinationCode(routeAggregate.getDestinationCode())
                            .destinationName(routeAggregate.getDestinationName())
                            .departureTime(trip.getDepartureTime())
                            .rawDepartureDate(trip.getRawDepartureDate())
                            .rawDepartureTime(trip.getRawDepartureTime())
                            .status(trip.getStatus())
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    public PagedResult<TripFetchView> fetchTrips(String merchantId, List<String> merchantIds, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "departureDate"));
        Specification<TripAggregate> specification = Specification.where(TripSpecification.hasMerchantId(merchantId))
                .and(TripSpecification.hasMerchantIds(merchantIds));
        Page<TripAggregate> page = tripAggregateRepositoryPort.findAll(specification, pageable);
        List<TripAggregate> tripList = page.getContent();

        List<String> routeIds = tripList.stream()
                .map(TripAggregate::getRouteId)
                .toList();

        Map<String, RouteAggregate> routeAggregateMap = routeAggregateRepositoryPort.findAllByIdIn(routeIds);

        List<TripFetchView> items = page.getContent().stream()
                .map(trip -> {
                    RouteAggregate routeAggregate = routeAggregateMap.get(trip.getRouteId());
                    return TripFetchView.builder()
                            .id(trip.getId())
                            .tripCode(trip.getTripCode())
                            .routeId(trip.getRouteId())
                            .creator(trip.getCreator())
                            .originCode(routeAggregate.getOriginCode())
                            .originName(routeAggregate.getOriginName())
                            .destinationCode(routeAggregate.getDestinationCode())
                            .destinationName(routeAggregate.getDestinationName())
                            .originProvinceId(routeAggregate.getOriginProvinceId())
                            .destinationProvinceId(routeAggregate.getDestinationProvinceId())
                            .originDepartmentId(routeAggregate.getOriginDepartmentId())
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
