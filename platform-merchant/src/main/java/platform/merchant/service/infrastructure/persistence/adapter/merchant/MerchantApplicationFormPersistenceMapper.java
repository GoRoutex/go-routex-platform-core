package platform.merchant.service.infrastructure.persistence.adapter.merchant;

import org.springframework.stereotype.Component;
import platform.merchant.service.domain.merchant.model.MerchantApplicationForm;
import platform.merchant.service.infrastructure.persistence.jpa.merchant.entity.MerchantApplicationFormEntity;

@Component
public class MerchantApplicationFormPersistenceMapper {

    public MerchantApplicationFormEntity toEntity(MerchantApplicationForm application) {
        return MerchantApplicationFormEntity.builder()
                .id(application.getId())
                .formCode(application.getFormCode())
                .displayName(application.getDisplayName())
                .legalName(application.getLegalName())
                .contact(application.getContact())
                .bankInfo(application.getBankInfo())
                .ownerInfo(application.getOwnerInfo())
                .approvedBy(application.getApprovedBy())
                .approvedAt(application.getApprovedAt())
                .businessLicenseUrl(application.getBusinessLicenseUrl())
                .logoUrl(application.getLogoUrl())
                .businessLicense(application.getBusinessLicense())
                .country(application.getCountry())
                .postalCode(application.getPostalCode())
                .province(application.getProvince())
                .description(application.getDescription())
                .ward(application.getWard())
                .address(application.getAddress())
                .rejectedBy(application.getRejectedBy())
                .rejectionReason(application.getRejectionReason())
                .status(application.getStatus())
                .submittedAt(application.getSubmittedAt())
                .submittedBy(application.getSubmittedBy())
                .taxCode(application.getTaxCode())
                .slug(application.getSlug())
                .createdAt(application.getCreatedAt())
                .createdBy(application.getCreatedBy())
                .updatedAt(application.getUpdatedAt())
                .updatedBy(application.getUpdatedBy())
                .build();
    }

    public MerchantApplicationForm toDomain(MerchantApplicationFormEntity entity) {
        return MerchantApplicationForm.builder()
                .id(entity.getId())
                .formCode(entity.getFormCode())
                .displayName(entity.getDisplayName())
                .legalName(entity.getLegalName())
                .contact(entity.getContact())
                .bankInfo(entity.getBankInfo())
                .ownerInfo(entity.getOwnerInfo())
                .approvedBy(entity.getApprovedBy())
                .approvedAt(entity.getApprovedAt())
                .businessLicenseUrl(entity.getBusinessLicenseUrl())
                .logoUrl(entity.getLogoUrl())
                .businessLicense(entity.getBusinessLicense())
                .country(entity.getCountry())
                .postalCode(entity.getPostalCode())
                .province(entity.getProvince())
                .description(entity.getDescription())
                .ward(entity.getWard())
                .address(entity.getAddress())
                .rejectedBy(entity.getRejectedBy())
                .rejectionReason(entity.getRejectionReason())
                .status(entity.getStatus())
                .submittedAt(entity.getSubmittedAt())
                .submittedBy(entity.getSubmittedBy())
                .taxCode(entity.getTaxCode())
                .slug(entity.getSlug())
                .createdAt(entity.getCreatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedAt(entity.getUpdatedAt())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }
}
