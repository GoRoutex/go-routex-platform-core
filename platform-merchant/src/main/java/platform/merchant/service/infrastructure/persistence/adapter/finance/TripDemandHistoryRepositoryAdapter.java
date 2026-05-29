package platform.merchant.service.infrastructure.persistence.adapter.finance;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import platform.merchant.service.domain.finance.model.TripDemandHistory;
import platform.merchant.service.domain.finance.port.TripDemandHistoryRepositoryPort;
import platform.merchant.service.infrastructure.persistence.jpa.finance.entity.TripDemandHistoryEntity;
import platform.merchant.service.infrastructure.persistence.jpa.finance.repository.TripDemandHistoryRepository;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TripDemandHistoryRepositoryAdapter implements TripDemandHistoryRepositoryPort {

    private final TripDemandHistoryRepository repository;
    private final FinancePersistenceMapper mapper;

    @Override
    public void save(TripDemandHistory history) {
        TripDemandHistoryEntity entity = mapper.toEntity(history);
        repository.save(entity);
    }

    @Override
    public Optional<TripDemandHistory> findById(String id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Object[]> findPopularRoutes(String merchantId) {
        return repository.findPopularRoutes(merchantId);
    }
}
