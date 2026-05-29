package platform.merchant.service.infrastructure.persistence.adapter.finance;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import platform.merchant.service.domain.finance.model.MerchantDailyStats;
import platform.merchant.service.domain.finance.port.MerchantDailyStatsRepositoryPort;
import platform.merchant.service.infrastructure.persistence.jpa.finance.entity.MerchantDailyStatsEntity;
import platform.merchant.service.infrastructure.persistence.jpa.finance.repository.MerchantDailyStatsRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MerchantDailyStatsRepositoryAdapter implements MerchantDailyStatsRepositoryPort {

    private final MerchantDailyStatsRepository repository;
    private final FinancePersistenceMapper mapper;

    @Override
    public void save(MerchantDailyStats stats) {
        MerchantDailyStatsEntity entity = mapper.toEntity(stats);
        repository.save(entity);
    }

    @Override
    public Optional<MerchantDailyStats> findById(String id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<MerchantDailyStats> findAllByMerchantIdAndStatsDateBetween(String merchantId, LocalDate startDate, LocalDate endDate) {
        return repository.findAllByMerchantIdAndStatsDateBetween(merchantId, startDate, endDate).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}
