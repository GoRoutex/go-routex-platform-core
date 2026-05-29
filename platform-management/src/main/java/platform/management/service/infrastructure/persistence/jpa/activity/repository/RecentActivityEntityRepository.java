package platform.management.service.infrastructure.persistence.jpa.activity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import platform.management.service.infrastructure.persistence.jpa.activity.entity.RecentActivityEntity;

public interface RecentActivityEntityRepository
        extends JpaRepository<RecentActivityEntity, String>, JpaSpecificationExecutor<RecentActivityEntity> {
}

