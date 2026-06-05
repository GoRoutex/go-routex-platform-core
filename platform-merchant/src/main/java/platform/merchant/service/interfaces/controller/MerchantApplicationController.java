package platform.merchant.service.interfaces.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import platform.core.common.service.persistence.utils.ExceptionUtils;
import platform.merchant.service.application.command.merchant.AcceptMerchantApplicationCommand;
import platform.merchant.service.application.command.merchant.AcceptMerchantApplicationResult;
import platform.merchant.service.application.command.merchant.RejectMerchantApplicationCommand;
import platform.merchant.service.application.command.merchant.RejectMerchantApplicationResult;
import platform.merchant.service.application.command.merchant.SubmitMerchantApplicationCommand;
import platform.merchant.service.application.command.merchant.SubmitMerchantApplicationResult;
import platform.merchant.service.application.service.MerchantApplicationFormService;
import platform.merchant.service.infrastructure.persistence.utils.HttpUtils;
import platform.merchant.service.interfaces.model.merchant.AcceptMerchantApplicationRequest;
import platform.merchant.service.interfaces.model.merchant.AcceptMerchantApplicationResponse;
import platform.merchant.service.interfaces.model.merchant.RejectMerchantApplicationRequest;
import platform.merchant.service.interfaces.model.merchant.RejectMerchantApplicationResponse;
import platform.merchant.service.interfaces.model.merchant.SubmitMerchantApplicationRequest;
import platform.merchant.service.interfaces.model.merchant.SubmitMerchantApplicationResponse;
import vn.com.go.routex.identity.security.log.SystemLog;

import java.math.BigDecimal;

import static platform.core.common.service.persistence.constant.ApiConstant.ACCEPT_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.ADMIN_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.API_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.API_VERSION;
import static platform.core.common.service.persistence.constant.ApiConstant.APPLICATIONS_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.MERCHANT_SERVICE;
import static platform.core.common.service.persistence.constant.ApiConstant.REJECT_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.SUBMIT_PATH;
import static platform.core.common.service.persistence.constant.ErrorConstant.SUCCESS_CODE;
import static platform.core.common.service.persistence.constant.ErrorConstant.SUCCESS_MESSAGE;

@RestController
@RequiredArgsConstructor
@RequestMapping(API_PATH + API_VERSION + MERCHANT_SERVICE)
public class MerchantApplicationController {

    private final MerchantApplicationFormService merchantApplicationFormService;
    private final SystemLog sLog = SystemLog.getLogger(this.getClass());

    @InitBinder
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.setDisallowedFields("requestId", "requestDateTime", "channel", "data");
    }
    /*
    * Submit application form for Merchant Registration
    * */
    @PostMapping(APPLICATIONS_PATH + SUBMIT_PATH)
    public ResponseEntity<SubmitMerchantApplicationResponse> submitApplication(
            @Valid @RequestBody SubmitMerchantApplicationRequest request
    ) {

        sLog.info("[SUBMIT-FORM] Merchant application form request: {}", request);
        SubmitMerchantApplicationResult result = merchantApplicationFormService.submit(SubmitMerchantApplicationCommand.builder()
                .context(HttpUtils.toContext(request))
                .displayName(request.getData().getDisplayName())
                .legalName(request.getData().getLegalName())
                .taxCode(request.getData().getTaxCode())
                .businessLicense(request.getData().getBusinessLicense())
                .businessLicenseUrl(request.getData().getBusinessLicenseUrl())
                .logoUrl(request.getData().getLogoUrl())
                .address(SubmitMerchantApplicationCommand.Address.builder()
                        .country(request.getData().getAddressInfo().getCountry())
                        .province(request.getData().getAddressInfo().getProvince())
                        .city(request.getData().getAddressInfo().getCity())
                        .postalCode(request.getData().getAddressInfo().getPostalCode())
                        .ward(request.getData().getAddressInfo().getWard())
                        .address(request.getData().getAddressInfo().getAddress())
                        .build()
                )
                .description(request.getData().getDescription())
                .slug(request.getData().getSlug())
                .contact(SubmitMerchantApplicationCommand.Contact.builder()
                        .contactName(request.getData().getContact().getContactName())
                        .contactPhone(request.getData().getContact().getContactPhone())
                        .contactEmail(request.getData().getContact().getContactEmail())
                        .build())
                .bankInfo(SubmitMerchantApplicationCommand.BankInfo.builder()
                        .bankName(request.getData().getBankInfo().getBankName())
                        .bankBranch(request.getData().getBankInfo().getBankBranch())
                        .bankAccountName(request.getData().getBankInfo().getBankAccountName())
                        .bankAccountNumber(request.getData().getBankInfo().getBankAccountNumber())
                        .build())
                .ownerInfo(SubmitMerchantApplicationCommand.OwnerInfo.builder()
                        .ownerName(request.getData().getOwnerInfo().getOwnerName())
                        .ownerFullName(request.getData().getOwnerInfo().getOwnerFullName())
                        .ownerPhone(request.getData().getOwnerInfo().getOwnerPhone())
                        .ownerEmail(request.getData().getOwnerInfo().getOwnerEmail())
                        .build())
                .build());

        SubmitMerchantApplicationResponse response = SubmitMerchantApplicationResponse.builder()
                .result(ExceptionUtils.buildResultResponse(SUCCESS_CODE, SUCCESS_MESSAGE))
                .data(SubmitMerchantApplicationResponse.SubmitMerchantApplicationResponseData.builder()
                        .applicationId(result.applicationId())
                        .formCode(result.formCode())
                        .displayName(result.displayName())
                        .legalName(result.legalName())
                        .status(result.status())
                        .submittedAt(result.submittedAt())
                        .build())
                .build();

        return HttpUtils.buildResponse(request, response);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(ADMIN_PATH + APPLICATIONS_PATH + ACCEPT_PATH)
    public ResponseEntity<AcceptMerchantApplicationResponse> acceptApplication(
            @Valid @RequestBody AcceptMerchantApplicationRequest request
    ) {
        AcceptMerchantApplicationResult result = merchantApplicationFormService.accept(AcceptMerchantApplicationCommand.builder()
                .context(HttpUtils.toContext(request))
                .applicationFormId(request.getData().getApplicationFormId())
                .approvedBy(request.getData().getApprovedBy())
                .commission(new BigDecimal(request.getData().getCommission()))
                .build());

        AcceptMerchantApplicationResponse response = AcceptMerchantApplicationResponse.builder()
                .result(ExceptionUtils.buildResultResponse(SUCCESS_CODE, SUCCESS_MESSAGE))
                .data(AcceptMerchantApplicationResponse.AcceptMerchantApplicationResponseData.builder()
                        .applicationId(result.applicationId())
                        .formCode(result.formCode())
                        .merchantId(result.merchantId())
                        .merchantCode(result.merchantCode())
                        .merchantName(result.merchantName())
                        .status(result.status())
                        .approvedBy(result.approvedBy())
                        .approvedAt(result.approvedAt())
                        .build())
                .build();

        return HttpUtils.buildResponse(request, response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(ADMIN_PATH + APPLICATIONS_PATH + REJECT_PATH)
    public ResponseEntity<RejectMerchantApplicationResponse> rejectApplication(
            @Valid @RequestBody RejectMerchantApplicationRequest request
    ) {
        RejectMerchantApplicationResult result = merchantApplicationFormService.reject(RejectMerchantApplicationCommand.builder()
                .context(HttpUtils.toContext(request))
                .applicationFormId(request.getData().getApplicationFormId())
                .rejectedBy(request.getData().getRejectedBy())
                .rejectionReason(request.getData().getRejectionReason())
                .build());

        RejectMerchantApplicationResponse response = RejectMerchantApplicationResponse.builder()
                .result(ExceptionUtils.buildResultResponse(SUCCESS_CODE, SUCCESS_MESSAGE))
                .data(RejectMerchantApplicationResponse.RejectMerchantApplicationResponseData.builder()
                        .applicationId(result.applicationId())
                        .formCode(result.formCode())
                        .status(result.status())
                        .rejectedBy(result.rejectedBy())
                        .rejectionReason(result.rejectionReason())
                        .build())
                .build();

        return HttpUtils.buildResponse(request, response);
    }

}
