package platform.merchant.service.infrastructure.persistence.adapter.merchant;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import platform.merchant.service.domain.merchant.model.MerchantUser;
import platform.merchant.service.domain.merchant.port.MerchantUserRepositoryPort;
import platform.merchant.service.infrastructure.persistence.jpa.merchant.repository.MerchantUserEntityRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MerchantUserRepositoryAdapter implements MerchantUserRepositoryPort {

    private final MerchantUserEntityRepository merchantUserEntityRepository;
    private final MerchantUserPersistenceMapper merchantUserPersistenceMapper;

    @Override
    public MerchantUser save(MerchantUser merchantUser) {
        return merchantUserPersistenceMapper.toDomain(
                merchantUserEntityRepository.save(merchantUserPersistenceMapper.toEntity(merchantUser))
        );
    }

    @Override
    public Optional<MerchantUser> findByUserId(String userId) {
        return merchantUserEntityRepository.findByUserId(userId)
                .map(merchantUserPersistenceMapper::toDomain);
    }
}
