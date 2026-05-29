package platform.merchant.service.interfaces.mapper;

import org.springframework.stereotype.Component;
import platform.merchant.service.application.command.route.FetchRouteResult;
import platform.merchant.service.application.command.route.RoutePointResult;
import platform.merchant.service.interfaces.model.route.FetchRouteResponse;
import platform.merchant.service.interfaces.model.route.SearchRouteResponse;

@Component
public class RouteResponseMapper {

    public FetchRouteResponse.FetchRouteResponseData toFetchRouteResponseData(FetchRouteResult item) {
        return FetchRouteResponse.FetchRouteResponseData.builder()
                .id(item.id())
                .creator(item.creator())
                .originCode(item.originCode())
                .originName(item.originName())
                .destinationCode(item.destinationCode())
                .destinationName(item.destinationName())
                .originDepartmentId(item.originDepartmentId())
                .originDepartmentName(item.originDepartmentName())
                .destinationDepartmentId(item.destinationDepartmentId())
                .destinationDepartmentName(item.destinationDepartmentName())
                .duration(item.duration())
                .status(item.status())
                .routePoints(item.routePoints() == null ? null : item.routePoints().stream()
                        .map(this::toSearchRoutePoint)
                        .toList())
                .build();
    }
//
//    public FetchRouteResponse.AssignmentInformation toAssignmentInformation(FetchRouteResult.AssignmentRecord record) {
//        return FetchRouteResponse.AssignmentInformation.builder()
//                .vehicleId(record.vehicleId())
//                .vehiclePlate(record.vehiclePlate())
//                .vehicleTemplateName(record.vehicleTemplateName())
//                .driverId(record.driverId())
//                .driverName(record.driverName())
//                .build();
//    }

    public SearchRouteResponse.SearchRoutePoints toSearchRoutePoint(RoutePointResult point) {
        return SearchRouteResponse.SearchRoutePoints.builder()
                .id(point.id())
                .routeId(point.routeId())
                .creator(point.creator())
                .stopOrder(point.stopOrder())
                .note(point.note())
                .departmentId(point.departmentId())
                .stopName(point.stopName())
                .stopAddress(point.stopAddress())
                .stopCity(point.stopCity())
                .stopLatitude(point.stopLatitude())
                .stopLongitude(point.stopLongitude())
                .stayDuration(point.stayDuration())
                .timeAtDepartment(point.timeAtDepartment())
                .createdAt(point.createdAt())
                .createdBy(point.createdBy())
                .build();
    }
}
