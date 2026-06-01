package platform.management.service.application.services.impl;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import platform.core.common.service.infrastructure.event.DomainEvent;
import vn.com.go.routex.identity.security.log.SystemLog;
import platform.management.service.application.services.RecentActivityService;
import platform.management.service.domain.activity.model.RecentActivity;
import platform.management.service.domain.activity.port.RecentActivityRepositoryPort;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RecentActivityServiceImpl implements RecentActivityService {

    private final RecentActivityRepositoryPort recentActivityRepositoryPort;
    private final SystemLog sLog = SystemLog.getLogger(this.getClass());

    @Override
    @Transactional
    public void record(DomainEvent event) {
        if (event == null || event.eventId() == null || event.eventId().isBlank()) {
            return;
        }

        RecentActivity entity = RecentActivity.builder()
                .id(event.eventId())
                .eventType(event.eventType())
                .aggregateId(event.aggregateId())
                .eventKey(event.eventKey())
                .occurredAt(event.occurredAt() != null ? event.occurredAt() : OffsetDateTime.now())
                .header(event.header())
                .payload(event.payload())
                .build();

        // Best-effort extraction for UI display; publishers can standardize these keys.
        Map<String, Object> data = asMap(event.payload() != null ? event.payload().get("data") : null);
        Map<String, Object> header = event.header();

        entity.setMerchantId(asString(firstNonNull(
                header != null ? header.get("merchantId") : null,
                header != null ? header.get("merchant_id") : null,
                data != null ? data.get("merchantId") : null,
                data != null ? data.get("merchant_id") : null
        )));

        if (data != null) {
            entity.setTitle(asString(firstNonNull(data.get("title"), data.get("name"))));
            entity.setMessage(asString(firstNonNull(data.get("message"), data.get("description"), data.get("body"))));
            entity.setActorUserId(asString(firstNonNull(data.get("actorUserId"), data.get("userId"), data.get("createdBy"))));
            entity.setActorName(asString(firstNonNull(data.get("actorName"), data.get("username"), data.get("createdByName"))));
            entity.setEntityType(asString(firstNonNull(data.get("entityType"), data.get("aggregateType"))));
            entity.setEntityId(asString(firstNonNull(data.get("entityId"), data.get("aggregateId"), event.aggregateId())));
        }

        try {
            recentActivityRepositoryPort.save(entity);
        } catch (DataIntegrityViolationException e) {
            // Duplicate eventId (at-least-once delivery); ignore.
            sLog.warn("[RECENT-ACTIVITY] Duplicate event ignored: eventId={}", event.eventId());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RecentActivity> fetch(
            OffsetDateTime from,
            OffsetDateTime to,
            String merchantId,
            Set<String> eventTypes,
            int pageNumber,
            int pageSize
    ) {
        Specification<RecentActivity> spec = (root, query, cb) -> {
            var predicates = new ArrayList<Predicate>();

            if (from != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("occurredAt"), from));
            }
            if (to != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("occurredAt"), to));
            }
            if (merchantId != null && !merchantId.isBlank()) {
                predicates.add(cb.equal(root.get("merchantId"), merchantId));
            }
            if (eventTypes != null && !eventTypes.isEmpty()) {
                predicates.add(root.get("eventType").in(eventTypes));
            }

            return cb.and(predicates.toArray(Predicate[]::new));
        };

        PageRequest page = PageRequest.of(
                Math.max(0, pageNumber),
                Math.min(Math.max(1, pageSize), 100),
                Sort.by(Sort.Order.desc("occurredAt"), Sort.Order.desc("id"))
        );

        return recentActivityRepositoryPort.findAll(spec, page);
    }

    private static Object firstNonNull(Object... values) {
        if (values == null) return null;
        for (Object v : values) {
            if (v != null) return v;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> asMap(Object v) {
        if (v instanceof Map<?, ?> m) {
            // (Map<?, ?>) -> (Map<String, Object>) is safe for our best-effort access
            return (Map<String, Object>) m;
        }
        return null;
    }

    private static String asString(Object v) {
        if (v == null) return null;
        String s = Objects.toString(v, null);
        return (s != null && !s.isBlank()) ? s : null;
    }
}
