package platform.merchant.service.infrastructure.persistence.adapter.finance;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import platform.merchant.service.domain.finance.model.RevenueTransaction;
import platform.merchant.service.domain.finance.port.RevenueRepositoryPort;
import platform.merchant.service.infrastructure.persistence.jpa.finance.entity.RevenueTransactionEntity;
import platform.merchant.service.infrastructure.persistence.jpa.finance.repository.RevenueTransactionRepository;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RevenueRepositoryAdapter implements RevenueRepositoryPort {

    private final RevenueTransactionRepository repository;
    private final RevenuePersistenceMapper mapper;

    @Override
    public RevenueTransaction save(RevenueTransaction transaction) {
        RevenueTransactionEntity entity = mapper.toEntity(transaction);
        return mapper.toDomain(repository.save(entity));
    }

    @Override
    public List<RevenueTransaction> findAllByMerchantId(String merchantId) {
        return repository.findAllByMerchantId(merchantId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public BigDecimal sumMerchantAmount(String merchantId, OffsetDateTime startDate, OffsetDateTime endDate) {
        BigDecimal sum = repository.sumMerchantAmountByMerchantIdAndDateBetween(merchantId, startDate, endDate);
        return sum != null ? sum : BigDecimal.ZERO;
    }

    @Override
    public BigDecimal sumSystemAmount(OffsetDateTime startDate, OffsetDateTime endDate) {
        BigDecimal sum = repository.sumSystemAmountByDateBetween(startDate, endDate);
        return sum != null ? sum : BigDecimal.ZERO;
    }
}
