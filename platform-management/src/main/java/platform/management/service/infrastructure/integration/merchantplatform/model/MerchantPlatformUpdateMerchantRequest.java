package platform.management.service.infrastructure.integration.merchantplatform.model;

import lombok.Getter;
import lombok.Setter;
import platform.merchant.service.domain.merchant.MerchantStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
public class MerchantPlatformUpdateMerchantRequest {
    private String merchantId;
    private String code;
    private String slug;
    private String displayName;
    private String legalName;
    private String taxCode;
    private String businessLicenseNumber;
    private String businessLicenseUrl;
    private String phone;
    private String email;
    private String logoUrl;
    private String description;
    private String address;
    private String ward;
    private String province;
    private String country;
    private String postalCode;
    private String representativeName;
    private String contactName;
    private String contactPhone;
    private String contactEmail;
    private String ownerFullName;
    private String ownerPhone;
    private String ownerEmail;
    private String bankAccountName;
    private String bankAccountNumber;
    private String bankName;
    private String bankBranch;
    private BigDecimal commissionRate;
    private MerchantStatus status;
    private OffsetDateTime approvedAt;
    private String approvedBy;
    private String updatedBy;
}
