package platform.merchant.service.domain.route.port;


import platform.merchant.service.domain.route.model.RouteStopPlan;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface RouteStopRepositoryPort {
    void saveAll(List<RouteStopPlan> stopPlans);

    void save(RouteStopPlan routeStopPlan);

    List<RouteStopPlan> findByRouteId(String routeId);

    Map<String, List<RouteStopPlan>> findByRouteIds(List<String> routeIds);

    Optional<RouteStopPlan> findByRouteIdAndStopOrder(String routeId, String stopOrder);

    boolean existsByRouteIdAndStopOrder(String routeId, String stopOrder);

    List<RouteStopPlan> search(String keyword, int page, int size);
}

