package platform.management.service.domain.activity.port;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import platform.management.service.domain.activity.model.RecentActivity;

public interface RecentActivityRepositoryPort {
    Page<RecentActivity> findAll(Specification<RecentActivity> spec, PageRequest page);

    void save(RecentActivity entity);
}
