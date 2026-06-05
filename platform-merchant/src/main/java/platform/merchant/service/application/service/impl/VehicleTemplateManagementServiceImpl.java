package platform.merchant.service.application.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import platform.core.common.service.application.command.common.PagedResult;
import platform.core.common.service.domain.vehicle.VehicleTemplateStatus;
import platform.core.common.service.persistence.exception.BusinessException;
import platform.core.common.service.persistence.utils.ApiRequestUtils;
import platform.core.common.service.persistence.utils.ExceptionUtils;
import platform.merchant.service.application.command.vehicletemplate.CreateVehicleTemplateCommand;
import platform.merchant.service.application.command.vehicletemplate.CreateVehicleTemplateResult;
import platform.merchant.service.application.command.vehicletemplate.DeleteVehicleTemplateCommand;
import platform.merchant.service.application.command.vehicletemplate.DeleteVehicleTemplateResult;
import platform.merchant.service.application.command.vehicletemplate.FetchVehicleTemplateDetailQuery;
import platform.merchant.service.application.command.vehicletemplate.FetchVehicleTemplateDetailResult;
import platform.merchant.service.application.command.vehicletemplate.FetchVehicleTemplatesQuery;
import platform.merchant.service.application.command.vehicletemplate.FetchVehicleTemplatesResult;
import platform.merchant.service.application.command.vehicletemplate.UpdateVehicleTemplateCommand;
import platform.merchant.service.application.command.vehicletemplate.UpdateVehicleTemplateResult;
import platform.merchant.service.application.service.VehicleTemplateManagementService;
import platform.merchant.service.domain.vehicle.model.VehicleTemplate;
import platform.merchant.service.domain.vehicle.port.VehicleTemplateRepositoryPort;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static platform.core.common.service.persistence.constant.ApplicationConstant.DEFAULT_PAGE_NUMBER;
import static platform.core.common.service.persistence.constant.ApplicationConstant.DEFAULT_PAGE_SIZE;
import static platform.core.common.service.persistence.constant.ErrorConstant.DUPLICATE_ERROR;
import static platform.core.common.service.persistence.constant.ErrorConstant.DUPLICATE_VEHICLE_TEMPLATE_CATEGORY_TYPE;
import static platform.core.common.service.persistence.constant.ErrorConstant.DUPLICATE_VEHICLE_TEMPLATE_CODE;
import static platform.core.common.service.persistence.constant.ErrorConstant.INVALID_INPUT_ERROR;
import static platform.core.common.service.persistence.constant.ErrorConstant.INVALID_PAGE_NUMBER;
import static platform.core.common.service.persistence.constant.ErrorConstant.INVALID_PAGE_SIZE;
import static platform.core.common.service.persistence.constant.ErrorConstant.RECORD_NOT_FOUND;
import static platform.core.common.service.persistence.constant.ErrorConstant.VEHICLE_TEMPLATE_NOT_FOUND_BY_ID;

@Service
@RequiredArgsConstructor
public class VehicleTemplateManagementServiceImpl implements VehicleTemplateManagementService {

    private final VehicleTemplateRepositoryPort vehicleTemplateRepositoryPort;

    @Override
    @Transactional
    public CreateVehicleTemplateResult createVehicleTemplate(CreateVehicleTemplateCommand command) {
        validateTemplateCodeUniqueness(command.code(), command.merchantId(), command);
        validateTemplateCategoryTypeUniqueness(command.category().name(), command.type().name(), command.merchantId(), command);
        validateTicketPrice(command.ticketPrice(), command.context().requestId(), command.context().requestDateTime(), command.context().channel());

        VehicleTemplate template = VehicleTemplate.builder()
                .id(UUID.randomUUID().toString())
                .merchantId(command.merchantId())
                .code(command.code())
                .name(command.name())
                .manufacturer(command.manufacturer())
                .model(command.model())
                .seatCapacity(command.seatCapacity())
                .category(command.category())
                .type(command.type())
                .fuelType(command.fuelType())
                .hasFloor(Boolean.TRUE.equals(command.hasFloor()))
                .ticketPrice(command.ticketPrice())
                .status(command.status() == null ? VehicleTemplateStatus.ACTIVE : command.status())
                .createdAt(OffsetDateTime.now())
                .createdBy(command.creator())
                .build();

        vehicleTemplateRepositoryPort.save(template);
        return toCreateResult(template);
    }

    @Override
    @Transactional
    public UpdateVehicleTemplateResult updateVehicleTemplate(UpdateVehicleTemplateCommand command) {
        VehicleTemplate existing = findTemplate(command.templateId(), command.merchantId(),
                command.context().requestId(), command.context().requestDateTime(), command.context().channel());

        validateUpdatedCode(existing, command);
        validateUpdatedCategoryType(existing, command);
        validateTicketPrice(command.ticketPrice(), command.context().requestId(), command.context().requestDateTime(), command.context().channel());

        VehicleTemplate updated = VehicleTemplate.builder()
                .id(existing.getId())
                .merchantId(existing.getMerchantId())
                .code(ApiRequestUtils.firstNonBlank(command.code(), existing.getCode()))
                .name(ApiRequestUtils.firstNonBlank(command.name(), existing.getName()))
                .manufacturer(ApiRequestUtils.firstNonBlank(command.manufacturer(), existing.getManufacturer()))
                .model(ApiRequestUtils.firstNonBlank(command.model(), existing.getModel()))
                .seatCapacity(command.seatCapacity() == null ? existing.getSeatCapacity() : command.seatCapacity())
                .category(command.category() == null ? existing.getCategory() : command.category())
                .type(command.type() == null ? existing.getType() : command.type())
                .fuelType(command.fuelType() == null ? existing.getFuelType() : command.fuelType())
                .hasFloor(command.hasFloor() == null ? existing.isHasFloor() : command.hasFloor())
                .ticketPrice(command.ticketPrice() == null ? existing.getTicketPrice() : command.ticketPrice())
                .status(command.status() == null ? existing.getStatus() : command.status())
                .createdAt(existing.getCreatedAt())
                .createdBy(existing.getCreatedBy())
                .updatedAt(OffsetDateTime.now())
                .updatedBy(command.creator())
                .build();

        vehicleTemplateRepositoryPort.save(updated);
        return toUpdateResult(updated);
    }

    @Override
    @Transactional
    public DeleteVehicleTemplateResult deleteVehicleTemplate(DeleteVehicleTemplateCommand command) {
        VehicleTemplate existing = findTemplate(command.templateId(), command.merchantId(),
                command.context().requestId(), command.context().requestDateTime(), command.context().channel());

        VehicleTemplate deleted = VehicleTemplate.builder()
                .id(existing.getId())
                .merchantId(existing.getMerchantId())
                .code(existing.getCode())
                .name(existing.getName())
                .manufacturer(existing.getManufacturer())
                .model(existing.getModel())
                .seatCapacity(existing.getSeatCapacity())
                .category(existing.getCategory())
                .type(existing.getType())
                .fuelType(existing.getFuelType())
                .hasFloor(existing.isHasFloor())
                .ticketPrice(existing.getTicketPrice())
                .status(VehicleTemplateStatus.INACTIVE)
                .createdAt(existing.getCreatedAt())
                .createdBy(existing.getCreatedBy())
                .updatedAt(OffsetDateTime.now())
                .updatedBy(command.creator())
                .build();

        vehicleTemplateRepositoryPort.save(deleted);
        return DeleteVehicleTemplateResult.builder()
                .id(deleted.getId())
                .code(deleted.getCode())
                .status(deleted.getStatus())
                .build();
    }

    @Override
    public FetchVehicleTemplatesResult fetchVehicleTemplates(FetchVehicleTemplatesQuery query) {
        int pageSize = ApiRequestUtils.parseIntOrDefault(query.pageSize(), DEFAULT_PAGE_SIZE, "pageSize",
                query.context().requestId(), query.context().requestDateTime(), query.context().channel());
        int pageNumber = ApiRequestUtils.parseIntOrDefault(query.pageNumber(), DEFAULT_PAGE_NUMBER, "pageNumber",
                query.context().requestId(), query.context().requestDateTime(), query.context().channel());

        validatePaging(query, pageSize, pageNumber);

        PagedResult<VehicleTemplate> page = vehicleTemplateRepositoryPort.fetch(
                query.merchantId(),
                query.status(),
                query.category(),
                query.type(),
                pageNumber - 1,
                pageSize);
        List<FetchVehicleTemplatesResult.FetchVehicleTemplateItemResult> items = page.getItems().stream()
                .map(this::toFetchItemResult)
                .toList();

        return FetchVehicleTemplatesResult.builder()
                .items(items)
                .pageNumber(page.getPageNumber() + 1)
                .pageSize(page.getPageSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }

    @Override
    public FetchVehicleTemplateDetailResult fetchVehicleTemplateDetail(FetchVehicleTemplateDetailQuery query) {
        VehicleTemplate template = findTemplate(query.templateId(), query.merchantId(),
                query.context().requestId(), query.context().requestDateTime(), query.context().channel());
        return toDetailResult(template);
    }

    private void validatePaging(FetchVehicleTemplatesQuery query, int pageSize, int pageNumber) {
        if (pageSize < 1 || pageSize > 100) {
            throw new BusinessException(query.context().requestId(), query.context().requestDateTime(), query.context().channel(),
                    ExceptionUtils.buildResultResponse(INVALID_INPUT_ERROR, INVALID_PAGE_SIZE));
        }
        if (pageNumber < 1) {
            throw new BusinessException(query.context().requestId(), query.context().requestDateTime(), query.context().channel(),
                    ExceptionUtils.buildResultResponse(INVALID_INPUT_ERROR, INVALID_PAGE_NUMBER));
        }
    }

    private void validateTemplateCodeUniqueness(String code, String merchantId, CreateVehicleTemplateCommand command) {
        if (vehicleTemplateRepositoryPort.existsByCode(code, merchantId)) {
            throw new BusinessException(command.context().requestId(), command.context().requestDateTime(), command.context().channel(),
                    ExceptionUtils.buildResultResponse(DUPLICATE_ERROR, String.format(DUPLICATE_VEHICLE_TEMPLATE_CODE, code)));
        }
    }

    private void validateTemplateCategoryTypeUniqueness(String category, String type, String merchantId, CreateVehicleTemplateCommand command) {
        if (vehicleTemplateRepositoryPort.existsByCategoryAndType(category, type, merchantId)) {
            throw new BusinessException(command.context().requestId(), command.context().requestDateTime(), command.context().channel(),
                    ExceptionUtils.buildResultResponse(DUPLICATE_ERROR, String.format(DUPLICATE_VEHICLE_TEMPLATE_CATEGORY_TYPE, category, type)));
        }
    }

    private void validateUpdatedCode(VehicleTemplate existing, UpdateVehicleTemplateCommand command) {
        if (command.code() == null || command.code().isBlank() || command.code().trim().equals(existing.getCode())) {
            return;
        }
        if (vehicleTemplateRepositoryPort.existsByCode(command.code().trim(), command.merchantId())) {
            throw new BusinessException(command.context().requestId(), command.context().requestDateTime(), command.context().channel(),
                    ExceptionUtils.buildResultResponse(DUPLICATE_ERROR, String.format(DUPLICATE_VEHICLE_TEMPLATE_CODE, command.code().trim())));
        }
    }

    private void validateUpdatedCategoryType(VehicleTemplate existing, UpdateVehicleTemplateCommand command) {
        String category = command.category() == null ? existing.getCategory().name() : command.category().name();
        String type = command.type() == null ? existing.getType().name() : command.type().name();
        boolean sameCategoryType = existing.getCategory().name().equals(category) && existing.getType().name().equals(type);
        if (sameCategoryType) {
            return;
        }
        if (vehicleTemplateRepositoryPort.existsByCategoryAndType(category, type, command.merchantId())) {
            throw new BusinessException(command.context().requestId(), command.context().requestDateTime(), command.context().channel(),
                    ExceptionUtils.buildResultResponse(DUPLICATE_ERROR, String.format(DUPLICATE_VEHICLE_TEMPLATE_CATEGORY_TYPE, category, type)));
        }
    }

    private VehicleTemplate findTemplate(String templateId, String merchantId, String requestId, String requestDateTime, String channel) {
        return vehicleTemplateRepositoryPort.findById(templateId, merchantId)
                .orElseThrow(() -> new BusinessException(requestId, requestDateTime, channel,
                        ExceptionUtils.buildResultResponse(RECORD_NOT_FOUND, String.format(VEHICLE_TEMPLATE_NOT_FOUND_BY_ID, templateId))));
    }

    private void validateTicketPrice(BigDecimal ticketPrice, String requestId, String requestDateTime, String channel) {
        if (ticketPrice != null && ticketPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException(requestId, requestDateTime, channel,
                    ExceptionUtils.buildResultResponse(INVALID_INPUT_ERROR, "ticketPrice must be >= 0"));
        }
    }

    private CreateVehicleTemplateResult toCreateResult(VehicleTemplate template) {
        return CreateVehicleTemplateResult.builder()
                .id(template.getId())
                .merchantId(template.getMerchantId())
                .code(template.getCode())
                .name(template.getName())
                .manufacturer(template.getManufacturer())
                .model(template.getModel())
                .seatCapacity(template.getSeatCapacity())
                .category(template.getCategory())
                .type(template.getType())
                .fuelType(template.getFuelType())
                .hasFloor(template.isHasFloor())
                .ticketPrice(template.getTicketPrice())
                .status(template.getStatus())
                .build();
    }

    private UpdateVehicleTemplateResult toUpdateResult(VehicleTemplate template) {
        return UpdateVehicleTemplateResult.builder()
                .id(template.getId())
                .merchantId(template.getMerchantId())
                .code(template.getCode())
                .name(template.getName())
                .manufacturer(template.getManufacturer())
                .model(template.getModel())
                .seatCapacity(template.getSeatCapacity())
                .category(template.getCategory())
                .type(template.getType())
                .fuelType(template.getFuelType())
                .hasFloor(template.isHasFloor())
                .ticketPrice(template.getTicketPrice())
                .status(template.getStatus())
                .build();
    }

    private FetchVehicleTemplatesResult.FetchVehicleTemplateItemResult toFetchItemResult(VehicleTemplate template) {
        return FetchVehicleTemplatesResult.FetchVehicleTemplateItemResult.builder()
                .id(template.getId())
                .merchantId(template.getMerchantId())
                .code(template.getCode())
                .name(template.getName())
                .manufacturer(template.getManufacturer())
                .model(template.getModel())
                .seatCapacity(template.getSeatCapacity())
                .category(template.getCategory())
                .type(template.getType())
                .fuelType(template.getFuelType())
                .hasFloor(template.isHasFloor())
                .ticketPrice(template.getTicketPrice())
                .status(template.getStatus())
                .build();
    }

    private FetchVehicleTemplateDetailResult toDetailResult(VehicleTemplate template) {
        return FetchVehicleTemplateDetailResult.builder()
                .id(template.getId())
                .merchantId(template.getMerchantId())
                .code(template.getCode())
                .name(template.getName())
                .manufacturer(template.getManufacturer())
                .model(template.getModel())
                .seatCapacity(template.getSeatCapacity())
                .category(template.getCategory())
                .type(template.getType())
                .fuelType(template.getFuelType())
                .hasFloor(template.isHasFloor())
                .ticketPrice(template.getTicketPrice())
                .status(template.getStatus())
                .build();
    }
}
