package platform.merchant.service.infrastructure.persistence.adapter.route;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import platform.core.common.service.application.command.common.PagedResult;
import platform.merchant.service.domain.route.RouteStatus;
import platform.merchant.service.domain.route.model.RouteAggregate;
import platform.merchant.service.domain.route.model.RouteStopPlan;
import platform.merchant.service.domain.route.port.RouteAggregateRepositoryPort;
import platform.merchant.service.domain.route.port.RouteStopRepositoryPort;
import platform.merchant.service.infrastructure.persistence.jpa.route.entity.RouteEntity;
import platform.merchant.service.infrastructure.persistence.jpa.route.repository.RouteEntityRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RouteAggregateRepositoryAdapter implements RouteAggregateRepositoryPort {

    private final RouteEntityRepository routeEntityRepository;
    private final RoutePersistenceMapper routePersistenceMapper;
    private final RouteStopRepositoryPort routeStopRepositoryPort;
    @Override
    public Optional<RouteAggregate> findById(String routeId) {
        return routeEntityRepository.findById(routeId)
                .map(routePersistenceMapper::toAggregate);
    }

    @Override
    public Optional<RouteAggregate> findById(String routeId, String merchantId) {
        return routeEntityRepository.findByIdAndMerchantId(routeId, merchantId)
                .map(routePersistenceMapper::toAggregate);
    }

    @Override
    public List<RouteAggregate> findByMerchantId(String merchantId) {
        return routeEntityRepository.findByMerchantId(merchantId).stream()
                .map(routePersistenceMapper::toAggregate)
                .toList();
    }

    @Override
    public void save(RouteAggregate aggregate) {
        routeEntityRepository.save(routePersistenceMapper.toEntity(aggregate));
    }

    @Override
    public PagedResult<RouteAggregate> fetch(String merchantId, RouteStatus status, int pageNumber, int pageSize) {
        Page<RouteEntity> page = routeEntityRepository.findByMerchantIdAndStatus(merchantId, status, PageRequest.of(pageNumber, pageSize));
        return toPagedResult(page);
    }

    @Override
    public Map<String, RouteAggregate> findAllByIdIn(List<String> routeIds) {
        List<RouteEntity> routes = routeEntityRepository.findAllByIdIn(routeIds);
        return routes.stream()
                .map(routePersistenceMapper::toAggregate)
                .collect(Collectors.toMap(
                        RouteAggregate::getId,
                        Function.identity(),
                        BinaryOperator.maxBy(Comparator.comparing(RouteAggregate::getCreatedAt))
                ));
    }

    @Override
    public PagedResult<RouteAggregate> fetch(String merchantId, int pageNumber, int pageSize) {
        Page<RouteEntity> page = routeEntityRepository.findByMerchantId(merchantId, PageRequest.of(pageNumber, pageSize));
        return toPagedResult(page);
    }

    @Override
    public List<RouteAggregate> findByIdIn(List<String> routeIds) {
        return routeEntityRepository.findByIdIn(routeIds)
                .stream()
                .map(routePersistenceMapper::toAggregate)
                .toList();
    }


    private PagedResult<RouteAggregate> toPagedResult(Page<RouteEntity> page) {

        List<RouteEntity> routeEntities = page.getContent();

        List<String> routeIds = routeEntities.stream()
                .map(RouteEntity::getId)
                .toList();

        Map<String, List<RouteStopPlan>> mapRouteStop = routeStopRepositoryPort.findByRouteIds(routeIds);

        return PagedResult.<RouteAggregate>builder()
                .items(page.getContent().stream()
                        .map(p -> {
                            RouteAggregate routeAggregate = routePersistenceMapper.toAggregate(p);
                            List<RouteStopPlan> stopPlans = mapRouteStop.get(p.getId());
                            routeAggregate.setStopPlans(stopPlans);
                            return routeAggregate;
                        })
                        .toList())
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }
}
