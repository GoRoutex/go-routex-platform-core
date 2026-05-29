package platform.management.service.application.command.user;

import lombok.Builder;
import platform.management.service.domain.user.model.Gender;
import platform.management.service.domain.user.model.UserStatus;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

@Builder
public record FetchUsersResult(
        List<FetchUserItemResult> items,
        int pageNumber,
        int pageSize,
        long totalElements,
        int totalPages
) {
    @Builder
    public record FetchUserItemResult(
            String id,
            String email,
            String fullName,
            String phoneNumber,
            String avatarUrl,
            LocalDate dob,
            Gender gender,
            Boolean phoneVerified,
            Boolean profileCompleted,
            Boolean emailVerified,
            UserStatus status,
            String language,
            String timezone,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    ) {
    }
}
