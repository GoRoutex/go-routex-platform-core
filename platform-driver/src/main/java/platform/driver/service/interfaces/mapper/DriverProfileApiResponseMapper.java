package platform.driver.service.interfaces.mapper;

import lombok.experimental.UtilityClass;
import platform.core.common.service.api.ApiResult;
import platform.driver.service.application.dto.driver.CreateDriverProfileResult;
import platform.driver.service.application.dto.driver.DeleteDriverProfileResult;
import platform.driver.service.application.dto.driver.DriverProfileDetailsView;
import platform.driver.service.application.dto.driver.UpdateDriverProfileResult;
import platform.driver.service.application.dto.driver.UpdateDriverStatusResult;
import platform.driver.service.interfaces.models.driver.request.CreateProfileRequest;
import platform.driver.service.interfaces.models.driver.request.DeleteProfileRequest;
import platform.driver.service.interfaces.models.driver.request.DriverProfileRequest;
import platform.driver.service.interfaces.models.driver.request.UpdateDriverStatusRequest;
import platform.driver.service.interfaces.models.driver.request.UpdateProfileRequest;
import platform.driver.service.interfaces.models.driver.response.CreateProfileResponse;
import platform.driver.service.interfaces.models.driver.response.DeleteProfileResponse;
import platform.driver.service.interfaces.models.driver.response.DriverProfileResponse;
import platform.driver.service.interfaces.models.driver.response.UpdateDriverStatusResponse;
import platform.driver.service.interfaces.models.driver.response.UpdateProfileResponse;

import static platform.core.common.service.persistence.constant.ErrorConstant.SUCCESS_CODE;
import static platform.core.common.service.persistence.constant.ErrorConstant.SUCCESS_MESSAGE;


@UtilityClass
public class DriverProfileApiResponseMapper {

    private static ApiResult successResult() {
        return ApiResult.builder().responseCode(SUCCESS_CODE).description(SUCCESS_MESSAGE).build();
    }

    public UpdateProfileResponse toUpdateProfileResponse(UpdateProfileRequest request, UpdateDriverProfileResult profile) {
        return UpdateProfileResponse.builder()
                .requestId(request.getRequestId())
                .requestDateTime(request.getRequestDateTime())
                .channel(request.getChannel())
                .result(successResult())
                .data(UpdateProfileResponse.UpdateProfileResponseData.builder()
                        .driverId(profile.driverId())
                        .employeeCode(profile.employeeCode())
                        .emergencyContactName(profile.emergencyContactName())
                        .emergencyContactPhone(profile.emergencyContactPhone())
                        .status(profile.status())
                        .licenseClass(profile.licenseClass())
                        .licenseNumber(profile.licenseNumber())
                        .licenseIssueDate(profile.licenseIssueDate())
                        .licenseExpiryDate(profile.licenseExpiryDate())
                        .pointsDelta(profile.pointsDelta())
                        .pointsReason(profile.pointsReason())
                        .kycVerified(profile.kycVerified())
                        .trainingCompleted(profile.trainingCompleted())
                        .note(profile.note())
                        .build())
                .build();
    }

    public CreateProfileResponse toCreateProfileResponse(CreateProfileRequest request, CreateDriverProfileResult profile) {
        return CreateProfileResponse.builder()
                .requestId(request.getRequestId())
                .requestDateTime(request.getRequestDateTime())
                .channel(request.getChannel())
                .result(successResult())
                .data(CreateProfileResponse.CreateProfileResponseData.builder()
                        .userId(profile.userId())
                        .driverCode(profile.driverCode())
                        .employeeCode(profile.employeeCode())
                        .currentRouteId(profile.currentRouteId())
                        .emergencyContactName(profile.emergencyContactName())
                        .emergencyContactPhone(profile.emergencyContactPhone())
                        .status(profile.status() != null ? profile.status().name() : null)
                        .operationStatus(profile.operationStatus() != null ? profile.operationStatus().name() : null)
                        .rating(profile.rating() != null ? profile.rating() : 0.0)
                        .totalTrips(profile.totalTrips() != null ? profile.totalTrips() : 0)
                        .licenseClass(profile.licenseClass())
                        .licenseNumber(profile.licenseNumber())
                        .licenseIssueDate(profile.licenseIssueDate())
                        .licenseExpiryDate(profile.licenseExpiryDate())
                        .pointsDelta(profile.pointsDelta() != null ? profile.pointsDelta() : 0)
                        .pointsReason(profile.pointsReason())
                        .kycVerified(Boolean.TRUE.equals(profile.kycVerified()))
                        .trainingCompleted(Boolean.TRUE.equals(profile.trainingCompleted()))
                        .note(profile.note())
                        .build())
                .build();
    }

    public DriverProfileResponse toDriverProfileResponse(DriverProfileRequest request, DriverProfileDetailsView view) {
        return DriverProfileResponse.builder()
                .requestId(request.getRequestId())
                .requestDateTime(request.getRequestDateTime())
                .channel(request.getChannel())
                .result(successResult())
                .data(DriverProfileResponse.DriverProfileResponseData.builder()
                        .driverId(view.profile().getId())
                        .userId(view.profile().getUserId())
                        .phone(view.user().getPhoneNumber())
                        .email(view.user().getEmail())
                        .status(view.profile().getStatus())
                        .operationStatus(view.profile().getOperationStatus())
                        .points(view.profile().getPointsDelta())
                        .createdAt(view.profile().getCreatedAt())
                        .updatedAt(view.profile().getUpdatedAt())
                        .build())
                .build();
    }

    public DeleteProfileResponse toDeleteProfileResponse(DeleteProfileRequest request, DeleteDriverProfileResult profile) {
        return DeleteProfileResponse.builder()
                .requestId(request.getRequestId())
                .requestDateTime(request.getRequestDateTime())
                .channel(request.getChannel())
                .result(successResult())
                .data(DeleteProfileResponse.DeleteProfileResponseData.builder()
                        .driverId(profile.driverId())
                        .status(profile.status())
                        .operationStatus(profile.operationStatus())
                        .build())
                .build();
    }

    public UpdateDriverStatusResponse toUpdateDriverStatusResponse(UpdateDriverStatusRequest request, UpdateDriverStatusResult profile) {
        return UpdateDriverStatusResponse.builder()
                .requestId(request.getRequestId())
                .requestDateTime(request.getRequestDateTime())
                .channel(request.getChannel())
                .result(successResult())
                .data(UpdateDriverStatusResponse.UpdateDriverStatusResponseData.builder()
                        .driverId(profile.driverId())
                        .status(profile.status())
                        .operationStatus(profile.operationStatus())
                        .build())
                .build();
    }
}
