package platform.merchant.service.application.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import platform.core.common.service.application.command.common.PagedResult;
import platform.core.common.service.domain.vehicle.model.VehicleProfile;
import platform.core.common.service.domain.vehicle.model.VehicleTemplate;
import platform.core.common.service.persistence.exception.BusinessException;
import platform.core.common.service.persistence.utils.ApiRequestUtils;
import platform.core.common.service.persistence.utils.ExceptionUtils;
import platform.merchant.service.application.command.maintenance.CreateMaintenancePlanCommand;
import platform.merchant.service.application.command.maintenance.CreateMaintenancePlanResult;
import platform.merchant.service.application.command.maintenance.DeleteMaintenancePlanCommand;
import platform.merchant.service.application.command.maintenance.DeleteMaintenancePlanResult;
import platform.merchant.service.application.command.maintenance.FetchMaintenancePlanDetailQuery;
import platform.merchant.service.application.command.maintenance.FetchMaintenancePlanDetailResult;
import platform.merchant.service.application.command.maintenance.FetchMaintenancePlansQuery;
import platform.merchant.service.application.command.maintenance.FetchMaintenancePlansResult;
import platform.merchant.service.application.command.maintenance.UpdateMaintenancePlanCommand;
import platform.merchant.service.application.command.maintenance.UpdateMaintenancePlanResult;
import platform.merchant.service.application.service.MaintenancePlanManagementService;
import platform.merchant.service.domain.maintenance.MaintenancePlanStatus;
import platform.merchant.service.domain.maintenance.model.MaintenancePlan;
import platform.merchant.service.domain.maintenance.port.MaintenancePlanRepositoryPort;
import platform.merchant.service.domain.vehicle.port.VehicleProfileRepositoryPort;
import platform.merchant.service.domain.vehicle.port.VehicleTemplateRepositoryPort;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static platform.core.common.service.persistence.constant.ApplicationConstant.DEFAULT_PAGE_NUMBER;
import static platform.core.common.service.persistence.constant.ApplicationConstant.DEFAULT_PAGE_SIZE;
import static platform.core.common.service.persistence.constant.ErrorConstant.DUPLICATE_ERROR;
import static platform.core.common.service.persistence.constant.ErrorConstant.DUPLICATE_MAINTENANCE_PLAN_CODE;
import static platform.core.common.service.persistence.constant.ErrorConstant.INVALID_INPUT_ERROR;
import static platform.core.common.service.persistence.constant.ErrorConstant.INVALID_PAGE_NUMBER;
import static platform.core.common.service.persistence.constant.ErrorConstant.INVALID_PAGE_SIZE;
import static platform.core.common.service.persistence.constant.ErrorConstant.MAINTENANCE_PLAN_NOT_FOUND_BY_ID;
import static platform.core.common.service.persistence.constant.ErrorConstant.RECORD_NOT_FOUND;
import static platform.core.common.service.persistence.constant.ErrorConstant.VEHICLE_NOT_FOUND_BY_ID;
import static platform.core.common.service.persistence.constant.ErrorConstant.VEHICLE_TEMPLATE_NOT_FOUND_BY_ID;

@Service
@RequiredArgsConstructor
public class MaintenancePlanManagementServiceImpl implements MaintenancePlanManagementService {

    private final MaintenancePlanRepositoryPort maintenancePlanRepositoryPort;
    private final VehicleProfileRepositoryPort vehicleProfileRepositoryPort;
    private final VehicleTemplateRepositoryPort vehicleTemplateRepositoryPort;

    @Override
    @Transactional
    public CreateMaintenancePlanResult createMaintenancePlan(CreateMaintenancePlanCommand command) {
        validateVehicleExists(command.vehicleId(), command.merchantId(), command.context().requestId(), command.context().requestDateTime(), command.context().channel());
        validateDuplicateCode(command.code(), command.merchantId(), command.context().requestId(), command.context().requestDateTime(), command.context().channel());
        validateDateRange(command.plannedDate(), command.dueDate(), command.context().requestId(), command.context().requestDateTime(), command.context().channel());

        MaintenancePlan plan = MaintenancePlan.builder()
                .id(UUID.randomUUID().toString())
                .merchantId(command.merchantId())
                .vehicleId(command.vehicleId())
                .code(command.code())
                .title(command.title())
                .description(command.description())
                .type(command.type())
                .status(MaintenancePlanStatus.SCHEDULED)
                .plannedDate(command.plannedDate())
                .dueDate(command.dueDate())
                .currentOdometerKm(command.currentOdometerKm())
                .targetOdometerKm(command.targetOdometerKm())
                .estimatedCost(command.estimatedCost())
                .serviceProvider(command.serviceProvider())
                .note(command.note())
                .createdAt(OffsetDateTime.now())
                .createdBy(command.creator())
                .build();

        maintenancePlanRepositoryPort.save(plan);
        return toCreateResult(plan);
    }

    @Override
    @Transactional
    public UpdateMaintenancePlanResult updateMaintenancePlan(UpdateMaintenancePlanCommand command) {
        MaintenancePlan existing = findMaintenancePlan(command.maintenancePlanId(), command.merchantId(),
                command.context().requestId(), command.context().requestDateTime(), command.context().channel());

        String vehicleId = ApiRequestUtils.firstNonBlank(command.vehicleId(), existing.getVehicleId());
        if (!vehicleId.equals(existing.getVehicleId())) {
            validateVehicleExists(vehicleId, command.merchantId(), command.context().requestId(), command.context().requestDateTime(), command.context().channel());
        }

        validateUpdatedCode(existing, command);
        validateDateRange(
                command.plannedDate() == null ? existing.getPlannedDate() : command.plannedDate(),
                command.dueDate() == null ? existing.getDueDate() : command.dueDate(),
                command.context().requestId(),
                command.context().requestDateTime(),
                command.context().channel());

        MaintenancePlan updated = existing.toBuilder()
                .vehicleId(vehicleId)
                .code(ApiRequestUtils.firstNonBlank(command.code(), existing.getCode()))
                .title(ApiRequestUtils.firstNonBlank(command.title(), existing.getTitle()))
                .description(ApiRequestUtils.firstNonBlank(command.description(), existing.getDescription()))
                .type(command.type() == null ? existing.getType() : command.type())
                .status(command.status() == null ? existing.getStatus() : command.status())
                .plannedDate(command.plannedDate() == null ? existing.getPlannedDate() : command.plannedDate())
                .dueDate(command.dueDate() == null ? existing.getDueDate() : command.dueDate())
                .completedDate(command.completedDate() == null ? existing.getCompletedDate() : command.completedDate())
                .currentOdometerKm(command.currentOdometerKm() == null ? existing.getCurrentOdometerKm() : command.currentOdometerKm())
                .targetOdometerKm(command.targetOdometerKm() == null ? existing.getTargetOdometerKm() : command.targetOdometerKm())
                .estimatedCost(command.estimatedCost() == null ? existing.getEstimatedCost() : command.estimatedCost())
                .actualCost(command.actualCost() == null ? existing.getActualCost() : command.actualCost())
                .serviceProvider(ApiRequestUtils.firstNonBlank(command.serviceProvider(), existing.getServiceProvider()))
                .note(ApiRequestUtils.firstNonBlank(command.note(), existing.getNote()))
                .updatedAt(OffsetDateTime.now())
                .updatedBy(command.creator())
                .build();

        maintenancePlanRepositoryPort.save(updated);
        return toUpdateResult(updated);
    }

    @Override
    @Transactional
    public DeleteMaintenancePlanResult deleteMaintenancePlan(DeleteMaintenancePlanCommand command) {
        MaintenancePlan existing = findMaintenancePlan(command.maintenancePlanId(), command.merchantId(),
                command.context().requestId(), command.context().requestDateTime(), command.context().channel());

        MaintenancePlan cancelled = existing.toBuilder()
                .status(MaintenancePlanStatus.CANCELLED)
                .updatedAt(OffsetDateTime.now())
                .updatedBy(command.creator())
                .build();

        maintenancePlanRepositoryPort.save(cancelled);
        return DeleteMaintenancePlanResult.builder()
                .id(cancelled.getId())
                .code(cancelled.getCode())
                .status(cancelled.getStatus())
                .build();
    }

    @Override
    public FetchMaintenancePlansResult fetchMaintenancePlans(FetchMaintenancePlansQuery query) {
        int pageSize = ApiRequestUtils.parseIntOrDefault(query.pageSize(), DEFAULT_PAGE_SIZE, "pageSize",
                query.context().requestId(), query.context().requestDateTime(), query.context().channel());
        int pageNumber = ApiRequestUtils.parseIntOrDefault(query.pageNumber(), DEFAULT_PAGE_NUMBER, "pageNumber",
                query.context().requestId(), query.context().requestDateTime(), query.context().channel());

        validatePaging(query, pageSize, pageNumber);
        validateDateRange(query.fromPlannedDate(), query.toPlannedDate(), query.context().requestId(), query.context().requestDateTime(), query.context().channel());

        PagedResult<MaintenancePlan> page = maintenancePlanRepositoryPort.fetch(
                query.merchantId(),
                query.vehicleId(),
                query.status(),
                query.type(),
                query.fromPlannedDate(),
                query.toPlannedDate(),
                pageNumber - 1,
                pageSize);

        Map<String, FetchMaintenancePlansResult.MaintenancePlanVehicleResult> vehiclesById = buildVehicleResults(page.getItems(), query.merchantId(),
                query.context().requestId(), query.context().requestDateTime(), query.context().channel());

        List<FetchMaintenancePlansResult.FetchMaintenancePlanItemResult> items = page.getItems().stream()
                .map(plan -> toFetchItemResult(plan, vehiclesById.get(plan.getVehicleId())))
                .toList();

        return FetchMaintenancePlansResult.builder()
                .items(items)
                .pageNumber(page.getPageNumber() + 1)
                .pageSize(page.getPageSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }

    @Override
    public FetchMaintenancePlanDetailResult fetchMaintenancePlanDetail(FetchMaintenancePlanDetailQuery query) {
        MaintenancePlan plan = findMaintenancePlan(query.maintenancePlanId(), query.merchantId(),
                query.context().requestId(), query.context().requestDateTime(), query.context().channel());
        return toDetailResult(plan, findVehicleResult(plan.getVehicleId(), query.merchantId(),
                query.context().requestId(), query.context().requestDateTime(), query.context().channel()));
    }

    private void validatePaging(FetchMaintenancePlansQuery query, int pageSize, int pageNumber) {
        if (pageSize < 1 || pageSize > 100) {
            throw new BusinessException(query.context().requestId(), query.context().requestDateTime(), query.context().channel(),
                    ExceptionUtils.buildResultResponse(INVALID_INPUT_ERROR, INVALID_PAGE_SIZE));
        }
        if (pageNumber < 1) {
            throw new BusinessException(query.context().requestId(), query.context().requestDateTime(), query.context().channel(),
                    ExceptionUtils.buildResultResponse(INVALID_INPUT_ERROR, INVALID_PAGE_NUMBER));
        }
    }

    private void validateVehicleExists(String vehicleId, String merchantId, String requestId, String requestDateTime, String channel) {
        vehicleProfileRepositoryPort.findById(vehicleId, merchantId)
                .orElseThrow(() -> new BusinessException(requestId, requestDateTime, channel,
                        ExceptionUtils.buildResultResponse(RECORD_NOT_FOUND, String.format(VEHICLE_NOT_FOUND_BY_ID, vehicleId))));
    }

    private void validateDuplicateCode(String code, String merchantId, String requestId, String requestDateTime, String channel) {
        if (maintenancePlanRepositoryPort.existsByCode(code, merchantId)) {
            throw new BusinessException(requestId, requestDateTime, channel,
                    ExceptionUtils.buildResultResponse(DUPLICATE_ERROR, String.format(DUPLICATE_MAINTENANCE_PLAN_CODE, code)));
        }
    }

    private void validateUpdatedCode(MaintenancePlan existing, UpdateMaintenancePlanCommand command) {
        if (command.code() == null || command.code().isBlank() || command.code().trim().equals(existing.getCode())) {
            return;
        }
        validateDuplicateCode(command.code().trim(), command.merchantId(), command.context().requestId(), command.context().requestDateTime(), command.context().channel());
    }

    private void validateDateRange(LocalDate plannedDate, LocalDate dueDate, String requestId, String requestDateTime, String channel) {
        if (plannedDate != null && dueDate != null && plannedDate.isAfter(dueDate)) {
            throw new BusinessException(requestId, requestDateTime, channel,
                    ExceptionUtils.buildResultResponse(INVALID_INPUT_ERROR, "plannedDate must be before or equal to dueDate"));
        }
    }

    private MaintenancePlan findMaintenancePlan(String maintenancePlanId, String merchantId, String requestId, String requestDateTime, String channel) {
        return maintenancePlanRepositoryPort.findById(maintenancePlanId, merchantId)
                .orElseThrow(() -> new BusinessException(requestId, requestDateTime, channel,
                        ExceptionUtils.buildResultResponse(RECORD_NOT_FOUND, String.format(MAINTENANCE_PLAN_NOT_FOUND_BY_ID, maintenancePlanId))));
    }

    private CreateMaintenancePlanResult toCreateResult(MaintenancePlan plan) {
        return CreateMaintenancePlanResult.builder()
                .id(plan.getId())
                .merchantId(plan.getMerchantId())
                .vehicleId(plan.getVehicleId())
                .code(plan.getCode())
                .title(plan.getTitle())
                .description(plan.getDescription())
                .type(plan.getType())
                .status(plan.getStatus())
                .plannedDate(plan.getPlannedDate())
                .dueDate(plan.getDueDate())
                .completedDate(plan.getCompletedDate())
                .currentOdometerKm(plan.getCurrentOdometerKm())
                .targetOdometerKm(plan.getTargetOdometerKm())
                .estimatedCost(plan.getEstimatedCost())
                .actualCost(plan.getActualCost())
                .serviceProvider(plan.getServiceProvider())
                .note(plan.getNote())
                .build();
    }

    private UpdateMaintenancePlanResult toUpdateResult(MaintenancePlan plan) {
        return UpdateMaintenancePlanResult.builder()
                .id(plan.getId())
                .merchantId(plan.getMerchantId())
                .vehicleId(plan.getVehicleId())
                .code(plan.getCode())
                .title(plan.getTitle())
                .description(plan.getDescription())
                .type(plan.getType())
                .status(plan.getStatus())
                .plannedDate(plan.getPlannedDate())
                .dueDate(plan.getDueDate())
                .completedDate(plan.getCompletedDate())
                .currentOdometerKm(plan.getCurrentOdometerKm())
                .targetOdometerKm(plan.getTargetOdometerKm())
                .estimatedCost(plan.getEstimatedCost())
                .actualCost(plan.getActualCost())
                .serviceProvider(plan.getServiceProvider())
                .note(plan.getNote())
                .build();
    }

    private FetchMaintenancePlansResult.FetchMaintenancePlanItemResult toFetchItemResult(
            MaintenancePlan plan,
            FetchMaintenancePlansResult.MaintenancePlanVehicleResult vehicle
    ) {
        return FetchMaintenancePlansResult.FetchMaintenancePlanItemResult.builder()
                .id(plan.getId())
                .vehicle(vehicle)
                .code(plan.getCode())
                .title(plan.getTitle())
                .type(plan.getType())
                .status(plan.getStatus())
                .plannedDate(plan.getPlannedDate())
                .dueDate(plan.getDueDate())
                .targetOdometerKm(plan.getTargetOdometerKm())
                .estimatedCost(plan.getEstimatedCost())
                .serviceProvider(plan.getServiceProvider())
                .build();
    }

    private FetchMaintenancePlanDetailResult toDetailResult(
            MaintenancePlan plan,
            FetchMaintenancePlanDetailResult.MaintenancePlanVehicleResult vehicle
    ) {
        return FetchMaintenancePlanDetailResult.builder()
                .id(plan.getId())
                .merchantId(plan.getMerchantId())
                .vehicle(vehicle)
                .code(plan.getCode())
                .title(plan.getTitle())
                .description(plan.getDescription())
                .type(plan.getType())
                .status(plan.getStatus())
                .plannedDate(plan.getPlannedDate())
                .dueDate(plan.getDueDate())
                .completedDate(plan.getCompletedDate())
                .currentOdometerKm(plan.getCurrentOdometerKm())
                .targetOdometerKm(plan.getTargetOdometerKm())
                .estimatedCost(plan.getEstimatedCost())
                .actualCost(plan.getActualCost())
                .serviceProvider(plan.getServiceProvider())
                .note(plan.getNote())
                .build();
    }

    private Map<String, FetchMaintenancePlansResult.MaintenancePlanVehicleResult> buildVehicleResults(
            List<MaintenancePlan> plans,
            String merchantId,
            String requestId,
            String requestDateTime,
            String channel
    ) {
        Map<String, VehicleProfile> vehiclesById = plans.stream()
                .map(plan -> findVehicleProfile(plan.getVehicleId(), merchantId, requestId, requestDateTime, channel))
                .collect(Collectors.toMap(VehicleProfile::getId, Function.identity(), (left, right) -> left));

        Map<String, VehicleTemplate> templatesById = vehicleTemplateRepositoryPort.findByIds(vehiclesById.values().stream()
                .map(VehicleProfile::getTemplateId)
                .distinct()
                .toList());

        return vehiclesById.values().stream()
                .collect(Collectors.toMap(
                        VehicleProfile::getId,
                        vehicle -> toFetchVehicleResult(vehicle,
                                requireTemplate(templatesById, vehicle.getTemplateId(), requestId, requestDateTime, channel))));
    }

    private FetchMaintenancePlanDetailResult.MaintenancePlanVehicleResult findVehicleResult(
            String vehicleId,
            String merchantId,
            String requestId,
            String requestDateTime,
            String channel
    ) {
        VehicleProfile vehicle = findVehicleProfile(vehicleId, merchantId, requestId, requestDateTime, channel);
        VehicleTemplate template = findTemplateById(vehicle.getTemplateId(), merchantId, requestId, requestDateTime, channel);
        return toDetailVehicleResult(vehicle, template);
    }

    private VehicleProfile findVehicleProfile(String vehicleId, String merchantId, String requestId, String requestDateTime, String channel) {
        return vehicleProfileRepositoryPort.findById(vehicleId, merchantId)
                .orElseThrow(() -> new BusinessException(requestId, requestDateTime, channel,
                        ExceptionUtils.buildResultResponse(RECORD_NOT_FOUND, String.format(VEHICLE_NOT_FOUND_BY_ID, vehicleId))));
    }

    private VehicleTemplate findTemplateById(String templateId, String merchantId, String requestId, String requestDateTime, String channel) {
        return vehicleTemplateRepositoryPort.findById(templateId, merchantId)
                .orElseThrow(() -> new BusinessException(requestId, requestDateTime, channel,
                        ExceptionUtils.buildResultResponse(RECORD_NOT_FOUND, String.format(VEHICLE_TEMPLATE_NOT_FOUND_BY_ID, templateId))));
    }

    private VehicleTemplate requireTemplate(
            Map<String, VehicleTemplate> templatesById,
            String templateId,
            String requestId,
            String requestDateTime,
            String channel
    ) {
        VehicleTemplate template = templatesById.get(templateId);
        if (template == null) {
            throw new BusinessException(requestId, requestDateTime, channel,
                    ExceptionUtils.buildResultResponse(RECORD_NOT_FOUND, String.format(VEHICLE_TEMPLATE_NOT_FOUND_BY_ID, templateId)));
        }
        return template;
    }

    private FetchMaintenancePlansResult.MaintenancePlanVehicleResult toFetchVehicleResult(VehicleProfile vehicle, VehicleTemplate template) {
        return FetchMaintenancePlansResult.MaintenancePlanVehicleResult.builder()
                .id(vehicle.getId())
                .templateId(template.getId())
                .status(vehicle.getStatus())
                .category(template.getCategory())
                .type(template.getType())
                .vehiclePlate(vehicle.getVehiclePlate())
                .seatCapacity(template.getSeatCapacity())
                .hasFloor(template.isHasFloor())
                .manufacturer(template.getManufacturer())
                .build();
    }

    private FetchMaintenancePlanDetailResult.MaintenancePlanVehicleResult toDetailVehicleResult(VehicleProfile vehicle, VehicleTemplate template) {
        return FetchMaintenancePlanDetailResult.MaintenancePlanVehicleResult.builder()
                .id(vehicle.getId())
                .templateId(template.getId())
                .status(vehicle.getStatus())
                .category(template.getCategory())
                .type(template.getType())
                .vehiclePlate(vehicle.getVehiclePlate())
                .seatCapacity(template.getSeatCapacity())
                .hasFloor(template.isHasFloor())
                .manufacturer(template.getManufacturer())
                .build();
    }
}
