package platform.management.service.application.command.user;

import lombok.Builder;
import platform.management.service.application.command.common.RequestContext;
import platform.management.service.domain.user.model.Gender;
import platform.management.service.domain.user.model.UserStatus;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Builder
public record UpdateUserCommand(
        RequestContext context,
        String userId,
        String email,
        String phoneNumber,
        String avatarUrl,
        String address,
        LocalDate dob,
        Gender gender,
        String nationalId,
        Boolean phoneVerified,
        Boolean profileCompleted,
        Boolean emailVerified,
        UserStatus status,
        String language,
        String timezone,
        Integer failLoginCount,
        OffsetDateTime lastLoginAt,
        OffsetDateTime lockedUntil,
        String updatedBy
) {
}
