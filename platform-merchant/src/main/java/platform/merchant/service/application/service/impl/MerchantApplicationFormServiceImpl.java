package platform.merchant.service.application.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import platform.core.common.service.common.RequestContext;
import platform.core.common.service.persistence.exception.BusinessException;
import platform.core.common.service.persistence.utils.ExceptionUtils;
import platform.merchant.service.application.command.merchant.AcceptMerchantApplicationCommand;
import platform.merchant.service.application.command.merchant.AcceptMerchantApplicationResult;
import platform.merchant.service.application.command.merchant.RejectMerchantApplicationCommand;
import platform.merchant.service.application.command.merchant.RejectMerchantApplicationResult;
import platform.merchant.service.application.command.merchant.SubmitMerchantApplicationCommand;
import platform.merchant.service.application.command.merchant.SubmitMerchantApplicationResult;
import platform.merchant.service.application.service.MerchantApplicationFormService;
import platform.merchant.service.domain.authorities.model.RoleAggregate;
import platform.merchant.service.domain.authorities.model.UserAccountReference;
import platform.merchant.service.domain.authorities.model.UserRoleAssignment;
import platform.merchant.service.domain.authorities.port.RoleRepositoryPort;
import platform.merchant.service.domain.authorities.port.UserAccountLookupPort;
import platform.merchant.service.domain.authorities.port.UserRoleAssignmentRepositoryPort;
import platform.merchant.service.domain.merchant.ApplicationFormStatus;
import platform.merchant.service.domain.merchant.MerchantStatus;
import platform.merchant.service.domain.merchant.model.Merchant;
import platform.merchant.service.domain.merchant.model.MerchantApplicationForm;
import platform.merchant.service.domain.merchant.model.MerchantUser;
import platform.merchant.service.domain.merchant.port.MerchantApplicationFormRepositoryPort;
import platform.merchant.service.domain.merchant.port.MerchantRepositoryPort;
import platform.merchant.service.domain.merchant.port.MerchantUserRepositoryPort;

import java.time.OffsetDateTime;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static platform.core.common.service.persistence.constant.ApplicationConstant.DEFAULT_MERCHANT_COMMISSION_RATE;
import static platform.core.common.service.persistence.constant.ApplicationConstant.DEFAULT_PAGE_NUMBER;
import static platform.core.common.service.persistence.constant.ApplicationConstant.DEFAULT_PAGE_SIZE;
import static platform.core.common.service.persistence.constant.ErrorConstant.DUPLICATE_ERROR;
import static platform.core.common.service.persistence.constant.ErrorConstant.INVALID_INPUT_ERROR;
import static platform.core.common.service.persistence.constant.ErrorConstant.INVALID_PAGE_NUMBER;
import static platform.core.common.service.persistence.constant.ErrorConstant.INVALID_PAGE_SIZE;
import static platform.core.common.service.persistence.constant.ErrorConstant.RECORD_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class MerchantApplicationFormServiceImpl implements MerchantApplicationFormService {

    private static final String MERCHANT_OWNER_ROLE_CODE = "MERCHANT_OWNER";

    private final MerchantApplicationFormRepositoryPort merchantApplicationFormRepositoryPort;
    private final MerchantRepositoryPort merchantRepositoryPort;
    private final MerchantUserRepositoryPort merchantUserRepositoryPort;
    private final UserAccountLookupPort userAccountLookupPort;
    private final RoleRepositoryPort roleRepositoryPort;
    private final UserRoleAssignmentRepositoryPort userRoleAssignmentRepositoryPort;

    @Override
    @Transactional
    public SubmitMerchantApplicationResult submit(SubmitMerchantApplicationCommand command) {
        MerchantApplicationForm applicationForm = MerchantApplicationForm.submit(
                UUID.randomUUID().toString(),
                merchantApplicationFormRepositoryPort.generateFormCode(),
                command.displayName(),
                command.legalName(),
                command.taxCode(),
                command.businessLicense(),
                command.businessLicenseUrl(),
                command.logoUrl(),
                command.address().country(),
                command.address().province(),
                command.address().ward(),
                command.address().address(),
                command.address().postalCode(),
                command.description(),
                resolveSlug(command),
                command.contact().contactName(),
                command.contact().contactPhone(),
                command.contact().contactEmail(),
                command.bankInfo().bankName(),
                command.bankInfo().bankBranch(),
                command.bankInfo().bankAccountName(),
                command.bankInfo().bankAccountNumber(),
                command.ownerInfo().ownerName(),
                command.ownerInfo().ownerFullName(),
                command.ownerInfo().ownerPhone(),
                command.ownerInfo().ownerEmail()
        );

        MerchantApplicationForm savedApplicationForm = merchantApplicationFormRepositoryPort.save(applicationForm);

        return SubmitMerchantApplicationResult.builder()
                .applicationId(savedApplicationForm.getId())
                .formCode(savedApplicationForm.getFormCode())
                .displayName(savedApplicationForm.getDisplayName())
                .legalName(savedApplicationForm.getLegalName())
                .status(savedApplicationForm.getStatus().name())
                .submittedAt(savedApplicationForm.getSubmittedAt())
                .build();
    }

    @Override
    @Transactional
    public AcceptMerchantApplicationResult accept(AcceptMerchantApplicationCommand command) {
        MerchantApplicationForm applicationForm = merchantApplicationFormRepositoryPort.findById(command.applicationFormId())
                .orElseThrow(() -> notFound(command.context()));

        validatePendingApplication(applicationForm, command.context());
        OffsetDateTime approvedAt = OffsetDateTime.now();

        Merchant merchant = Merchant.builder()
                .id(UUID.randomUUID().toString())
                .code(merchantRepositoryPort.generateMerchantCode())
                .displayName(applicationForm.getDisplayName())
                .legalName(applicationForm.getLegalName())
                .slug(applicationForm.getSlug())
                .businessLicenseUrl(applicationForm.getBusinessLicenseUrl())
                .businessLicenseNumber(applicationForm.getBusinessLicense())
                .taxCode(applicationForm.getTaxCode())
                .phone(applicationForm.getContact() == null ? null : applicationForm.getContact().getContactPhone())
                .email(applicationForm.getContact() == null ? null : applicationForm.getContact().getContactEmail())
                .logoUrl(applicationForm.getLogoUrl())
                .description(applicationForm.getDescription())
                .address(buildMerchantAddress(applicationForm))
                .ward(applicationForm.getWard())
                .province(applicationForm.getProvince())
                .country(applicationForm.getCountry())
                .postalCode(applicationForm.getPostalCode())
                .contactName(applicationForm.getContact().getContactName())
                .contactPhone(applicationForm.getContact().getContactPhone())
                .contactEmail(applicationForm.getContact().getContactEmail())
                .ownerEmail(applicationForm.getOwnerInfo().getOwnerEmail())
                .ownerFullName(applicationForm.getOwnerInfo().getOwnerFullName())
                .ownerPhone(applicationForm.getOwnerInfo().getOwnerPhone())
                .bankAccountName(applicationForm.getBankInfo().getBankAccountName())
                .bankName(applicationForm.getBankInfo().getBankName())
                .bankBranch(applicationForm.getBankInfo().getBankBranch())
                .bankAccountNumber(applicationForm.getBankInfo().getBankAccountNumber())
                .representativeName(applicationForm.getOwnerInfo().getOwnerFullName())
                .commissionRate(command.commission() != null ? command.commission() : DEFAULT_MERCHANT_COMMISSION_RATE)
                .status(MerchantStatus.ACTIVE)
                .approvedAt(approvedAt)
                .createdAt(approvedAt)
                .approvedBy(command.approvedBy())
                .createdBy(command.approvedBy())
                .updatedAt(approvedAt)
                .updatedBy(command.approvedBy())
                .build();

        Merchant savedMerchant = merchantRepositoryPort.save(merchant);

        UserAccountReference submittedUser = findSubmittedUserAccount(
                applicationForm.getSubmittedBy(),
                command.context()
        );

        createMerchantMembership(savedMerchant.getId(), submittedUser.id(), command.approvedBy(), command.context());
        assignMerchantOwnerRole(submittedUser, command.context());

        applicationForm.approve(command.approvedBy(), approvedAt);
        MerchantApplicationForm savedApplicationForm = merchantApplicationFormRepositoryPort.save(applicationForm);

        return AcceptMerchantApplicationResult.builder()
                .applicationId(savedApplicationForm.getId())
                .formCode(savedApplicationForm.getFormCode())
                .merchantId(savedMerchant.getId())
                .merchantCode(savedMerchant.getCode())
                .merchantName(savedMerchant.getDisplayName())
                .status(savedApplicationForm.getStatus().name())
                .approvedBy(savedApplicationForm.getApprovedBy())
                .approvedAt(savedApplicationForm.getApprovedAt())
                .build();
    }

    @Override
    @Transactional
    public RejectMerchantApplicationResult reject(RejectMerchantApplicationCommand command) {
        MerchantApplicationForm applicationForm = merchantApplicationFormRepositoryPort.findById(command.applicationFormId())
                .orElseThrow(() -> notFound(command.context()));

        validatePendingApplication(applicationForm, command.context());

        if (command.rejectionReason() == null || command.rejectionReason().isBlank()) {
            throw new BusinessException(
                    command.context().requestId(),
                    command.context().requestDateTime(),
                    command.context().channel(),
                    ExceptionUtils.buildResultResponse(INVALID_INPUT_ERROR, "rejectionReason is required")
            );
        }

        applicationForm.reject(command.rejectedBy(), command.rejectionReason().trim(), OffsetDateTime.now());
        MerchantApplicationForm savedApplicationForm = merchantApplicationFormRepositoryPort.save(applicationForm);

        return RejectMerchantApplicationResult.builder()
                .applicationId(savedApplicationForm.getId())
                .formCode(savedApplicationForm.getFormCode())
                .status(savedApplicationForm.getStatus().name())
                .rejectedBy(savedApplicationForm.getRejectedBy())
                .rejectionReason(savedApplicationForm.getRejectionReason())
                .build();
    }

    private String resolveSlug(SubmitMerchantApplicationCommand command) {
        if (command.slug() != null && !command.slug().isBlank()) {
            return command.slug().trim();
        }
        return command.displayName()
                .trim()
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
    }
    private void validatePendingApplication(MerchantApplicationForm applicationForm, RequestContext context) {
        if (applicationForm.getStatus() != ApplicationFormStatus.SUBMITTED) {
            throw new BusinessException(
                    context.requestId(),
                    context.requestDateTime(),
                    context.channel(),
                    ExceptionUtils.buildResultResponse(DUPLICATE_ERROR, "Merchant application form has already been reviewed")
            );
        }
    }

    private String buildMerchantAddress(MerchantApplicationForm applicationForm) {
        return Stream.of(
                        applicationForm.getAddress(),
                        applicationForm.getWard(),
                        applicationForm.getProvince(),
                        applicationForm.getCountry(),
                        applicationForm.getPostalCode()
                )
                .filter(value -> value != null && !value.isBlank())
                .map(String::trim)
                .collect(Collectors.joining(", "));
    }

    private BusinessException notFound(RequestContext context) {
        return new BusinessException(
                context.requestId(),
                context.requestDateTime(),
                context.channel(),
                ExceptionUtils.buildResultResponse(RECORD_NOT_FOUND, "Merchant application form not found")
        );
    }

    private UserAccountReference findSubmittedUserAccount(String submittedBy, RequestContext context) {
        return userAccountLookupPort.findByEmail(submittedBy)
                .orElseThrow(() -> new BusinessException(
                        context.requestId(),
                        context.requestDateTime(),
                        context.channel(),
                        ExceptionUtils.buildResultResponse(RECORD_NOT_FOUND, "Submitted user account not found")
                ));
    }

    private void createMerchantMembership(String merchantId, String userId, String actor, RequestContext context) {
        merchantUserRepositoryPort.findByUserId(userId)
                .ifPresent(existing -> {
                    throw new BusinessException(
                            context.requestId(),
                            context.requestDateTime(),
                            context.channel(),
                            ExceptionUtils.buildResultResponse(DUPLICATE_ERROR, "User already belongs to a merchant")
                    );
                });

        merchantUserRepositoryPort.save(
                MerchantUser.assign(
                        UUID.randomUUID().toString(),
                        merchantId,
                        userId,
                        MERCHANT_OWNER_ROLE_CODE,
                        actor,
                        OffsetDateTime.now()
                )
        );
    }

    private void assignMerchantOwnerRole(UserAccountReference userAccount, RequestContext context) {

        RoleAggregate merchantOwnerRole = roleRepositoryPort.findByCode(MERCHANT_OWNER_ROLE_CODE)
                .orElseThrow(() -> new BusinessException(
                        context.requestId(),
                        context.requestDateTime(),
                        context.channel(),
                        ExceptionUtils.buildResultResponse(RECORD_NOT_FOUND, "MERCHANT_OWNER role not found")
                ));

        if (!Boolean.TRUE.equals(merchantOwnerRole.getEnabled())) {
            throw new BusinessException(
                    context.requestId(),
                    context.requestDateTime(),
                    context.channel(),
                    ExceptionUtils.buildResultResponse(INVALID_INPUT_ERROR, "MERCHANT_OWNER role is inactive")
            );
        }

        if (!userRoleAssignmentRepositoryPort.exists(userAccount.id(), merchantOwnerRole.getId())) {
            userRoleAssignmentRepositoryPort.save(
                    UserRoleAssignment.assign(userAccount.id(), merchantOwnerRole.getId(), OffsetDateTime.now())
            );
        }
    }

    private int resolvePageSize(String pageSize, RequestContext context) {
        if (pageSize == null || pageSize.isBlank()) {
            return DEFAULT_PAGE_SIZE;
        }
        int value = parsePositiveInt(pageSize, "pageSize", context);
        if (value > 100) {
            throw new BusinessException(
                    context.requestId(),
                    context.requestDateTime(),
                    context.channel(),
                    ExceptionUtils.buildResultResponse(INVALID_INPUT_ERROR, INVALID_PAGE_SIZE)
            );
        }
        return value;
    }

    private int resolvePageNumber(String pageNumber, RequestContext context) {
        if (pageNumber == null || pageNumber.isBlank()) {
            return DEFAULT_PAGE_NUMBER;
        }
        int value = parsePositiveInt(pageNumber, "pageNumber", context);
        if (value < 1) {
            throw new BusinessException(
                    context.requestId(),
                    context.requestDateTime(),
                    context.channel(),
                    ExceptionUtils.buildResultResponse(INVALID_INPUT_ERROR, INVALID_PAGE_NUMBER)
            );
        }
        return value;
    }

    private int parsePositiveInt(String value, String field, RequestContext context) {
        try {
            return Integer.parseInt(value.trim());
        } catch (Exception ex) {
            throw new BusinessException(
                    context.requestId(),
                    context.requestDateTime(),
                    context.channel(),
                    ExceptionUtils.buildResultResponse(INVALID_INPUT_ERROR, field + " must be numeric")
            );
        }
    }
}
