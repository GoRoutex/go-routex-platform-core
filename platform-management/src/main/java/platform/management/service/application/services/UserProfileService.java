package platform.management.service.application.services;


import platform.management.service.application.command.user.GetUserProfileCommand;
import platform.management.service.application.command.user.GetUserProfileResult;

public interface UserProfileService {

    GetUserProfileResult getUserProfile(GetUserProfileCommand command);
}
