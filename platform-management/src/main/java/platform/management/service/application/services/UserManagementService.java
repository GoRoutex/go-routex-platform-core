package platform.management.service.application.services;

import platform.management.service.application.command.user.DeleteUserCommand;
import platform.management.service.application.command.user.DeleteUserResult;
import platform.management.service.application.command.user.FetchUserDetailQuery;
import platform.management.service.application.command.user.FetchUserDetailResult;
import platform.management.service.application.command.user.FetchUsersQuery;
import platform.management.service.application.command.user.FetchUsersResult;
import platform.management.service.application.command.user.UpdateUserCommand;
import platform.management.service.application.command.user.UpdateUserResult;

public interface UserManagementService {
    FetchUsersResult fetchUsers(FetchUsersQuery query);

    FetchUserDetailResult fetchUserDetail(FetchUserDetailQuery query);

    UpdateUserResult updateUser(UpdateUserCommand command);

    DeleteUserResult deleteUser(DeleteUserCommand command);
}
