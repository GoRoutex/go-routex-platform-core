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
import platform.management.service.application.services.RecentActivityService;
import platform.management.service.domain.activity.model.RecentActivity;
import platform.management.service.domain.activity.port.RecentActivityRepositoryPort;
import vn.com.go.routex.identity.security.log.SystemLog;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RecentActivityServiceImpl implements RecentActivityService {

    private static final String AUDIENCE_ADMIN = "ADMIN";
    private static final String AUDIENCE_MERCHANT = "MERCHANT";
    private static final String SCOPE_SYSTEM = "SYSTEM";
    private static final String SCOPE_MERCHANT = "MERCHANT";
    private static final String VISIBILITY_ADMIN_ONLY = "ADMIN_ONLY";
    private static final String VISIBILITY_MERCHANT_ONLY = "MERCHANT_ONLY";
    private static final String DEFAULT_SEVERITY = "INFO";
    private static final String DEFAULT_STATUS = "SUCCESS";

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

        String merchantId = asString(firstNonNull(
                header != null ? header.get("merchantId") : null,
                header != null ? header.get("merchant_id") : null,
                data != null ? data.get("merchantId") : null,
                data != null ? data.get("merchant_id") : null
        ));
        entity.setMerchantId(merchantId);

        entity.setAudienceType(upperOrDefault(asString(firstNonNull(
                header != null ? header.get("audienceType") : null,
                header != null ? header.get("audience_type") : null,
                data != null ? data.get("audienceType") : null,
                data != null ? data.get("audience_type") : null
        )), merchantId != null ? AUDIENCE_MERCHANT : AUDIENCE_ADMIN));
        entity.setScopeType(upperOrDefault(asString(firstNonNull(
                header != null ? header.get("scopeType") : null,
                header != null ? header.get("scope_type") : null,
                data != null ? data.get("scopeType") : null,
                data != null ? data.get("scope_type") : null
        )), merchantId != null ? SCOPE_MERCHANT : SCOPE_SYSTEM));
        entity.setScopeId(asString(firstNonNull(
                header != null ? header.get("scopeId") : null,
                header != null ? header.get("scope_id") : null,
                data != null ? data.get("scopeId") : null,
                data != null ? data.get("scope_id") : null,
                merchantId
        )));
        entity.setVisibility(upperOrDefault(asString(firstNonNull(
                header != null ? header.get("visibility") : null,
                data != null ? data.get("visibility") : null
        )), AUDIENCE_ADMIN.equals(entity.getAudienceType()) ? VISIBILITY_ADMIN_ONLY : VISIBILITY_MERCHANT_ONLY));
        entity.setSeverity(upperOrDefault(asString(firstNonNull(
                header != null ? header.get("severity") : null,
                data != null ? data.get("severity") : null
        )), DEFAULT_SEVERITY));
        entity.setStatus(upperOrDefault(asString(firstNonNull(
                header != null ? header.get("status") : null,
                data != null ? data.get("status") : null
        )), DEFAULT_STATUS));
        entity.setSourceService(asString(firstNonNull(
                header != null ? header.get("sourceService") : null,
                header != null ? header.get("source_service") : null,
                data != null ? data.get("sourceService") : null,
                data != null ? data.get("source_service") : null
        )));
        entity.setCorrelationId(asString(firstNonNull(
                header != null ? header.get("correlationId") : null,
                header != null ? header.get("correlation_id") : null,
                data != null ? data.get("correlationId") : null,
                data != null ? data.get("correlation_id") : null
        )));

        if (data != null) {
            entity.setTitle(asString(firstNonNull(data.get("title"), data.get("name"))));
            entity.setMessage(asString(firstNonNull(data.get("message"), data.get("description"), data.get("body"))));
            entity.setActorUserId(asString(firstNonNull(data.get("actorUserId"), data.get("userId"), data.get("createdBy"))));
            entity.setActorName(asString(firstNonNull(data.get("actorName"), data.get("username"), data.get("createdByName"))));
            entity.setEntityType(asString(firstNonNull(data.get("entityType"), data.get("aggregateType"))));
            entity.setEntityId(asString(firstNonNull(data.get("entityId"), data.get("aggregateId"), event.aggregateId())));
            entity.setEntityDisplayName(asString(firstNonNull(data.get("entityDisplayName"), data.get("displayName"), data.get("entityName"))));
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
            String audienceType,
            String scopeType,
            String scopeId,
            String merchantId,
            Set<String> eventTypes,
            String severity,
            String status,
            String sourceService,
            String entityType,
            String entityId,
            String actorUserId,
            String keyword,
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
            if (audienceType != null && !audienceType.isBlank()) {
                predicates.add(cb.equal(root.get("audienceType"), audienceType.toUpperCase()));
            }
            if (scopeType != null && !scopeType.isBlank()) {
                predicates.add(cb.equal(root.get("scopeType"), scopeType.toUpperCase()));
            }
            if (scopeId != null && !scopeId.isBlank()) {
                predicates.add(cb.equal(root.get("scopeId"), scopeId));
            }
            if (merchantId != null && !merchantId.isBlank()) {
                predicates.add(cb.equal(root.get("merchantId"), merchantId));
            }
            if (eventTypes != null && !eventTypes.isEmpty()) {
                predicates.add(root.get("eventType").in(eventTypes));
            }
            if (severity != null && !severity.isBlank()) {
                predicates.add(cb.equal(root.get("severity"), severity.toUpperCase()));
            }
            if (status != null && !status.isBlank()) {
                predicates.add(cb.equal(root.get("status"), status.toUpperCase()));
            }
            if (sourceService != null && !sourceService.isBlank()) {
                predicates.add(cb.equal(root.get("sourceService"), sourceService));
            }
            if (entityType != null && !entityType.isBlank()) {
                predicates.add(cb.equal(root.get("entityType"), entityType));
            }
            if (entityId != null && !entityId.isBlank()) {
                predicates.add(cb.equal(root.get("entityId"), entityId));
            }
            if (actorUserId != null && !actorUserId.isBlank()) {
                predicates.add(cb.equal(root.get("actorUserId"), actorUserId));
            }
            if (keyword != null && !keyword.isBlank()) {
                String pattern = "%" + keyword.trim().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("title")), pattern),
                        cb.like(cb.lower(root.get("message")), pattern),
                        cb.like(cb.lower(root.get("entityDisplayName")), pattern),
                        cb.like(cb.lower(root.get("entityId")), pattern),
                        cb.like(cb.lower(root.get("actorName")), pattern)
                ));
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

    private static String upperOrDefault(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value.trim().toUpperCase();
    }
}
