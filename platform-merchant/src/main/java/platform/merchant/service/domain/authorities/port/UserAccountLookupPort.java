package platform.merchant.service.domain.authorities.port;

import platform.merchant.service.domain.authorities.model.UserAccountReference;

import java.util.Optional;

public interface UserAccountLookupPort {
    Optional<UserAccountReference> findById(String userId);

    Optional<UserAccountReference> findByEmail(String email);
}
