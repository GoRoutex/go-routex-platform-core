package platform.merchant.service.domain.provinces.port;



import platform.merchant.service.domain.provinces.model.Province;

import java.util.Optional;

public interface ProvincesRepositoryPort {
    // Master data
    Optional<Province> findById(String id);

    Optional<Province> findByCode(String code);
}

