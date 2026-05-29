package platform.merchant.service.domain.merchant.port;

import platform.merchant.service.domain.merchant.model.MerchantUser;

import java.util.Optional;

public interface MerchantUserRepositoryPort {

    MerchantUser save(MerchantUser merchantUser);

    Optional<MerchantUser> findByUserId(String userId);
}
