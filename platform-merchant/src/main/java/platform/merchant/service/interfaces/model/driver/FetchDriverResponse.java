package platform.merchant.service.interfaces.model.driver;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.merchant.service.domain.driver.DriverStatus;
import platform.merchant.service.domain.driver.OperationStatus;
import platform.core.common.service.api.BaseResponse;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class FetchDriverResponse extends BaseResponse<FetchDriverResponse.FetchDriverResponsePage> {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class FetchDriverResponsePage {
        private List<FetchDriverResponseData> items;
        private Pagination pagination;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class FetchDriverResponseData {
        private String id;
        private FetchDriverUserInfo userInfo;
        private FetchDriverMerchantInfo merchantInfo;
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

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class FetchDriverUserInfo {
        private String userId;
        private String fullName;
        private String email;
        private String phone;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class FetchDriverMerchantInfo {
        private String merchantId;
        private String merchantName;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class Pagination {
        private int pageNumber;
        private int pageSize;
        private long totalElements;
        private int totalPages;
    }
}
