package platform.merchant.service.domain.finance.port;

import platform.merchant.service.domain.finance.model.MerchantDailyStats;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MerchantDailyStatsRepositoryPort {
    void save(MerchantDailyStats stats);
    Optional<MerchantDailyStats> findById(String id);
    List<MerchantDailyStats> findAllByMerchantIdAndStatsDateBetween(String merchantId, LocalDate startDate, LocalDate endDate);
}
