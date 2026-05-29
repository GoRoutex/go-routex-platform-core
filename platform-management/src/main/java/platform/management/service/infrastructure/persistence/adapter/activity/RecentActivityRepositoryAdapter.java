package platform.management.service.infrastructure.persistence.adapter.activity;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import platform.management.service.domain.activity.model.RecentActivity;
import platform.management.service.domain.activity.port.RecentActivityRepositoryPort;
import platform.management.service.infrastructure.persistence.jpa.activity.entity.RecentActivityEntity;
import platform.management.service.infrastructure.persistence.jpa.activity.repository.RecentActivityEntityRepository;


@Component
@RequiredArgsConstructor
public class RecentActivityRepositoryAdapter implements RecentActivityRepositoryPort {

    private final RecentActivityPersistenceMapper recentActivityPersistenceMapper;
    private final RecentActivityEntityRepository recentActivityEntityRepository;
    @Override
    public Page<RecentActivity> findAll(Specification<RecentActivity> spec, PageRequest page) {
        // Port exposes domain-level Specification, but Spring Data JPA executes it against the JPA entity.
        // We rely on identical attribute names between domain model and entity.
        @SuppressWarnings({"unchecked", "rawtypes"})
        Specification<RecentActivityEntity> entitySpec = (Specification) spec;

        return recentActivityEntityRepository
                .findAll(entitySpec, page)
                .map(recentActivityPersistenceMapper::toDomain);
    }

    @Override
    public void save(RecentActivity entity) {
        recentActivityEntityRepository.save(recentActivityPersistenceMapper.toEntity(entity));
    }
}
