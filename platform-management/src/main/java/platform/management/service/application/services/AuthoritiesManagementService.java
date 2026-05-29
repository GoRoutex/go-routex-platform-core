package platform.management.service.application.services;

import platform.management.service.application.command.authorities.AddPermissionCommand;
import platform.management.service.application.command.authorities.AddPermissionResult;
import platform.management.service.application.command.authorities.AddRoleCommand;
import platform.management.service.application.command.authorities.AddRoleResult;
import platform.management.service.application.command.authorities.SetPermissionCommand;
import platform.management.service.application.command.authorities.SetPermissionResult;
import platform.management.service.application.command.authorities.SetRoleCommand;
import platform.management.service.application.command.authorities.SetRoleResult;

public interface AuthoritiesManagementService {
    AddRoleResult addRole(AddRoleCommand command);

    AddPermissionResult addPermission(AddPermissionCommand command);

    SetPermissionResult setPermission(SetPermissionCommand command);

    SetRoleResult setRole(SetRoleCommand command);
}
