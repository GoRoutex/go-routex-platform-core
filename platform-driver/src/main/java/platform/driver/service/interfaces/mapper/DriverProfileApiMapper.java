package platform.driver.service.interfaces.mapper;

import lombok.experimental.UtilityClass;
import platform.core.common.service.persistence.utils.DateTimeUtils;
import platform.driver.service.application.common.UseCaseException;
import platform.driver.service.application.dto.driver.CreateDriverProfileCommand;
import platform.driver.service.application.dto.driver.DeleteDriverProfileCommand;
import platform.driver.service.application.dto.driver.GetDriverProfileQuery;
import platform.driver.service.application.dto.driver.UpdateDriverProfileCommand;
import platform.driver.service.application.dto.driver.UpdateDriverStatusCommand;
import platform.driver.service.interfaces.models.driver.request.CreateProfileRequest;
import platform.driver.service.interfaces.models.driver.request.DeleteProfileRequest;
import platform.driver.service.interfaces.models.driver.request.DriverProfileRequest;
import platform.driver.service.interfaces.models.driver.request.UpdateDriverStatusRequest;
import platform.driver.service.interfaces.models.driver.request.UpdateProfileRequest;
import platform.merchant.service.domain.driver.DriverStatus;
import platform.merchant.service.domain.driver.OperationStatus;

import static platform.core.common.service.persistence.constant.ErrorConstant.INVALID_INPUT_ERROR;
import static platform.core.common.service.persistence.constant.ErrorConstant.INVALID_INPUT_MESSAGE;


@UtilityClass
public class DriverProfileApiMapper {
    public CreateDriverProfileCommand toCommand(CreateProfileRequest request) {
        CreateProfileRequest.CreateProfileRequestData data = request.getData();
        return CreateDriverProfileCommand.builder()
                .userId(data.getUserId())
                .employeeCode(data.getEmployeeCode())
                .emergencyContactName(data.getEmergencyContactName())
                .emergencyContactPhone(data.getEmergencyContactPhone())
                .status(data.getStatus())
                .rating(data.getRating())
                .totalTrips(data.getTotalTrips())
                .licenseClass(data.getLicenseClass())
                .licenseNumber(data.getLicenseNumber())
                .licenseIssueDate(DateTimeUtils.toLocalDate(data.getLicenseIssueDate()))
                .licenseExpiryDate(DateTimeUtils.toLocalDate(data.getLicenseExpiryDate()))
                .pointsDelta(data.getPointsDelta())
                .pointsReason(data.getPointsReason())
                .kycVerified(data.isKycVerified())
                .trainingCompleted(data.isTrainingCompleted())
                .note(data.getNote())
                .build();
    }

    public UpdateDriverProfileCommand toCommand(UpdateProfileRequest request) {
        var data = request.getData();
        DriverStatus status = null;
        if (data.getStatus() != null && !data.getStatus().isBlank()) {
            status = parseEnum(DriverStatus.class, data.getStatus());
        }
        return UpdateDriverProfileCommand.builder()
                .driverId(data.getDriverId())
                .employeeCode(data.getEmployeeCode())
                .emergencyContactName(data.getEmergencyContactName())
                .emergencyContactPhone(data.getEmergencyContactPhone())
                .status(status)
                .licenseClass(data.getLicenseClass())
                .licenseNumber(data.getLicenseNumber())
                .licenseIssueDate(DateTimeUtils.toLocalDate(data.getLicenseIssueDate()))
                .licenseExpiryDate(DateTimeUtils.toLocalDate(data.getLicenseExpiryDate()))
                .pointsDelta(data.getPointsDelta())
                .pointsReason(data.getPointsReason())
                .kycVerified(data.getKycVerified())
                .trainingCompleted(data.getTrainingCompleted())
                .note(data.getNote())
                .build();

    }

    public DeleteDriverProfileCommand toCommand(DeleteProfileRequest request) {
        return new DeleteDriverProfileCommand(request.getData().getDriverId());
    }

    public UpdateDriverStatusCommand toCommand(UpdateDriverStatusRequest request) {
        var data = request.getData();
        DriverStatus status = null;
        OperationStatus operationStatus = null;

        if (data.getStatus() != null && !data.getStatus().isBlank()) {
            status = parseEnum(DriverStatus.class, data.getStatus());
        }
        if (data.getOperationStatus() != null && !data.getOperationStatus().isBlank()) {
            operationStatus = parseEnum(OperationStatus.class, data.getOperationStatus());
        }
        return new UpdateDriverStatusCommand(data.getDriverId(), status, operationStatus);
    }

    public GetDriverProfileQuery toQuery(DriverProfileRequest request) {
        return new GetDriverProfileQuery(request.getData().getDriverId());
    }

    private static <T extends Enum<T>> T parseEnum(Class<T> type, String value) {
        try {
            return Enum.valueOf(type, value);
        } catch (Exception e) {
            throw new UseCaseException(INVALID_INPUT_ERROR, INVALID_INPUT_MESSAGE);
        }
    }
}
