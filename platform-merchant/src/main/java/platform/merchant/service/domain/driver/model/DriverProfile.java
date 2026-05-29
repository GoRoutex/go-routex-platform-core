package platform.merchant.service.domain.driver.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.core.common.service.persistence.AbstractAuditingEntity;
import platform.merchant.service.domain.driver.DriverStatus;
import platform.merchant.service.domain.driver.OperationStatus;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public class DriverProfile extends AbstractAuditingEntity {

    private String id;

    private String merchantId;

    private String userId;

    private String fullName;

    private String phoneNumber;

    private String employeeCode;

    private String emergencyContactName;

    private String emergencyContactPhone;

    private DriverStatus status;

    private OperationStatus operationStatus;

    private Double rating;

    private Integer totalTrips;

    private String licenseClass;

    private String licenseNumber;

    private LocalDate licenseIssueDate;

    private LocalDate licenseExpiryDate;

    private Integer pointsDelta;

    private String pointsReason;

    private Boolean kycVerified;

    private Boolean trainingCompleted;

    private String note;
}
