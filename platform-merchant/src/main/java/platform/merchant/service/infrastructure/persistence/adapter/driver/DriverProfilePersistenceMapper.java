package platform.merchant.service.infrastructure.persistence.adapter.driver;

import org.springframework.stereotype.Component;
import platform.merchant.service.domain.driver.model.DriverProfile;
import platform.merchant.service.infrastructure.persistence.jpa.driver.entity.DriverProfileEntity;

@Component
public class DriverProfilePersistenceMapper {
    public DriverProfile toDomain(DriverProfileEntity entity) {
        if (entity == null) {
            return null;
        }
        return DriverProfile.builder()
                .id(entity.getId())
                .merchantId(entity.getMerchantId())
                .userId(entity.getUserId())
                .employeeCode(entity.getEmployeeCode())
                .emergencyContactName(entity.getEmergencyContactName())
                .emergencyContactPhone(entity.getEmergencyContactPhone())
                .status(entity.getStatus())
                .operationStatus(entity.getOperationStatus())
                .rating(entity.getRating())
                .totalTrips(entity.getTotalTrips())
                .licenseClass(entity.getLicenseClass())
                .licenseNumber(entity.getLicenseNumber())
                .licenseIssueDate(entity.getLicenseIssueDate())
                .licenseExpiryDate(entity.getLicenseExpiryDate())
                .pointsDelta(entity.getPointsDelta())
                .pointsReason(entity.getPointsReason())
                .kycVerified(entity.getKycVerified())
                .trainingCompleted(entity.getTrainingCompleted())
                .note(entity.getNote())
                .createdAt(entity.getCreatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedAt(entity.getUpdatedAt())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }

    public DriverProfileEntity toEntity(DriverProfile domain) {
        if (domain == null) {
            return null;
        }
        return DriverProfileEntity.builder()
                .id(domain.getId())
                .merchantId(domain.getMerchantId())
                .userId(domain.getUserId())
                .employeeCode(domain.getEmployeeCode())
                .emergencyContactName(domain.getEmergencyContactName())
                .emergencyContactPhone(domain.getEmergencyContactPhone())
                .status(domain.getStatus())
                .operationStatus(domain.getOperationStatus())
                .rating(domain.getRating())
                .totalTrips(domain.getTotalTrips())
                .licenseClass(domain.getLicenseClass())
                .licenseNumber(domain.getLicenseNumber())
                .licenseIssueDate(domain.getLicenseIssueDate())
                .licenseExpiryDate(domain.getLicenseExpiryDate())
                .pointsDelta(domain.getPointsDelta())
                .pointsReason(domain.getPointsReason())
                .kycVerified(domain.getKycVerified())
                .trainingCompleted(domain.getTrainingCompleted())
                .note(domain.getNote())
                .createdAt(domain.getCreatedAt())
                .createdBy(domain.getCreatedBy())
                .updatedAt(domain.getUpdatedAt())
                .updatedBy(domain.getUpdatedBy())
                .build();
    }
}
