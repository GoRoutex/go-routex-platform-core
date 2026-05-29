package platform.management.service.application.services;

import platform.management.service.application.command.media.UploadMediaCommand;
import platform.management.service.application.command.media.UploadMediaResult;

public interface MediaService {
    UploadMediaResult uploadMedia(UploadMediaCommand command);
}
