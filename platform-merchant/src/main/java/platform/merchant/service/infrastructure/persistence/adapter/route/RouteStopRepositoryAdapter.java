package platform.merchant.service.infrastructure.persistence.adapter.route;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import platform.merchant.service.domain.route.model.RouteStopPlan;
import platform.merchant.service.domain.route.port.RouteStopRepositoryPort;
import platform.merchant.service.infrastructure.persistence.jpa.routepoint.entity.RouteStopEntity;
import platform.merchant.service.infrastructure.persistence.jpa.routepoint.repository.RouteStopEntityRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RouteStopRepositoryAdapter implements RouteStopRepositoryPort {

    private final RouteStopEntityRepository routeStopEntityRepository;
    private final RoutePersistenceMapper routePersistenceMapper;

    @Override
    public void saveAll(List<RouteStopPlan> stopPlans) {
        List<RouteStopEntity> entities = stopPlans.stream()
                .map(routePersistenceMapper::toEntity)
                .toList();
        routeStopEntityRepository.saveAll(entities);
    }

    @Override
    public void save(RouteStopPlan routeStopPlan) {
        routeStopEntityRepository.save(routePersistenceMapper.toEntity(routeStopPlan));
    }

    @Override
    public List<RouteStopPlan> findByRouteId(String routeId) {
        List<RouteStopPlan> stopPlans = new ArrayList<>(routeStopEntityRepository.findAllByRouteId(routeId).stream()
                .map(routePersistenceMapper::toStopPlan)
                .toList());
        stopPlans.sort(Comparator.comparingInt(RouteStopPlan::getStopOrder));
        return stopPlans;
    }

    @Override
    public Map<String, List<RouteStopPlan>> findByRouteIds(List<String> routeIds) {
        List<RouteStopPlan> stopPlans = routeStopEntityRepository.findByRouteIdIn(routeIds).stream()
                .map(routePersistenceMapper::toStopPlan)
                .toList();

        return stopPlans.stream()
                .sorted(Comparator.comparingInt(RouteStopPlan::getStopOrder))
                .collect(Collectors.groupingBy(RouteStopPlan::getRouteId));
    }

    @Override
    public Optional<RouteStopPlan> findByRouteIdAndStopOrder(String routeId, String stopOrder) {
        return routeStopEntityRepository.findByRouteIdAndStopOrder(routeId, stopOrder)
                .map(routePersistenceMapper::toStopPlan);
    }

    @Override
    public boolean existsByRouteIdAndStopOrder(String routeId, String stopOrder) {
        return routeStopEntityRepository.existsByRouteIdAndStopOrder(routeId, stopOrder);
    }

    @Override
    public List<RouteStopPlan> search(String keyword, int page, int size) {
        return routeStopEntityRepository.findByStopNameContainingIgnoreCase(keyword, org.springframework.data.domain.PageRequest.of(page, size))
                .stream()
                .map(routePersistenceMapper::toStopPlan)
                .toList();
    }
}

