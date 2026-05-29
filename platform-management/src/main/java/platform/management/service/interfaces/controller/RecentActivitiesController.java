package platform.management.service.interfaces.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import platform.management.service.application.services.RecentActivityService;
import platform.management.service.domain.activity.model.RecentActivity;
import platform.management.service.infrastructure.persistence.exception.BusinessException;
import platform.management.service.infrastructure.persistence.utils.ApiRequestUtils;
import platform.management.service.infrastructure.persistence.utils.ExceptionUtils;
import platform.management.service.infrastructure.persistence.utils.HttpUtils;
import platform.management.service.interfaces.factory.ApiResultFactory;
import platform.management.service.interfaces.models.activity.RecentActivitiesFetchData;
import platform.management.service.interfaces.models.activity.RecentActivitiesFetchResponse;
import platform.management.service.interfaces.models.activity.RecentActivityItem;
import platform.core.common.service.api.BaseRequest;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

import static platform.management.service.infrastructure.persistence.constant.ErrorConstant.INVALID_INPUT_ERROR;
import static platform.management.service.infrastructure.persistence.constant.ErrorConstant.INVALID_PAGE_NUMBER;
import static platform.management.service.infrastructure.persistence.constant.ErrorConstant.INVALID_PAGE_SIZE;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recent-activities")
public class RecentActivitiesController {

    private final RecentActivityService recentActivityService;
    private final ApiResultFactory apiResultFactory;

    @GetMapping
    public ResponseEntity<RecentActivitiesFetchResponse> fetch(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false, name = "eventType") List<String> eventTypes,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime to
    ) {
        BaseRequest baseRequest = ApiRequestUtils.getBaseRequestOrDefault(request);
        String merchantId = ApiRequestUtils.requireMerchantId(request, baseRequest);

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

        if (merchantId == null || merchantId.isBlank()) {
            throw new BusinessException(
                    baseRequest.getRequestId(),
                    baseRequest.getRequestDateTime(),
                    baseRequest.getChannel(),
                    ExceptionUtils.buildResultResponse(INVALID_INPUT_ERROR, "merchantId is required")
            );
        }

        Page<RecentActivity> page = recentActivityService.fetch(
                from,
                to,
                merchantId,
                eventTypes != null ? Set.copyOf(eventTypes) : null,
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
                .header(entity.getHeader())
                .payload(entity.getPayload())
                .build();
    }
}
