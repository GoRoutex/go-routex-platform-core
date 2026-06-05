package platform.merchant.service.infrastructure.persistence.jpa.merchant.entity;


import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.core.common.service.persistence.AbstractAuditingEntity;
import platform.merchant.service.domain.merchant.ApplicationFormStatus;
import platform.merchant.service.domain.merchant.model.ApplicationFormBankInfo;
import platform.merchant.service.domain.merchant.model.ApplicationFormContact;
import platform.merchant.service.domain.merchant.model.ApplicationFormOwner;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "MERCHANT_APPLICATION_FORM")
public class MerchantApplicationFormEntity extends AbstractAuditingEntity {

    @Id
    private String id;
    private String displayName;
    private String legalName;
    @Embedded
    private ApplicationFormContact contact;
    @Embedded
    private ApplicationFormBankInfo bankInfo;
    @Embedded
    private ApplicationFormOwner ownerInfo;
    private String approvedBy;
    private OffsetDateTime approvedAt;
    private String businessLicenseUrl;
    private String logoUrl;
    private String businessLicense;
    private String description;
    private String formCode;
    private String address;
    private String ward;
    private String postalCode;
    private String province;
    private String country;
    private String rejectedBy;
    private String rejectionReason;
    @Enumerated(EnumType.STRING)
    private ApplicationFormStatus status;
    private OffsetDateTime submittedAt;
    private String submittedBy;
    private String taxCode;
    private String slug;
}
