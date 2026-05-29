package platform.management.service.domain.provinces.port;

import platform.management.service.domain.provinces.model.Province;

import java.util.Optional;

public interface ProvincesRepositoryPort {
    Optional<Province> findById(Integer id);
    Optional<Province> findByCode(String code);
}
