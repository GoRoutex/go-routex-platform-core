package platform.management.service.infrastructure.persistence.adapter.route;

import org.springframework.stereotype.Component;
import platform.management.service.infrastructure.persistence.jpa.routepoint.entity.RouteStopEntity;
import platform.merchant.service.domain.assignment.model.TripAssignmentRecord;
import platform.merchant.service.domain.route.model.RouteAggregate;
import platform.merchant.service.domain.route.model.RouteStopPlan;
import platform.merchant.service.domain.vehicle.model.VehicleProfile;
import platform.merchant.service.infrastructure.persistence.jpa.assignment.entity.TripAssignmentEntity;
import platform.merchant.service.infrastructure.persistence.jpa.route.entity.RouteEntity;
import platform.merchant.service.infrastructure.persistence.jpa.vehicle.entity.VehicleEntity;
import platform.merchant.service.infrastructure.persistence.jpa.vehicle.entity.VehicleTemplateEntity;

@Component
final class RoutePersistenceMapper {

    public RouteAggregate toAggregate(RouteEntity route) {
        return RouteAggregate.builder()
                .id(route.getId())
                .merchantId(route.getMerchantId())
                .creator(route.getCreator())
                .originCode(route.getOriginCode())
                .originName(route.getOriginName())
                .destinationCode(route.getDestinationCode())
                .destinationName(route.getDestinationName())
                .originProvinceId(route.getOriginProvinceId())
                .destinationProvinceId(route.getDestinationProvinceId())
                .originDepartmentId(route.getOriginDepartmentId())
                .destinationDepartmentId(route.getDestinationDepartmentId())
                .originDepartmentName(route.getOriginDepartmentName())
                .destinationDepartmentName(route.getDestinationDepartmentName())
                .status(route.getStatus())
                .duration(route.getDuration())
                .distance(route.getDistance())
                .createdAt(route.getCreatedAt())
                .createdBy(route.getCreatedBy())
                .updatedAt(route.getUpdatedAt())
                .updatedBy(route.getUpdatedBy())
                .build();
    }

    public RouteEntity toEntity(RouteAggregate aggregate) {
        return RouteEntity.builder()
                .id(aggregate.getId())
                .merchantId(aggregate.getMerchantId())
                .creator(aggregate.getCreator())
                .originCode(aggregate.getOriginCode())
                .originName(aggregate.getOriginName())
                .destinationCode(aggregate.getDestinationCode())
                .destinationName(aggregate.getDestinationName())
                .originProvinceId(aggregate.getOriginProvinceId())
                .destinationProvinceId(aggregate.getDestinationProvinceId())
                .originDepartmentId(aggregate.getOriginDepartmentId())
                .destinationDepartmentId(aggregate.getDestinationDepartmentId())
                .originDepartmentName(aggregate.getOriginDepartmentName())
                .destinationDepartmentName(aggregate.getDestinationDepartmentName())
                .status(aggregate.getStatus())
                .duration(aggregate.getDuration())
                .distance(aggregate.getDistance())
                .createdAt(aggregate.getCreatedAt())
                .createdBy(aggregate.getCreatedBy())
                .updatedAt(aggregate.getUpdatedAt())
                .updatedBy(aggregate.getUpdatedBy())
                .build();
    }

    public RouteStopPlan toStopPlan(RouteStopEntity routeStop) {
        return RouteStopPlan.builder()
                .id(routeStop.getId())
                .routeId(routeStop.getRouteId())
                .creator(routeStop.getCreator())
                .stopOrder(Integer.parseInt(routeStop.getStopOrder()))
                .note(routeStop.getNote())
                .departmentId(routeStop.getDepartmentId())
                .stopName(routeStop.getStopName())
                .stopAddress(routeStop.getStopAddress())
                .stopCity(routeStop.getStopCity())
                .stopLatitude(routeStop.getStopLatitude())
                .stopLongitude(routeStop.getStopLongitude())
                .stayDuration(routeStop.getStayDuration())
                .timeAtDepartment(routeStop.getTimeAtDepartment())
                .createdAt(routeStop.getCreatedAt())
                .createdBy(routeStop.getCreatedBy())
                .build();
    }

    public RouteStopEntity toEntity(RouteStopPlan stopPlan) {
        return RouteStopEntity.builder()
                .id(stopPlan.getId())
                .routeId(stopPlan.getRouteId())
                .creator(stopPlan.getCreator())
                .stopOrder(String.valueOf(stopPlan.getStopOrder()))
                .note(stopPlan.getNote())
                .departmentId(stopPlan.getDepartmentId())
                .stopName(stopPlan.getStopName())
                .stopAddress(stopPlan.getStopAddress())
                .stopCity(stopPlan.getStopCity())
                .stopLatitude(stopPlan.getStopLatitude())
                .stopLongitude(stopPlan.getStopLongitude())
                .stayDuration(stopPlan.getStayDuration())
                .timeAtDepartment(stopPlan.getTimeAtDepartment())
                .createdAt(stopPlan.getCreatedAt())
                .createdBy(stopPlan.getCreatedBy())
                .build();
    }

    public VehicleProfile toVehicleProfile(VehicleEntity vehicle, VehicleTemplateEntity template) {
        return VehicleProfile.builder()
                .id(vehicle.getId())
                .vehiclePlate(vehicle.getVehiclePlate())
                .templateId(template.getId())
                .hasFloor(template.isHasFloor())
                .build();
    }


    public TripAssignmentRecord toAssignmentRecord(TripAssignmentEntity assignment) {
        return TripAssignmentRecord.builder()
                .id(assignment.getId())
                .merchantId(assignment.getMerchantId())
                .tripId(assignment.getTripId())
                .creator(assignment.getCreator())
                .driverId(assignment.getDriverId())
                .vehicleId(assignment.getVehicleId())
                .assignedAt(assignment.getAssignedAt())
                .ticketPrice(assignment.getTicketPrice())
                .unAssignedAt(assignment.getUnAssignedAt())
                .status(assignment.getStatus())
                .createdAt(assignment.getCreatedAt())
                .createdBy(assignment.getCreatedBy())
                .updatedAt(assignment.getUpdatedAt())
                .updatedBy(assignment.getUpdatedBy())
                .build();
    }

    public TripAssignmentEntity toEntity(TripAssignmentRecord record) {
        return TripAssignmentEntity.builder()
                .id(record.getId())
                .merchantId(record.getMerchantId())
                .tripId(record.getTripId())
                .creator(record.getCreator())
                .driverId(record.getDriverId())
                .vehicleId(record.getVehicleId())
                .ticketPrice(record.getTicketPrice())
                .assignedAt(record.getAssignedAt())
                .unAssignedAt(record.getUnAssignedAt())
                .status(record.getStatus())
                .createdAt(record.getCreatedAt())
                .createdBy(record.getCreatedBy())
                .updatedAt(record.getUpdatedAt())
                .updatedBy(record.getUpdatedBy())
                .build();
    }
}
