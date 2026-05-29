package platform.merchant.service.infrastructure.integration.userservice;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import platform.merchant.service.domain.authorities.model.UserAccountReference;
import platform.merchant.service.domain.authorities.port.UserAccountLookupPort;
import platform.merchant.service.domain.user.port.UserRepositoryPort;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserAccountLookupGrpcAdapter implements UserAccountLookupPort {

    private final UserRepositoryPort userRepositoryPort;

    @Override
    public Optional<UserAccountReference> findById(String userId) {
        return userRepositoryPort.findById(userId)
                .map(user -> new UserAccountReference(user.getId()));
    }

    @Override
    public Optional<UserAccountReference> findByEmail(String email) {
        return userRepositoryPort.findByEmail(email)
                .map(user -> new UserAccountReference(user.getId()));
    }
}
