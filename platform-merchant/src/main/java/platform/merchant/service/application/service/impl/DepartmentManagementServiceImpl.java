package platform.merchant.service.application.service.impl;

import platform.core.common.service.persistence.utils.ExceptionUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import platform.core.common.service.persistence.exception.BusinessException;
import platform.merchant.service.application.command.department.CreateDepartmentCommand;
import platform.merchant.service.application.command.department.CreateDepartmentResult;
import platform.merchant.service.application.command.department.DeleteDepartmentCommand;
import platform.merchant.service.application.command.department.DeleteDepartmentResult;
import platform.merchant.service.application.command.department.FetchDepartmentQuery;
import platform.merchant.service.application.command.department.FetchDepartmentResult;
import platform.merchant.service.application.command.department.GetDepartmentDetailQuery;
import platform.merchant.service.application.command.department.GetDepartmentDetailResult;
import platform.merchant.service.application.command.department.UpdateDepartmentCommand;
import platform.merchant.service.application.command.department.UpdateDepartmentResult;
import platform.merchant.service.application.service.DepartmentManagementService;
import platform.core.common.service.application.command.common.PagedResult;
import platform.merchant.service.domain.department.DepartmentStatus;
import platform.merchant.service.domain.department.model.Department;
import platform.merchant.service.domain.department.port.DepartmentRepositoryPort;
import platform.merchant.service.domain.provinces.model.Province;
import platform.merchant.service.domain.provinces.model.Ward;
import platform.merchant.service.domain.provinces.port.ProvincesRepositoryPort;
import platform.merchant.service.domain.provinces.port.WardRepositoryPort;
import platform.core.common.service.persistence.utils.DateTimeUtils;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static platform.core.common.service.persistence.constant.ErrorConstant.DEPARTMENT_NOT_FOUND;
import static platform.core.common.service.persistence.constant.ErrorConstant.DUPLICATE_DEPARTMENT_MESSAGE;
import static platform.core.common.service.persistence.constant.ErrorConstant.DUPLICATE_ERROR;
import static platform.core.common.service.persistence.constant.ErrorConstant.PROVINCE_NOT_FOUND;
import static platform.core.common.service.persistence.constant.ErrorConstant.RECORD_NOT_FOUND;
import static platform.core.common.service.persistence.constant.ErrorConstant.WARD_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class DepartmentManagementServiceImpl implements DepartmentManagementService {
    private final DepartmentRepositoryPort departmentRepositoryPort;
    private final ProvincesRepositoryPort provincesRepositoryPort;
    private final WardRepositoryPort wardRepositoryPort;

    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int DEFAULT_PAGE_NUMBER = 1;

    @Override
    public CreateDepartmentResult createDepartment(CreateDepartmentCommand command) {
        if(departmentRepositoryPort.existsByName(command.name(), command.merchantId())) {
            throw new BusinessException(command.context().requestId(), command.context().requestDateTime(), command.context().channel(),
                    ExceptionUtils.buildResultResponse(DUPLICATE_ERROR, DUPLICATE_DEPARTMENT_MESSAGE));
        }
        Ward ward = wardRepositoryPort.findById(command.wardId())
                .orElseThrow(() -> new BusinessException(command.context().requestId(), command.context().requestDateTime(), command.context().channel(),
                        ExceptionUtils.buildResultResponse(RECORD_NOT_FOUND, WARD_NOT_FOUND)));

        Province province = provincesRepositoryPort.findById(command.provinceId())
                .orElseThrow(() -> new BusinessException(command.context().requestId(), command.context().requestDateTime(), command.context().channel(),
                        ExceptionUtils.buildResultResponse(RECORD_NOT_FOUND, PROVINCE_NOT_FOUND)));

        Department department = Department.builder()
                .id(UUID.randomUUID().toString())
                .merchantId(command.merchantId())
                .name(command.name())
                .type(command.type())
                .address(command.address())
                .wardId(command.wardId())
                .wardName(ward.getName())
                .provinceId(command.provinceId())
                .provinceName(province.getName())
                .openingTime(command.openingTime())
                .onlineOpeningTime(command.onlineOpeningTime())
                .onlineClosingTime(command.onlineClosingTime())
                .closingTime(command.closingTime())
                .latitude(command.latitude())
                .longitude(command.longitude())
                .status(command.status())
                .createdAt(OffsetDateTime.now())
                .build();

        enrichAdministrativeNames(department);
        departmentRepositoryPort.save(department);

        return CreateDepartmentResult.builder()
                .id(department.getId())
                .name(department.getName())
                .type(department.getType())
                .address(department.getAddress())
                .wardId(department.getWardId())
                .wardName(department.getWardName())
                .provinceId(department.getProvinceId())
                .provinceName(department.getProvinceName())
                .onlineClosingTime(department.getOnlineClosingTime())
                .onlineOpeningTime(department.getOnlineOpeningTime())
                .openingTime(department.getOpeningTime())
                .closingTime(department.getClosingTime())
                .latitude(department.getLatitude())
                .longitude(department.getLongitude())
                .status(department.getStatus())
                .build();
    }

    @Override
    public UpdateDepartmentResult updateDepartment(UpdateDepartmentCommand command) {
        Department existing = departmentRepositoryPort.findById(command.id(), command.merchantId())
                .orElseThrow(() -> new BusinessException(
                        command.context().requestId(),
                        command.context().requestDateTime(),
                        command.context().channel(),
                        ExceptionUtils.buildResultResponse(RECORD_NOT_FOUND, String.format(DEPARTMENT_NOT_FOUND, command.id()))
                ));

        if (command.name() != null && !command.name().isBlank() && !command.name().equalsIgnoreCase(existing.getName())) {
            if (departmentRepositoryPort.existsByName(command.name(), command.merchantId())) {
                throw new BusinessException(command.context().requestId(), command.context().requestDateTime(), command.context().channel(),
                        ExceptionUtils.buildResultResponse(DUPLICATE_ERROR, DUPLICATE_DEPARTMENT_MESSAGE));
            }
        }

        Department updated = Department.builder()
                .id(existing.getId())
                .merchantId(existing.getMerchantId())
                .name(firstNonBlank(command.name(), existing.getName()))
                .type(command.type() != null ? command.type() : existing.getType())
                .address(firstNonBlank(command.address(), existing.getAddress()))
                .wardId(firstNonBlank(command.wardId(), existing.getWardId()))
                .provinceId(firstNonBlank(command.provinceId(), existing.getProvinceId()))
                .openingTime(firstNonBlank(command.openingTime(), existing.getOpeningTime()))
                .closingTime(firstNonBlank(command.closingTime(), existing.getClosingTime()))
                .onlineOpeningTime(firstNonBlank(command.onlineOpeningTime(), existing.getOnlineOpeningTime()))
                .onlineClosingTime(firstNonBlank(command.onlineClosingTime(), existing.getOnlineClosingTime()))
                .latitude(command.latitude() != null ? command.latitude() : existing.getLatitude())
                .longitude(command.longitude() != null ? command.longitude() : existing.getLongitude())
                .status(command.status() != null ? command.status() : existing.getStatus())
                .createdAt(existing.getCreatedAt())
                .createdBy(existing.getCreatedBy())
                .updatedAt(existing.getUpdatedAt())
                .updatedBy(existing.getUpdatedBy())
                .build();

        enrichAdministrativeNames(updated);
        departmentRepositoryPort.save(updated);

        return UpdateDepartmentResult.builder()
                .id(updated.getId())
                .name(updated.getName())
                .type(updated.getType())
                .address(updated.getAddress())
                .wardId(updated.getWardId())
                .wardName(updated.getWardName())
                .provinceId(updated.getProvinceId())
                .provinceName(updated.getProvinceName())
                .openingTime(updated.getOpeningTime())
                .closingTime(updated.getClosingTime())
                .onlineOpeningTime(updated.getOnlineOpeningTime())
                .onlineClosingTime(updated.getOnlineClosingTime())
                .latitude(updated.getLatitude())
                .longitude(updated.getLongitude())
                .status(updated.getStatus())
                .build();
    }

    private void enrichAdministrativeNames(Department department) {
        if (department.getWardId() != null && !department.getWardId().isBlank()) {
            try {
                wardRepositoryPort.findById(department.getWardId())
                        .ifPresent(w -> department.setWardName(w.getName()));
            } catch (NumberFormatException ignored) {}
        }
        if (department.getProvinceId() != null && !department.getProvinceId().isBlank()) {
            try {
                provincesRepositoryPort.findById(department.getProvinceId())
                        .ifPresent(p -> department.setProvinceName(p.getName()));
            } catch (NumberFormatException ignored) {}
        }
    }

    @Override
    public DeleteDepartmentResult deleteDepartment(DeleteDepartmentCommand command) {
        Department existing = departmentRepositoryPort.findById(command.id(), command.merchantId())
                .orElseThrow(() -> new BusinessException(
                        command.context().requestId(),
                        command.context().requestDateTime(),
                        command.context().channel(),
                        ExceptionUtils.buildResultResponse(RECORD_NOT_FOUND, String.format(DEPARTMENT_NOT_FOUND, command.id()))
                ));

        existing.setStatus(DepartmentStatus.CLOSED);

        departmentRepositoryPort.save(existing);

        return DeleteDepartmentResult.builder()
                .id(existing.getId())
                .status(existing.getStatus())
                .build();
    }

    @Override
    public FetchDepartmentResult fetchDepartment(FetchDepartmentQuery query) {
        int pageSize = parseIntOrDefault(query.pageSize(), DEFAULT_PAGE_SIZE, "pageSize",
                query.context().requestId(), query.context().requestDateTime(), query.context().channel());
        int pageNumber = parseIntOrDefault(query.pageNumber(), DEFAULT_PAGE_NUMBER, "pageNumber",
                query.context().requestId(), query.context().requestDateTime(), query.context().channel());

        PagedResult<Department> page;
        if(query.provinceId() != null) {
            page = departmentRepositoryPort.fetch(query.merchantId(), query.provinceId(), pageNumber - 1, pageSize);
        } else {
            page = departmentRepositoryPort.fetch(query.merchantId(), pageNumber - 1, pageSize);
        }
        List<FetchDepartmentResult.FetchDepartmentItemResult> items = page.getItems().stream()
                .map(p -> FetchDepartmentResult.FetchDepartmentItemResult.builder()
                        .id(p.getId())
                        .name(p.getName())
                        .type(p.getType())
                        .address(p.getAddress())
                        .wardId(p.getWardId())
                        .wardName(p.getWardName())
                        .provinceId(p.getProvinceId())
                        .provinceName(p.getProvinceName())
                        .latitude(p.getLatitude())
                        .longitude(p.getLongitude())
                        .openingTime(p.getOpeningTime())
                        .closingTime(p.getClosingTime())
                        .onlineOpeningTime(p.getOnlineOpeningTime())
                        .onlineClosingTime(p.getOnlineClosingTime())
                        .status(p.getStatus())
                        .build())
                .collect(Collectors.toList());

        return FetchDepartmentResult.builder()
                .items(items)
                .pageNumber(page.getPageNumber() + 1)
                .pageSize(page.getPageSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }

    @Override
    public GetDepartmentDetailResult getDepartmentDetail(GetDepartmentDetailQuery query) {
        Department department = departmentRepositoryPort.findById(query.departmentId().trim(), query.merchantId())
                .orElseThrow(() -> new BusinessException(
                        query.context().requestId(),
                        query.context().requestDateTime(),
                        query.context().channel(),
                        ExceptionUtils.buildResultResponse(RECORD_NOT_FOUND, String.format(DEPARTMENT_NOT_FOUND, query.departmentId()))
                ));

        return GetDepartmentDetailResult.builder()
                .id(department.getId())
                .name(department.getName())
                .type(department.getType())
                .address(department.getAddress())
                .wardId(department.getWardId())
                .wardName(department.getWardName())
                .provinceId(department.getProvinceId())
                .provinceName(department.getProvinceName())
                .openingTime(department.getOpeningTime())
                .closingTime(department.getClosingTime())
                .onlineOpeningTime(department.getOnlineOpeningTime())
                .onlineClosingTime(department.getOnlineClosingTime())
                .latitude(department.getLatitude())
                .longitude(department.getLongitude())
                .status(department.getStatus())
                .build();
    }

    private static int parseIntOrDefault(String v, int defaultValue, String field, String requestId, String requestDateTime, String channel) {
        if (v == null || v.isBlank()) return defaultValue;
        return DateTimeUtils.parseIntOrThrow(v, field, requestId, requestDateTime, channel);
    }

    private static String firstNonBlank(String value, String fallback) {
        return (value == null || value.isBlank()) ? fallback : value;
    }
}
