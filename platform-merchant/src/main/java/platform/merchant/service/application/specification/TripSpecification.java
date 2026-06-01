package platform.merchant.service.application.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import platform.core.common.service.domain.trip.TripStatus;
import platform.merchant.service.domain.trip.model.TripAggregate;
import platform.merchant.service.infrastructure.persistence.jpa.merchant.entity.MerchantEntity;
import platform.merchant.service.infrastructure.persistence.jpa.route.entity.RouteEntity;
import platform.merchant.service.infrastructure.persistence.jpa.routepoint.entity.RouteStopEntity;
import platform.merchant.service.infrastructure.persistence.jpa.trip.entity.TripEntity;

import java.util.List;


@RequiredArgsConstructor
public class TripSpecification {

    public static Specification<TripAggregate> originNameContainsIgnoreCase(String originName) {
        String v = normalize(originName);
        return (root, query, cb) -> {
            if(v.isBlank()) return cb.conjunction();

            Subquery<String> routeSubQuery = query.subquery(String.class);
            Root<RouteEntity> routeEntityRoot = routeSubQuery.from(RouteEntity.class);
            routeSubQuery.select(routeEntityRoot.get("id"))
                    .where(cb.or(
                            cb.like(cb.lower(routeEntityRoot.get("originName")), "%" + v + "%"),
                            cb.like(cb.lower(routeEntityRoot.get("originDepartmentName")), "%" + v + "%")
                    ));

            return getPredicate(v, root, query, cb, routeSubQuery);
        };
    }

    private static Predicate getPredicate(String v, Root<TripAggregate> root, CriteriaQuery<?> query, CriteriaBuilder cb, Subquery<String> routeSubQuery) {
        Subquery<String> stopSubQuery = query.subquery(String.class);
        Root<RouteStopEntity> routeStopRoot = stopSubQuery.from(RouteStopEntity.class);
        stopSubQuery.select(routeStopRoot.get("routeId"))
                .where(cb.like(cb.lower(routeStopRoot.get("stopName")), "%" + v + "%"));

        return cb.or(
                root.get("routeId").in(routeSubQuery),
                root.get("routeId").in(stopSubQuery)
        );
    }

//    public static Specification<TripAggregate> hasOriginProvinceId(String provinceId) {
//        return (root, query, cb) -> {
//            if (provinceId == null || provinceId.isBlank()) return cb.conjunction();
//            Root<RouteEntity> routeRoot = query.from(RouteEntity.class);
//            return cb.and(
//                    cb.equal(root.get("routeId"), routeRoot.get("id")),
//                    cb.equal(routeRoot.get("originProvinceId"), provinceId)
//            );
//        };
//    }

    public static Specification<TripAggregate> destinationNameContainsIgnoreCase(String destinationName) {
        String v = normalize(destinationName);
        return (root, query, cb) -> {
            if(v.isBlank()) return cb.conjunction();
            Subquery<String> routeSubQuery = query.subquery(String.class);
            Root<RouteEntity> routeEntityRoot = routeSubQuery.from(RouteEntity.class);
            routeSubQuery.select(routeEntityRoot.get("id"))
                    .where(cb.or(
                            cb.like(cb.lower(routeEntityRoot.get("destinationName")), "%" + v + "%"),
                            cb.like(cb.lower(routeEntityRoot.get("destinationDepartmentName")), "%" + v + "%")
                    ));
            return getPredicate(v, root, query, cb, routeSubQuery);
        };
    }

//    public static Specification<TripAggregate> hasDestinationProvinceId(String provinceId) {
//        return (root, query, cb) -> {
//            if (provinceId == null || provinceId.isBlank()) return cb.conjunction();
//            Root<RouteEntity> routeRoot = query.from(RouteEntity.class);
//            return cb.and(
//                    cb.equal(root.get("routeId"), routeRoot.get("id")),
//                    cb.equal(routeRoot.get("destinationProvinceId"), provinceId)
//            );
//        };
//    }

    public static Specification<TripAggregate> assignedStatus(TripStatus status) {
        return (root, query, cb) -> {
            if(status == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("status"), status);
        };
    }

    public static Specification<TripAggregate> hasMerchantId(String merchantId) {
        return (root, query, cb) -> {
            if (merchantId == null || merchantId.isBlank()) {
                return cb.conjunction();
            }
            return cb.equal(root.get("merchantId"), merchantId);
        };
    }


    public static Specification<TripAggregate> hasMerchantIds(List<String> merchantIds) {
        return (root, query, cb) -> {
            if (merchantIds == null) {
                return cb.conjunction();
            }
            if (merchantIds.isEmpty()) {
                return cb.disjunction();
            }
            return root.get("merchantId").in(merchantIds);
        };
    }
    private static String normalize(String message) {
        return message == null ? "" : message.trim().toLowerCase();
    }
}
