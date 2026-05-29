package platform.merchant.service.domain.user.port;

import platform.merchant.service.domain.user.model.User;

import java.util.Optional;

public interface UserRepositoryPort {
    Optional<User> findById(String id);

    Optional<User> findByEmail(String email);
}
