package platform.merchant.service.application.specification;

import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import platform.core.common.service.domain.trip.TripStatus;
import platform.merchant.service.infrastructure.persistence.jpa.merchant.entity.MerchantEntity;
import platform.merchant.service.infrastructure.persistence.jpa.trip.entity.TripEntity;


@RequiredArgsConstructor
public class TripSpecification {

    public static Specification<TripEntity> originNameContainsIgnoreCase(String originName) {
        String v = normalize(originName);
        return (root, query, cb) -> cb.like(cb.lower(root.get("originName")), "%" + v + "%");
    }

    public static Specification<TripEntity> destinationNameContainsIgnoreCase(String destinationName) {
        String v = normalize(destinationName);
        return (root, query, cb) -> cb.like(cb.lower(root.get("destinationName")), "%" + v + "%");
    }

    public static Specification<TripEntity> assignedStatus(TripStatus status) {
        return (root, query, cb) -> {
            if(status == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("status"), status);
        };
    }

    public static Specification<TripEntity> hasMerchantId(String merchantId) {
        return (root, query, cb) -> {
            if (merchantId == null || merchantId.isBlank()) {
                return cb.conjunction();
            }
            return cb.equal(root.get("merchantId"), merchantId);
        };
    }


    public static Specification<TripEntity> hasMerchantName(String merchantName) {
        return (root, query, cb) -> {
            if (merchantName == null || merchantName.isBlank()) {
                return cb.conjunction();
            }
            Subquery<String> subquery = query.subquery(String.class);
            Root<MerchantEntity> merchant = subquery.from(MerchantEntity.class);
            subquery.select(merchant.get("id"))
                    .where(cb.like(cb.lower(merchant.get("name")), "%" + merchantName.toLowerCase() + "%"));
            return root.get("merchantId").in(subquery);
        };
    }
    private static String normalize(String message) {
        return message == null ? "" : message.trim().toLowerCase();
    }
}
