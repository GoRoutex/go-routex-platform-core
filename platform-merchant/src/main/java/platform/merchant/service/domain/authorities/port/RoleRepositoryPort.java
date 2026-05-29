package platform.merchant.service.domain.authorities.port;

import platform.merchant.service.domain.authorities.model.RoleAggregate;

import java.util.Optional;

public interface RoleRepositoryPort {
    Optional<RoleAggregate> findByCode(String code);
}
