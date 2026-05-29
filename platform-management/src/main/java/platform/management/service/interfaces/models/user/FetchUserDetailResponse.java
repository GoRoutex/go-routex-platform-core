package platform.management.service.interfaces.models.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.management.service.domain.user.model.Gender;
import platform.management.service.domain.user.model.UserStatus;
import platform.core.common.service.api.BaseResponse;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class FetchUserDetailResponse extends BaseResponse<FetchUserDetailResponse.FetchUserDetailResponseData> {

    @Getter
    @Setter
    @NoArgsConstructor
    @SuperBuilder
    public static class FetchUserDetailResponseData {
        private String id;
        private String email;
        private String phoneNumber;
        private String avatarUrl;
        private String address;
        private LocalDate dob;
        private Gender gender;
        private String nationalId;
        private Boolean phoneVerified;
        private Boolean profileCompleted;
        private Boolean emailVerified;
        private UserStatus status;
        private String language;
        private String timezone;
        private Integer failLoginCount;
        private OffsetDateTime lastLoginAt;
        private OffsetDateTime lockedUntil;
        private OffsetDateTime createdAt;
        private String createdBy;
        private OffsetDateTime updatedAt;
        private String updatedBy;
    }
}
