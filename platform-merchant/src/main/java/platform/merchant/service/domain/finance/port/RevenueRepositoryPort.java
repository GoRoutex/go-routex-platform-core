package platform.merchant.service.domain.finance.port;


import platform.merchant.service.domain.finance.model.RevenueTransaction;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public interface RevenueRepositoryPort {
    RevenueTransaction save(RevenueTransaction transaction);
    List<RevenueTransaction> findAllByMerchantId(String merchantId);
    BigDecimal sumMerchantAmount(String merchantId, OffsetDateTime startDate, OffsetDateTime endDate);
    BigDecimal sumSystemAmount(OffsetDateTime startDate, OffsetDateTime endDate);
}
