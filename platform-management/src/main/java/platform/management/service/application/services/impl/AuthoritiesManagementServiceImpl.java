package platform.management.service.application.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import platform.core.common.service.persistence.exception.BusinessException;
import platform.core.common.service.persistence.utils.ExceptionUtils;
import platform.management.service.application.command.authorities.AddPermissionCommand;
import platform.management.service.application.command.authorities.AddPermissionResult;
import platform.management.service.application.command.authorities.AddRoleCommand;
import platform.management.service.application.command.authorities.AddRoleResult;
import platform.management.service.application.command.authorities.SetPermissionCommand;
import platform.management.service.application.command.authorities.SetPermissionResult;
import platform.management.service.application.command.authorities.SetRoleCommand;
import platform.management.service.application.command.authorities.SetRoleResult;
import platform.management.service.application.services.AuthoritiesManagementService;
import platform.management.service.domain.authorities.model.PermissionProfile;
import platform.management.service.domain.authorities.model.RoleAggregate;
import platform.management.service.domain.authorities.model.UserRoleAssignment;
import platform.management.service.domain.authorities.port.PermissionRepositoryPort;
import platform.management.service.domain.authorities.port.UserRoleAssignmentRepositoryPort;
import platform.management.service.domain.role.port.RoleRepositoryPort;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static platform.core.common.service.persistence.constant.ErrorConstant.AUTHORITIES_NOT_FOUND;
import static platform.core.common.service.persistence.constant.ErrorConstant.DUPLICATE_ERROR;
import static platform.core.common.service.persistence.constant.ErrorConstant.DUPLICATE_USER_ROLE_MESSAGE;
import static platform.core.common.service.persistence.constant.ErrorConstant.PERMISSION_EXISTS_ERROR;
import static platform.core.common.service.persistence.constant.ErrorConstant.RECORD_NOT_FOUND;
import static platform.core.common.service.persistence.constant.ErrorConstant.ROLE_EXISTS_ERROR;
import static platform.core.common.service.persistence.constant.ErrorConstant.ROLE_NOT_FOUND;


@Service
@RequiredArgsConstructor
public class AuthoritiesManagementServiceImpl implements AuthoritiesManagementService {

    private final RoleRepositoryPort roleRepositoryPort;
    private final PermissionRepositoryPort permissionRepositoryPort;
    private final UserRoleAssignmentRepositoryPort userRoleAssignmentRepositoryPort;

    @Override
    public AddRoleResult addRole(AddRoleCommand command) {
        if (roleRepositoryPort.existsByCode(command.code())) {
            throw new BusinessException(command.requestId(), command.requestDateTime(), command.channel(),
                    ExceptionUtils.buildResultResponse(DUPLICATE_ERROR, String.format(ROLE_EXISTS_ERROR, command.code())));
        }

        RoleAggregate roleAggregate = RoleAggregate.create(
                UUID.randomUUID().toString(),
                command.code(),
                command.name(),
                command.description(),
                command.enabled(),
                command.creator(),
                OffsetDateTime.now()
        );

        roleRepositoryPort.save(roleAggregate);

        return AddRoleResult.builder()
                .code(command.code())
                .name(command.name())
                .creator(command.creator())
                .description(command.description())
                .build();
    }

    @Override
    public AddPermissionResult addPermission(AddPermissionCommand command) {

        if (permissionRepositoryPort.existsByCode(command.code())) {
            throw new BusinessException(command.requestId(), command.requestDateTime(), command.channel(),
                    ExceptionUtils.buildResultResponse(DUPLICATE_ERROR, String.format(PERMISSION_EXISTS_ERROR, command.code())));
        }

        PermissionProfile permissionProfile = PermissionProfile.create(
                command.code(),
                command.name(),
                command.description(),
                command.enabled(),
                command.creator(),
                OffsetDateTime.now()
        );

        permissionRepositoryPort.save(permissionProfile);

        return AddPermissionResult.builder()
                .code(command.code())
                .name(command.name())
                .creator(command.creator())
                .description(command.description())
                .build();
    }

    @Override
    public SetPermissionResult setPermission(SetPermissionCommand command) {
        RoleAggregate roleAggregate = roleRepositoryPort.findById(command.roleId())
                .orElseThrow(() -> new BusinessException(command.requestId(), command.requestDateTime(), command.channel(),
                        ExceptionUtils.buildResultResponse(RECORD_NOT_FOUND, ROLE_NOT_FOUND)));

        List<PermissionProfile> authoritiesList = permissionRepositoryPort.findByCodes(command.authoritiesCode());

        if(authoritiesList.size() != command.authoritiesCode().size()) {
            Set<String> foundCodes = authoritiesList.stream()
                    .map(PermissionProfile::getCode)
                    .collect(Collectors.toSet());

            Set<String> missingCodes = new HashSet<>(command.authoritiesCode());
            missingCodes.removeAll(foundCodes);

            throw new BusinessException(command.requestId(), command.requestDateTime(), command.channel(),
                    ExceptionUtils.buildResultResponse(RECORD_NOT_FOUND, AUTHORITIES_NOT_FOUND));
        }

        roleAggregate.assignAuthorities(command.authoritiesCode());
        roleRepositoryPort.save(roleAggregate);


        return SetPermissionResult.builder()
                .roleId(command.roleId())
                .authorities(command.authoritiesCode())
                .build();
    }

    @Override
    @Transactional
    public SetRoleResult setRole(SetRoleCommand command) {
        RoleAggregate roleAggregate = roleRepositoryPort.findById(command.roleId())
                .orElseThrow(() -> new BusinessException(command.requestId(), command.requestDateTime(), command.channel(),
                        ExceptionUtils.buildResultResponse(RECORD_NOT_FOUND, ROLE_NOT_FOUND)));

        if(userRoleAssignmentRepositoryPort.exists(command.userId(), command.roleId())) {
            throw new BusinessException(command.requestId(), command.requestDateTime(), command.channel(),
                    ExceptionUtils.buildResultResponse(DUPLICATE_ERROR, DUPLICATE_USER_ROLE_MESSAGE));
        }

        UserRoleAssignment userRoleAssignment = UserRoleAssignment.assign(
                command.userId(),
                roleAggregate.getId(),
                OffsetDateTime.now()
        );

        userRoleAssignmentRepositoryPort.save(userRoleAssignment);

        return SetRoleResult.builder()
                .userId(command.userId())
                .roleId(command.roleId())
                .assignedAt(OffsetDateTime.now())
                .build();

    }
}
