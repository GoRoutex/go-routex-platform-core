package platform.merchant.service.domain.finance.port;

import platform.merchant.service.domain.finance.model.TripDemandHistory;

import java.util.List;
import java.util.Optional;

public interface TripDemandHistoryRepositoryPort {
    void save(TripDemandHistory history);
    Optional<TripDemandHistory> findById(String id);
    List<Object[]> findPopularRoutes(String merchantId);
}
