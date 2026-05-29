package platform.management.service.interfaces.models.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.management.service.domain.user.model.Gender;
import platform.management.service.domain.user.model.UserStatus;
import platform.core.common.service.api.BaseResponse;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class FetchUsersResponse extends BaseResponse<FetchUsersResponse.FetchUsersResponsePage> {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class FetchUsersResponsePage {
        private List<FetchUserResponseData> items;
        private Pagination pagination;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class FetchUserResponseData {
        private String id;
        private String email;
        private String fullName;
        private String phoneNumber;
        private String avatarUrl;
        private LocalDate dob;
        private Gender gender;
        private Boolean phoneVerified;
        private Boolean profileCompleted;
        private Boolean emailVerified;
        private UserStatus status;
        private String language;
        private String timezone;
        private OffsetDateTime createdAt;
        private OffsetDateTime updatedAt;
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
