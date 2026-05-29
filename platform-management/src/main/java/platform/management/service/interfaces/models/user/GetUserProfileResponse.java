package platform.management.service.interfaces.models.user;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.management.service.domain.user.model.UserStatus;
import platform.core.common.service.api.BaseResponse;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class GetUserProfileResponse extends BaseResponse<GetUserProfileResponse.GetUserProfileResponseData> {


    @Getter
    @Setter
    @NoArgsConstructor
    @SuperBuilder
    public static class GetUserProfileResponseData {
        private String userId;
        private String username;
        private String email;
        private String phone;
        private String fullName;
        private UserStatus status;
        private Boolean emailVerified;
        private Boolean phoneVerified;
        private OffsetDateTime createdAt;
        private OffsetDateTime updatedAt;

        private List<String> authorities;
        private CustomerProfile customer;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @SuperBuilder
    public static class CustomerProfile {
        private String customerId;
        private String customerCode;
        private Integer tripPoints;
        private Integer availablePoints;
        private Integer totalTrips;
        private BigDecimal totalSpent;
        private OffsetDateTime lastTripAt;
    }
}
