package platform.management.service.domain.user.port;

import platform.management.service.domain.user.model.User;

import java.util.Optional;

public interface UserRepositoryPort {

    Optional<User> findById(String id);
}
