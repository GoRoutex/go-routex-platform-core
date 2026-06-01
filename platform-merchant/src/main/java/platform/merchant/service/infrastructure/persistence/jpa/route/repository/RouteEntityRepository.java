package platform.merchant.service.infrastructure.persistence.jpa.route.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import platform.merchant.service.domain.route.RouteStatus;
import platform.merchant.service.infrastructure.persistence.jpa.route.entity.RouteEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface RouteEntityRepository extends JpaRepository<RouteEntity, String>{

    Optional<RouteEntity> findByIdAndMerchantId(String id, String merchantId);

    Optional<RouteEntity> findByMerchantId(String merchantId);

    Page<RouteEntity> findByMerchantId(String merchantId, Pageable pageable);

    List<RouteEntity> findByIdIn(List<String> routeIds);

    Page<RouteEntity> findByMerchantIdAndStatus(String merchantId, RouteStatus status, Pageable pageable);

    List<RouteEntity> findAllByIdIn(List<String> routeIds);
}