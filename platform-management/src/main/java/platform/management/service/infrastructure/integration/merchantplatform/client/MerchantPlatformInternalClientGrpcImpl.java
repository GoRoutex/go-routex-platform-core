package platform.management.service.infrastructure.integration.merchantplatform.client;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import platform.core.common.service.common.RequestAttributes;
import platform.core.common.service.persistence.constant.ErrorConstant;
import platform.core.common.service.persistence.exception.BusinessException;
import platform.core.common.service.persistence.utils.ExceptionUtils;
import platform.management.service.interfaces.models.merchant.FetchMerchantsResponse;
import platform.management.service.interfaces.models.merchant.UpdateMerchantRequest;
import platform.management.service.interfaces.models.merchant.UpdateMerchantResponse;
import platform.merchant.service.domain.merchant.ApplicationFormStatus;
import platform.management.service.infrastructure.integration.merchantplatform.model.MerchantPlatformFetchMerchantsRequest;
import platform.management.service.infrastructure.integration.merchantplatform.model.MerchantPlatformInternalModels;
import platform.management.service.infrastructure.integration.merchantplatform.model.MerchantPlatformUpdateMerchantRequest;
import platform.core.common.service.api.BaseResponse;
import platform.core.common.service.api.ApiResult;
import platform.merchant.service.domain.merchant.MerchantStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class MerchantPlatformInternalClientGrpcImpl implements MerchantPlatformInternalClient {

    @GrpcClient("merchantService")
    private MerchantAdminGrpcServiceGrpc.MerchantAdminGrpcServiceBlockingStub merchantAdminGrpcServiceStub;

    @Override
    public BaseResponse<MerchantPlatformInternalModels.MerchantData> fetchMerchantDetail(String merchantId) {
        try {
            MerchantDetailRequest request = MerchantDetailRequest.newBuilder()
                    .setMerchantId(merchantId != null ? merchantId : "")
                    .setContext(buildRequestContext())
                    .build();

            MerchantDetailResponse response = merchantAdminGrpcServiceStub.fetchMerchantDetail(request);
            MerchantPlatformInternalModels.MerchantData data = mapMerchantData(response.getMerchant());
            return successResponse(data);
        } catch (StatusRuntimeException ex) {
            handleGrpcException(ex);
            return null;
        }
    }

    @Override
    public BaseResponse<MerchantPlatformInternalModels.MerchantPage> fetchMerchants(int pageNumber, int pageSize) {
        try {
            FetchMerchantsRequest request = FetchMerchantsRequest.newBuilder()
                    .setPageNumber(pageNumber)
                    .setPageSize(pageSize)
                    .setContext(buildRequestContext())
                    .build();

            FetchMerchantsResponse response = merchantAdminGrpcServiceStub.fetchMerchants(request);
            List<MerchantPlatformInternalModels.MerchantData> items = response.getItemsList().stream()
                    .map(this::mapMerchantData)
                    .toList();

            MerchantPlatformInternalModels.MerchantPage page = new MerchantPlatformInternalModels.MerchantPage();
            page.setItems(items);
            page.setPagination(mapPagination(response.getPagination()));
            return successResponse(page);
        } catch (StatusRuntimeException ex) {
            handleGrpcException(ex);
            return null;
        }
    }

    @Override
    public BaseResponse<List<MerchantPlatformInternalModels.MerchantData>> fetchMerchantsByIds(MerchantPlatformFetchMerchantsRequest request) {
        try {
            FetchMerchantsByIdsRequest grpcRequest = FetchMerchantsByIdsRequest.newBuilder()
                    .addAllMerchantIds(request.getMerchantIds() != null ? request.getMerchantIds() : List.of())
                    .setContext(buildRequestContext())
                    .build();

            FetchMerchantsByIdsResponse response = merchantAdminGrpcServiceStub.fetchMerchantsByIds(grpcRequest);
            List<MerchantPlatformInternalModels.MerchantData> dataList = response.getMerchantsList().stream()
                    .map(this::mapMerchantData)
                    .toList();
            return successResponse(dataList);
        } catch (StatusRuntimeException ex) {
            handleGrpcException(ex);
            return null;
        }
    }

    @Override
    public BaseResponse<List<String>> searchMerchantIds(String merchantName) {
        try {
            SearchMerchantIdsRequest request = SearchMerchantIdsRequest.newBuilder()
                    .setMerchantName(merchantName != null ? merchantName : "")
                    .setContext(buildRequestContext())
                    .build();

            SearchMerchantIdsResponse response = merchantAdminGrpcServiceStub.searchMerchantIds(request);
            return successResponse(response.getMerchantIdsList());
        } catch (StatusRuntimeException ex) {
            handleGrpcException(ex);
            return null;
        }
    }

    @Override
    public BaseResponse<MerchantPlatformInternalModels.MerchantData> updateMerchant(MerchantPlatformUpdateMerchantRequest request) {
        try {
            UpdateMerchantRequest grpcRequest = UpdateMerchantRequest.newBuilder()
                    .setMerchantId(request.getMerchantId() != null ? request.getMerchantId() : "")
                    .setCode(request.getCode() != null ? request.getCode() : "")
                    .setSlug(request.getSlug() != null ? request.getSlug() : "")
                    .setDisplayName(request.getDisplayName() != null ? request.getDisplayName() : "")
                    .setLegalName(request.getLegalName() != null ? request.getLegalName() : "")
                    .setTaxCode(request.getTaxCode() != null ? request.getTaxCode() : "")
                    .setBusinessLicenseNumber(request.getBusinessLicenseNumber() != null ? request.getBusinessLicenseNumber() : "")
                    .setBusinessLicenseUrl(request.getBusinessLicenseUrl() != null ? request.getBusinessLicenseUrl() : "")
                    .setPhone(request.getPhone() != null ? request.getPhone() : "")
                    .setEmail(request.getEmail() != null ? request.getEmail() : "")
                    .setLogoUrl(request.getLogoUrl() != null ? request.getLogoUrl() : "")
                    .setDescription(request.getDescription() != null ? request.getDescription() : "")
                    .setAddress(request.getAddress() != null ? request.getAddress() : "")
                    .setWard(request.getWard() != null ? request.getWard() : "")
                    .setProvince(request.getProvince() != null ? request.getProvince() : "")
                    .setCountry(request.getCountry() != null ? request.getCountry() : "")
                    .setPostalCode(request.getPostalCode() != null ? request.getPostalCode() : "")
                    .setRepresentativeName(request.getRepresentativeName() != null ? request.getRepresentativeName() : "")
                    .setContactName(request.getContactName() != null ? request.getContactName() : "")
                    .setContactPhone(request.getContactPhone() != null ? request.getContactPhone() : "")
                    .setContactEmail(request.getContactEmail() != null ? request.getContactEmail() : "")
                    .setOwnerFullName(request.getOwnerFullName() != null ? request.getOwnerFullName() : "")
                    .setOwnerPhone(request.getOwnerPhone() != null ? request.getOwnerPhone() : "")
                    .setOwnerEmail(request.getOwnerEmail() != null ? request.getOwnerEmail() : "")
                    .setBankAccountName(request.getBankAccountName() != null ? request.getBankAccountName() : "")
                    .setBankAccountNumber(request.getBankAccountNumber() != null ? request.getBankAccountNumber() : "")
                    .setBankName(request.getBankName() != null ? request.getBankName() : "")
                    .setBankBranch(request.getBankBranch() != null ? request.getBankBranch() : "")
                    .setCommissionRate(request.getCommissionRate() != null ? request.getCommissionRate().toString() : "")
                    .setStatus(request.getStatus() != null ? request.getStatus().name() : "")
                    .setApprovedAt(request.getApprovedAt() != null ? request.getApprovedAt().toString() : "")
                    .setApprovedBy(request.getApprovedBy() != null ? request.getApprovedBy() : "")
                    .setUpdatedBy(request.getUpdatedBy() != null ? request.getUpdatedBy() : "")
                    .setContext(buildRequestContext())
                    .build();

            UpdateMerchantResponse response = merchantAdminGrpcServiceStub.updateMerchant(grpcRequest);
            MerchantPlatformInternalModels.MerchantData data = mapMerchantData(response.getMerchant());
            return successResponse(data);
        } catch (StatusRuntimeException ex) {
            handleGrpcException(ex);
            return null;
        }
    }

    @Override
    public BaseResponse<MerchantPlatformInternalModels.MerchantApplicationFormPage> fetchApplicationForms(
            ApplicationFormStatus status, int pageNumber, int pageSize) {
        try {
            FetchApplicationFormsRequest request = FetchApplicationFormsRequest.newBuilder()
                    .setStatus(status != null ? status.name() : "")
                    .setPageNumber(pageNumber)
                    .setPageSize(pageSize)
                    .setContext(buildRequestContext())
                    .build();

            FetchApplicationFormsResponse response = merchantAdminGrpcServiceStub.fetchApplicationForms(request);
            List<MerchantPlatformInternalModels.MerchantApplicationFormData> items = response.getItemsList().stream()
                    .map(this::mapApplicationFormData)
                    .toList();

            MerchantPlatformInternalModels.MerchantApplicationFormPage page = new MerchantPlatformInternalModels.MerchantApplicationFormPage();
            page.setItems(items);
            page.setPagination(mapPagination(response.getPagination()));
            return successResponse(page);
        } catch (StatusRuntimeException ex) {
            handleGrpcException(ex);
            return null;
        }
    }

    @Override
    public BaseResponse<MerchantPlatformInternalModels.MerchantApplicationFormData> fetchApplicationFormDetail(String applicationFormId) {
        try {
            FetchApplicationFormDetailRequest request = FetchApplicationFormDetailRequest.newBuilder()
                    .setApplicationFormId(applicationFormId != null ? applicationFormId : "")
                    .setContext(buildRequestContext())
                    .build();

            FetchApplicationFormDetailResponse response = merchantAdminGrpcServiceStub.fetchApplicationFormDetail(request);
            MerchantPlatformInternalModels.MerchantApplicationFormData data = mapApplicationFormData(response.getForm());
            return successResponse(data);
        } catch (StatusRuntimeException ex) {
            handleGrpcException(ex);
            return null;
        }
    }

    private AdminRequestContext buildRequestContext() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String requestId = null;
        String requestDateTime = null;
        String channel = null;
        if (attributes != null) {
            var request = attributes.getRequest();
            requestId = request.getHeader(RequestAttributes.REQUEST_ID);
            requestDateTime = request.getHeader(RequestAttributes.REQUEST_DATE_TIME);
            channel = request.getHeader(RequestAttributes.CHANNEL);
        }
        if (requestId == null || requestId.isBlank()) {
            requestId = UUID.randomUUID().toString();
        }
        if (requestDateTime == null || requestDateTime.isBlank()) {
            requestDateTime = OffsetDateTime.now(ZoneOffset.ofHours(7)).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        }
        if (channel == null || channel.isBlank()) {
            channel = "ONL";
        }
        return AdminRequestContext.newBuilder()
                .setRequestId(requestId)
                .setRequestDateTime(requestDateTime)
                .setChannel(channel)
                .build();
    }

    private <T> BaseResponse<T> successResponse(T data) {
        return BaseResponse.<T>builder()
                .result(ApiResult.builder()
                        .responseCode(ErrorConstant.SUCCESS_CODE)
                        .description("Success")
                        .build())
                .data(data)
                .build();
    }

    private MerchantPlatformInternalModels.MerchantData mapMerchantData(MerchantAdminInfo info) {
        if (info == null || info.getId().isEmpty()) {
            return null;
        }
        MerchantPlatformInternalModels.MerchantData data = new MerchantPlatformInternalModels.MerchantData();
        data.setId(info.getId());
        data.setCode(info.getCode());
        data.setSlug(info.getSlug());
        data.setDisplayName(info.getDisplayName());
        data.setLegalName(info.getLegalName());
        data.setTaxCode(info.getTaxCode());
        data.setBusinessLicenseNumber(info.getBusinessLicenseNumber());
        data.setBusinessLicenseUrl(info.getBusinessLicenseUrl());
        data.setPhone(info.getPhone());
        data.setEmail(info.getEmail());
        data.setLogoUrl(info.getLogoUrl());
        data.setDescription(info.getDescription());
        data.setAddress(info.getAddress());
        data.setWard(info.getWard());
        data.setProvince(info.getProvince());
        data.setCountry(info.getCountry());
        data.setPostalCode(info.getPostalCode());
        data.setRepresentativeName(info.getRepresentativeName());
        data.setContactName(info.getContactName());
        data.setContactPhone(info.getContactPhone());
        data.setContactEmail(info.getContactEmail());
        data.setOwnerFullName(info.getOwnerFullName());
        data.setOwnerPhone(info.getOwnerPhone());
        data.setOwnerEmail(info.getOwnerEmail());
        data.setBankAccountName(info.getBankAccountName());
        data.setBankAccountNumber(info.getBankAccountNumber());
        data.setBankName(info.getBankName());
        data.setBankBranch(info.getBankBranch());
        data.setCommissionRate(info.getCommissionRate().isEmpty() ? BigDecimal.ZERO : new BigDecimal(info.getCommissionRate()));
        data.setStatus(info.getStatus().isEmpty() ? null : MerchantStatus.valueOf(info.getStatus()));
        data.setApprovedAt(info.getApprovedAt().isEmpty() ? null : OffsetDateTime.parse(info.getApprovedAt()));
        data.setApprovedBy(info.getApprovedBy());
        return data;
    }

    private MerchantPlatformInternalModels.MerchantApplicationFormData mapApplicationFormData(MerchantApplicationFormInfo info) {
        if (info == null || info.getId().isEmpty()) {
            return null;
        }
        MerchantPlatformInternalModels.MerchantApplicationFormData data = new MerchantPlatformInternalModels.MerchantApplicationFormData();
        data.setId(info.getId());
        data.setFormCode(info.getFormCode());
        data.setDisplayName(info.getDisplayName());
        data.setLegalName(info.getLegalName());
        data.setTaxCode(info.getTaxCode());
        data.setBusinessLicense(info.getBusinessLicense());
        data.setBusinessLicenseUrl(info.getBusinessLicenseUrl());
        data.setLogoUrl(info.getLogoUrl());
        data.setDescription(info.getDescription());
        data.setSlug(info.getSlug());
        data.setSubmittedBy(info.getSubmittedBy());
        data.setSubmittedAt(info.getSubmittedAt().isEmpty() ? null : OffsetDateTime.parse(info.getSubmittedAt()));
        data.setApprovedBy(info.getApprovedBy());
        data.setApprovedAt(info.getApprovedAt().isEmpty() ? null : OffsetDateTime.parse(info.getApprovedAt()));
        data.setRejectedBy(info.getRejectedBy());
        data.setRejectionReason(info.getRejectionReason());
        data.setCountry(info.getCountry());
        data.setProvince(info.getProvince());
        data.setWard(info.getWard());
        data.setAddress(info.getAddress());
        data.setPostalCode(info.getPostalCode());
        data.setContactName(info.getContactName());
        data.setContactPhone(info.getContactPhone());
        data.setContactEmail(info.getContactEmail());
        data.setOwnerName(info.getOwnerName());
        data.setOwnerFullName(info.getOwnerFullName());
        data.setOwnerPhone(info.getOwnerPhone());
        data.setOwnerEmail(info.getOwnerEmail());
        data.setBankAccountName(info.getBankAccountName());
        data.setBankAccountNumber(info.getBankAccountNumber());
        data.setBankName(info.getBankName());
        data.setBankBranch(info.getBankBranch());
        data.setStatus(info.getStatus().isEmpty() ? null : ApplicationFormStatus.valueOf(info.getStatus()));
        return data;
    }

    private MerchantPlatformInternalModels.Pagination mapPagination(AdminPagination info) {
        if (info == null) {
            return null;
        }
        MerchantPlatformInternalModels.Pagination pagination = new MerchantPlatformInternalModels.Pagination();
        pagination.setPageNumber(info.getPageNumber());
        pagination.setPageSize(info.getPageSize());
        pagination.setTotalElements(info.getTotalElements());
        pagination.setTotalPages(info.getTotalPages());
        return pagination;
    }

    private void handleGrpcException(StatusRuntimeException ex) {
        String errorMsg = ex.getStatus().getDescription();
        if (errorMsg == null) {
            errorMsg = ex.getMessage();
        }
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String requestId = null;
        String requestDateTime = null;
        String channel = null;
        if (attributes != null) {
            var request = attributes.getRequest();
            requestId = request.getHeader(RequestAttributes.REQUEST_ID);
            requestDateTime = request.getHeader(RequestAttributes.REQUEST_DATE_TIME);
            channel = request.getHeader(RequestAttributes.CHANNEL);
        }
        if (requestId == null || requestId.isBlank()) {
            requestId = UUID.randomUUID().toString();
        }
        if (requestDateTime == null || requestDateTime.isBlank()) {
            requestDateTime = OffsetDateTime.now(ZoneOffset.ofHours(7)).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        }
        if (channel == null || channel.isBlank()) {
            channel = "ONL";
        }

        String responseCode = ErrorConstant.SYSTEM_ERROR;
        if (ex.getStatus().getCode() == Status.Code.NOT_FOUND || (errorMsg != null && errorMsg.contains("not found"))) {
            responseCode = ErrorConstant.RECORD_NOT_FOUND;
        }

        throw new BusinessException(
                requestId,
                requestDateTime,
                channel,
                ExceptionUtils.buildResultResponse(responseCode, errorMsg)
        );
    }
}
