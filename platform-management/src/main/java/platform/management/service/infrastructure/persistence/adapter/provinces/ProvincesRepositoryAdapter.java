package platform.management.service.infrastructure.persistence.adapter.provinces;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import platform.management.service.domain.provinces.model.Province;
import platform.management.service.domain.provinces.port.ProvincesRepositoryPort;
import platform.management.service.infrastructure.persistence.jpa.provinces.repository.ProvincesEntityRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ProvincesRepositoryAdapter implements ProvincesRepositoryPort {

    private final ProvincesEntityRepository provincesEntityRepository;
    private final ProvincesPersistenceMapper provincesPersistenceMapper;

    @Override
    public Optional<Province> findById(Integer id) {
        return provincesEntityRepository.findById(id).map(provincesPersistenceMapper::toDomain);
    }

    @Override
    public Optional<Province> findByCode(String code) {
        return provincesEntityRepository.findByCode(code).map(provincesPersistenceMapper::toDomain);
    }
}
