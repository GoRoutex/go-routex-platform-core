package platform.merchant.service.infrastructure.persistence.jpa.routepoint.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import platform.merchant.service.infrastructure.persistence.jpa.routepoint.entity.RouteStopEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface RouteStopEntityRepository extends JpaRepository<RouteStopEntity, String> {

    RouteStopEntity findByRouteId(String routeId);

    List<RouteStopEntity> findAllByRouteId(String routeId);

    List<RouteStopEntity> findByRouteIdIn(List<String> routeIds);

    Optional<RouteStopEntity> findByRouteIdAndStopOrder(String routeId, String stopOrder);

    boolean existsByRouteIdAndStopOrder(String routeId, String stopOrder);

    List<RouteStopEntity> findByStopNameContainingIgnoreCase(String keyword, org.springframework.data.domain.Pageable pageable);
}

