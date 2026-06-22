package platform.merchant.service.application.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import platform.core.common.service.application.command.common.PagedResult;
import platform.core.common.service.domain.vehicle.VehicleStatus;
import platform.core.common.service.persistence.exception.BusinessException;
import platform.core.common.service.persistence.utils.ApiRequestUtils;
import platform.core.common.service.persistence.utils.ExceptionUtils;
import platform.merchant.service.application.command.vehicle.AddVehicleCommand;
import platform.merchant.service.application.command.vehicle.AddVehicleResult;
import platform.merchant.service.application.command.vehicle.DeleteVehicleCommand;
import platform.merchant.service.application.command.vehicle.DeleteVehicleResult;
import platform.merchant.service.application.command.vehicle.FetchVehicleDetailQuery;
import platform.merchant.service.application.command.vehicle.FetchVehicleDetailResult;
import platform.merchant.service.application.command.vehicle.FetchVehiclesQuery;
import platform.merchant.service.application.command.vehicle.FetchVehiclesResult;
import platform.merchant.service.application.command.vehicle.UpdateVehicleCommand;
import platform.merchant.service.application.command.vehicle.UpdateVehicleResult;
import platform.merchant.service.application.service.VehicleManagementService;
import platform.merchant.service.domain.vehicle.model.VehicleProfile;
import platform.merchant.service.domain.vehicle.model.VehicleTemplate;
import platform.merchant.service.domain.vehicle.port.VehicleProfileRepositoryPort;
import platform.merchant.service.domain.vehicle.port.VehicleTemplateRepositoryPort;
import vn.com.go.routex.identity.security.log.SystemLog;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static platform.core.common.service.persistence.constant.ApplicationConstant.DEFAULT_PAGE_NUMBER;
import static platform.core.common.service.persistence.constant.ApplicationConstant.DEFAULT_PAGE_SIZE;
import static platform.core.common.service.persistence.constant.ErrorConstant.DUPLICATE_ERROR;
import static platform.core.common.service.persistence.constant.ErrorConstant.DUPLICATE_VEHICLE;
import static platform.core.common.service.persistence.constant.ErrorConstant.INVALID_INPUT_ERROR;
import static platform.core.common.service.persistence.constant.ErrorConstant.INVALID_PAGE_NUMBER;
import static platform.core.common.service.persistence.constant.ErrorConstant.INVALID_PAGE_SIZE;
import static platform.core.common.service.persistence.constant.ErrorConstant.RECORD_NOT_FOUND;
import static platform.core.common.service.persistence.constant.ErrorConstant.VEHICLE_NOT_FOUND_BY_ID;
import static platform.core.common.service.persistence.constant.ErrorConstant.VEHICLE_TEMPLATE_NOT_FOUND_BY_ID;


@Service
@RequiredArgsConstructor
public class VehicleManagementServiceImpl implements VehicleManagementService {

    private final VehicleProfileRepositoryPort vehicleProfileRepositoryPort;
    private final VehicleTemplateRepositoryPort vehicleTemplateRepositoryPort;
    private final SystemLog sLog = SystemLog.getLogger(this.getClass());

    @Override
    @Transactional
    public AddVehicleResult addVehicle(AddVehicleCommand command) {
        validateDuplicateVehiclePlate(command.vehiclePlate(), command.vehiclePlate(), command.merchantId(), command);

        VehicleTemplate vehicleTemplate = findTemplateById(
                command.templateId(),
                command.merchantId(),
                command.context().requestId(),
                command.context().requestDateTime(),
                command.context().channel());

        VehicleProfile newVehicle = VehicleProfile.register(
                UUID.randomUUID().toString(),
                command.merchantId(),
                vehicleTemplate.getId(),
                command.creator(),
                command.hasFloor(),
                command.vehiclePlate(),
                OffsetDateTime.now()
        );

        vehicleProfileRepositoryPort.save(newVehicle);
        return toAddVehicleResult(newVehicle, vehicleTemplate);
    }

    @Override
    @Transactional
    public UpdateVehicleResult updateVehicle(UpdateVehicleCommand command) {
        VehicleWithTemplate existing = findVehicleWithTemplate(command.vehicleId(), command.merchantId(), command);

        validateDuplicateVehiclePlate(command.vehiclePlate(), existing.vehicle().getVehiclePlate(), command.merchantId(), command);
        VehicleTemplate updatedTemplate = resolveUpdatedTemplate(command.templateId(), existing.template(), command);

        VehicleProfile updated = existing.vehicle().toBuilder()
                .merchantId(existing.vehicle().getMerchantId())
                .templateId(updatedTemplate.getId())
                .creator(ApiRequestUtils.firstNonBlank(command.creator(), existing.vehicle().getCreator()))
                .vehiclePlate(ApiRequestUtils.firstNonBlank(command.vehiclePlate(), existing.vehicle().getVehiclePlate()))
                .status(command.status() == null ? existing.vehicle().getStatus() : command.status())
                .updatedAt(OffsetDateTime.now())
                .updatedBy(command.creator())
                .build();


        sLog.info("Vehicle Profile: {}", updated);
        vehicleProfileRepositoryPort.save(updated);
        return toUpdateVehicleResult(updated, updatedTemplate);
    }

    @Override
    @Transactional
    public DeleteVehicleResult deleteVehicle(DeleteVehicleCommand command) {
        VehicleWithTemplate existing = findVehicleWithTemplate(command.vehicleId(), command.merchantId(), command);

        VehicleProfile inactive = existing.vehicle().toBuilder()
                .status(VehicleStatus.INACTIVE)
                .updatedAt(OffsetDateTime.now())
                .updatedBy(command.creator())
                .build();
        vehicleProfileRepositoryPort.save(inactive);

        return DeleteVehicleResult.builder()
                .id(inactive.getId())
                .status(inactive.getStatus())
                .build();
    }

    @Override
    public FetchVehiclesResult fetchVehicles(FetchVehiclesQuery query) {
        int pageSize = ApiRequestUtils.parseIntOrDefault(query.pageSize(), DEFAULT_PAGE_SIZE, "pageSize",
                query.context().requestId(), query.context().requestDateTime(), query.context().channel());
        int pageNumber = ApiRequestUtils.parseIntOrDefault(query.pageNumber(), DEFAULT_PAGE_NUMBER, "pageNumber",
                query.context().requestId(), query.context().requestDateTime(), query.context().channel());

        validatePaging(query, pageSize, pageNumber);

        PagedResult<VehicleProfile> page;

        if(query.status() != null) {
             page = vehicleProfileRepositoryPort.fetch(query.merchantId(), query.status(), pageNumber - 1, pageSize);
        } else {
            page = vehicleProfileRepositoryPort.fetch(query.merchantId(), pageNumber - 1, pageSize);
        }
        Map<String, VehicleTemplate> templatesById = vehicleTemplateRepositoryPort.findByIdsIncludingInactive(page.getItems().stream()
                .map(VehicleProfile::getTemplateId)
                .distinct()
                .toList());


        sLog.info("Templates by Ids: {}", templatesById);

        List<FetchVehiclesResult.FetchVehicleItemResult> items = page.getItems().stream()
                .map(vehicle -> toFetchVehicleItemResult(vehicle, templatesById.get(vehicle.getTemplateId())))
                .collect(Collectors.toList());

        return FetchVehiclesResult.builder()
                .items(items)
                .pageNumber(page.getPageNumber() + 1)
                .pageSize(page.getPageSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }

    @Override
    public FetchVehicleDetailResult fetchVehicleDetail(FetchVehicleDetailQuery query) {
        VehicleWithTemplate vehicle = findVehicleWithTemplate(query.vehicleId(), query.merchantId(), query);
        return toFetchVehicleDetailResult(vehicle.vehicle(), vehicle.template());
    }

    private void validatePaging(FetchVehiclesQuery query, int pageSize, int pageNumber) {
        if (pageSize < 1 || pageSize > 100) {
            throw new BusinessException(query.context().requestId(), query.context().requestDateTime(), query.context().channel(),
                    ExceptionUtils.buildResultResponse(INVALID_INPUT_ERROR, INVALID_PAGE_SIZE));
        }
        if (pageNumber < 1) {
            throw new BusinessException(query.context().requestId(), query.context().requestDateTime(), query.context().channel(),
                    ExceptionUtils.buildResultResponse(INVALID_INPUT_ERROR, INVALID_PAGE_NUMBER));
        }
    }

    private void validateDuplicateVehiclePlate(String newVehiclePlate, String currentVehiclePlate, String merchantId, AddVehicleCommand command) {
        if (vehicleProfileRepositoryPort.existsByVehiclePlate(newVehiclePlate, merchantId)) {
            throw duplicateVehiclePlate(command.context().requestId(), command.context().requestDateTime(), command.context().channel(), newVehiclePlate);
        }
    }

    private void validateDuplicateVehiclePlate(String newVehiclePlate, String currentVehiclePlate, String merchantId, UpdateVehicleCommand command) {
        if (newVehiclePlate == null || newVehiclePlate.isBlank()) {
            return;
        }

        String normalizedVehiclePlate = newVehiclePlate.trim();
        if (normalizedVehiclePlate.equals(currentVehiclePlate)) {
            return;
        }

        if (vehicleProfileRepositoryPort.existsByVehiclePlate(normalizedVehiclePlate, merchantId)) {
            throw duplicateVehiclePlate(command.context().requestId(), command.context().requestDateTime(), command.context().channel(), normalizedVehiclePlate);
        }
    }

    private BusinessException duplicateVehiclePlate(String requestId, String requestDateTime, String channel, String vehiclePlate) {
        return new BusinessException(requestId, requestDateTime, channel,
                ExceptionUtils.buildResultResponse(DUPLICATE_ERROR, String.format(DUPLICATE_VEHICLE, vehiclePlate)));
    }

    private VehicleTemplate resolveUpdatedTemplate(String templateId, VehicleTemplate currentTemplate, UpdateVehicleCommand command) {
        if (templateId == null || templateId.isBlank()) {
            return currentTemplate;
        }
        return findTemplateById(templateId.trim(), command.merchantId(), command.context().requestId(), command.context().requestDateTime(), command.context().channel());
    }

    private VehicleWithTemplate findVehicleWithTemplate(String vehicleId, String merchantId, UpdateVehicleCommand command) {
        VehicleProfile vehicle = findVehicleById(vehicleId, merchantId, command.context().requestId(), command.context().requestDateTime(), command.context().channel());
        VehicleTemplate template = findTemplateById(vehicle.getTemplateId(), merchantId, command.context().requestId(), command.context().requestDateTime(), command.context().channel());
        return new VehicleWithTemplate(vehicle, template);
    }

    private VehicleWithTemplate findVehicleWithTemplate(String vehicleId, String merchantId, DeleteVehicleCommand command) {
        VehicleProfile vehicle = findVehicleById(vehicleId, merchantId, command.context().requestId(), command.context().requestDateTime(), command.context().channel());
        VehicleTemplate template = findTemplateById(vehicle.getTemplateId(), merchantId, command.context().requestId(), command.context().requestDateTime(), command.context().channel());
        return new VehicleWithTemplate(vehicle, template);
    }

    private VehicleWithTemplate findVehicleWithTemplate(String vehicleId, String merchantId, FetchVehicleDetailQuery query) {
        VehicleProfile vehicle = findVehicleById(vehicleId, merchantId, query.context().requestId(), query.context().requestDateTime(), query.context().channel());
        VehicleTemplate template = findTemplateByIdIncludingInactive(vehicle.getTemplateId(), merchantId, query.context().requestId(), query.context().requestDateTime(), query.context().channel());
        return new VehicleWithTemplate(vehicle, template);
    }

    private VehicleProfile findVehicleById(String vehicleId, String merchantId, String requestId, String requestDateTime, String channel) {
        return vehicleProfileRepositoryPort.findById(vehicleId, merchantId)
                .orElseThrow(() -> new BusinessException(requestId, requestDateTime, channel,
                        ExceptionUtils.buildResultResponse(RECORD_NOT_FOUND, String.format(VEHICLE_NOT_FOUND_BY_ID, vehicleId))));
    }

    private VehicleTemplate findTemplateById(String templateId, String merchantId, String requestId, String requestDateTime, String channel) {
        return vehicleTemplateRepositoryPort.findById(templateId, merchantId)
                .orElseThrow(() -> new BusinessException(requestId, requestDateTime, channel,
                        ExceptionUtils.buildResultResponse(RECORD_NOT_FOUND, String.format(VEHICLE_TEMPLATE_NOT_FOUND_BY_ID, templateId))));
    }

    private VehicleTemplate findTemplateByIdIncludingInactive(String templateId, String merchantId, String requestId, String requestDateTime, String channel) {
        return vehicleTemplateRepositoryPort.findByIdIncludingInactive(templateId, merchantId)
                .orElseThrow(() -> new BusinessException(requestId, requestDateTime, channel,
                        ExceptionUtils.buildResultResponse(RECORD_NOT_FOUND, String.format(VEHICLE_TEMPLATE_NOT_FOUND_BY_ID, templateId))));
    }

    private AddVehicleResult toAddVehicleResult(VehicleProfile vehicle, VehicleTemplate template) {
        return AddVehicleResult.builder()
                .id(vehicle.getId())
                .templateId(template.getId())
                .creator(vehicle.getCreator())
                .type(template.getType())
                .category(template.getCategory())
                .vehiclePlate(vehicle.getVehiclePlate())
                .seatCapacity(template.getSeatCapacity())
                .manufacturer(template.getManufacturer())
                .status(vehicle.getStatus())
                .build();
    }

    private UpdateVehicleResult toUpdateVehicleResult(VehicleProfile vehicle, VehicleTemplate template) {
        return UpdateVehicleResult.builder()
                .id(vehicle.getId())
                .templateId(template.getId())
                .creator(vehicle.getCreator())
                .category(template.getCategory())
                .type(template.getType())
                .vehiclePlate(vehicle.getVehiclePlate())
                .seatCapacity(template.getSeatCapacity())
                .hasFloor(template.isHasFloor())
                .manufacturer(template.getManufacturer())
                .status(vehicle.getStatus())
                .build();
    }

    private FetchVehiclesResult.FetchVehicleItemResult toFetchVehicleItemResult(VehicleProfile vehicle, VehicleTemplate template) {
        return FetchVehiclesResult.FetchVehicleItemResult.builder()
                .id(vehicle.getId())
                .templateId(vehicle.getTemplateId())
                .creator(vehicle.getCreator())
                .status(vehicle.getStatus())
                .category(template == null ? null : template.getCategory())
                .type(template == null ? null : template.getType())
                .vehiclePlate(vehicle.getVehiclePlate())
                .seatCapacity(template == null ? null : template.getSeatCapacity())
                .hasFloor(template == null ? null : template.isHasFloor())
                .manufacturer(template == null ? null : template.getManufacturer())
                .build();
    }

    private FetchVehicleDetailResult toFetchVehicleDetailResult(VehicleProfile vehicle, VehicleTemplate template) {
        return FetchVehicleDetailResult.builder()
                .id(vehicle.getId())
                .merchantId(vehicle.getMerchantId())
                .templateId(template.getId())
                .creator(vehicle.getCreator())
                .status(vehicle.getStatus())
                .category(template.getCategory())
                .type(template.getType())
                .vehiclePlate(vehicle.getVehiclePlate())
                .seatCapacity(template.getSeatCapacity())
                .hasFloor(template.isHasFloor())
                .manufacturer(template.getManufacturer())
                .build();
    }

    private record VehicleWithTemplate(VehicleProfile vehicle, VehicleTemplate template) {
    }
}
