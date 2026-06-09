package platform.management.service.interfaces.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import platform.core.common.service.api.BaseRequest;
import platform.core.common.service.persistence.exception.BusinessException;
import platform.core.common.service.persistence.utils.ExceptionUtils;
import platform.management.service.application.services.RecentActivityService;
import platform.management.service.domain.activity.model.RecentActivity;
import platform.management.service.infrastructure.persistence.utils.ApiRequestUtils;
import platform.management.service.infrastructure.persistence.utils.HttpUtils;
import platform.management.service.interfaces.factory.ApiResultFactory;
import platform.management.service.interfaces.models.activity.RecentActivitiesFetchData;
import platform.management.service.interfaces.models.activity.RecentActivitiesFetchResponse;
import platform.management.service.interfaces.models.activity.RecentActivityItem;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

import static platform.core.common.service.persistence.constant.ApiConstant.ADMIN_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.API_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.API_VERSION;
import static platform.core.common.service.persistence.constant.ApiConstant.MERCHANT_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.RECENT_ACTIVITIES;
import static platform.core.common.service.persistence.constant.ErrorConstant.INVALID_INPUT_ERROR;
import static platform.core.common.service.persistence.constant.ErrorConstant.INVALID_PAGE_NUMBER;
import static platform.core.common.service.persistence.constant.ErrorConstant.INVALID_PAGE_SIZE;


@RestController
@RequiredArgsConstructor
@RequestMapping(API_PATH + API_VERSION)
public class RecentActivitiesController {

    private static final String AUDIENCE_ADMIN = "ADMIN";
    private static final String AUDIENCE_MERCHANT = "MERCHANT";
    private static final String SCOPE_MERCHANT = "MERCHANT";

    private final RecentActivityService recentActivityService;
    private final ApiResultFactory apiResultFactory;

    @GetMapping(ADMIN_PATH + RECENT_ACTIVITIES)
    public ResponseEntity<RecentActivitiesFetchResponse> fetchAdminActivities(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) String merchantId,
            @RequestParam(required = false, name = "eventType") List<String> eventTypes,
            @RequestParam(required = false) String severity,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String sourceService,
            @RequestParam(required = false) String entityType,
            @RequestParam(required = false) String entityId,
            @RequestParam(required = false) String actorUserId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime to
    ) {
        BaseRequest baseRequest = ApiRequestUtils.getBaseRequestOrDefault(request);
        requireRole("ADMIN");
        validatePage(baseRequest, pageNumber, pageSize);

        return fetchActivities(
                baseRequest,
                from,
                to,
                AUDIENCE_ADMIN,
                null,
                null,
                merchantId,
                eventTypes,
                severity,
                status,
                sourceService,
                entityType,
                entityId,
                actorUserId,
                keyword,
                pageNumber,
                pageSize
        );
    }

    @GetMapping(MERCHANT_PATH + RECENT_ACTIVITIES)
    public ResponseEntity<RecentActivitiesFetchResponse> fetchMerchantActivities(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false, name = "eventType") List<String> eventTypes,
            @RequestParam(required = false) String severity,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String sourceService,
            @RequestParam(required = false) String entityType,
            @RequestParam(required = false) String entityId,
            @RequestParam(required = false) String actorUserId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime to
    ) {
        BaseRequest baseRequest = ApiRequestUtils.getBaseRequestOrDefault(request);
        requireRole("MERCHANT_OWNER");
        validatePage(baseRequest, pageNumber, pageSize);
        String merchantId = ApiRequestUtils.requireMerchantId(request, baseRequest);

        return fetchActivities(
                baseRequest,
                from,
                to,
                AUDIENCE_MERCHANT,
                SCOPE_MERCHANT,
                merchantId,
                merchantId,
                eventTypes,
                severity,
                status,
                sourceService,
                entityType,
                entityId,
                actorUserId,
                keyword,
                pageNumber,
                pageSize
        );
    }

    private ResponseEntity<RecentActivitiesFetchResponse> fetchActivities(
            BaseRequest baseRequest,
            OffsetDateTime from,
            OffsetDateTime to,
            String audienceType,
            String scopeType,
            String scopeId,
            String merchantId,
            List<String> eventTypes,
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
        Page<RecentActivity> page = recentActivityService.fetch(
                from,
                to,
                audienceType,
                scopeType,
                scopeId,
                merchantId,
                eventTypes != null ? Set.copyOf(eventTypes) : null,
                severity,
                status,
                sourceService,
                entityType,
                entityId,
                actorUserId,
                keyword,
                pageNumber,
                pageSize
        );

        List<RecentActivityItem> items = page.getContent().stream()
                .map(this::toItem)
                .toList();

        RecentActivitiesFetchData data = RecentActivitiesFetchData.builder()
                .items(items)
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .hasNext(page.hasNext())
                .build();

        RecentActivitiesFetchResponse response = RecentActivitiesFetchResponse.builder()
                .result(apiResultFactory.buildSuccess())
                .data(data)
                .build();

        return HttpUtils.buildResponse(baseRequest, response);
    }

    private void validatePage(BaseRequest baseRequest, int pageNumber, int pageSize) {
        if (pageNumber < 0) {
            throw new BusinessException(
                    baseRequest.getRequestId(),
                    baseRequest.getRequestDateTime(),
                    baseRequest.getChannel(),
                    ExceptionUtils.buildResultResponse(INVALID_INPUT_ERROR, INVALID_PAGE_NUMBER)
            );
        }
        if (pageSize < 1 || pageSize > 100) {
            throw new BusinessException(
                    baseRequest.getRequestId(),
                    baseRequest.getRequestDateTime(),
                    baseRequest.getChannel(),
                    ExceptionUtils.buildResultResponse(INVALID_INPUT_ERROR, INVALID_PAGE_SIZE)
            );
        }
    }

    private void requireRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new AccessDeniedException("Access denied");
        } else {
            authentication.getAuthorities();
        }

        boolean hasRole = authentication.getAuthorities().stream()
                .map(authority -> authority.getAuthority() == null ? "" : authority.getAuthority().toUpperCase())
                .anyMatch(authority -> role.equals(authority) || ("ROLE_" + role).equals(authority));
        if (!hasRole) {
            throw new AccessDeniedException("Access denied");
        }
    }

    private RecentActivityItem toItem(RecentActivity entity) {
        return RecentActivityItem.builder()
                .id(entity.getId())
                .eventType(entity.getEventType())
                .aggregateId(entity.getAggregateId())
                .eventKey(entity.getEventKey())
                .occurredAt(entity.getOccurredAt())
                .title(entity.getTitle())
                .message(entity.getMessage())
                .actorUserId(entity.getActorUserId())
                .actorName(entity.getActorName())
                .entityType(entity.getEntityType())
                .entityId(entity.getEntityId())
                .merchantId(entity.getMerchantId())
                .audienceType(entity.getAudienceType())
                .scopeType(entity.getScopeType())
                .scopeId(entity.getScopeId())
                .visibility(entity.getVisibility())
                .severity(entity.getSeverity())
                .status(entity.getStatus())
                .sourceService(entity.getSourceService())
                .correlationId(entity.getCorrelationId())
                .entityDisplayName(entity.getEntityDisplayName())
                .header(entity.getHeader())
                .payload(entity.getPayload())
                .build();
    }
}
