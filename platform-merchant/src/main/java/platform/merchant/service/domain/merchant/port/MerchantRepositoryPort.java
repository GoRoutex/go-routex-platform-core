package platform.merchant.service.domain.merchant.port;


import platform.core.common.service.application.command.common.PagedResult;
import platform.merchant.service.domain.merchant.model.Merchant;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface MerchantRepositoryPort {

    Merchant save(Merchant merchant);

    Optional<Merchant> findById(String merchantId);

    boolean existsByCode(String code);

    String generateMerchantCode();

    PagedResult<Merchant> fetch(int pageNumber, int pageSize);

    PagedResult<Merchant> fetch(String merchantName, int pageNumber, int pageSize);

    List<Merchant> findByIds(java.util.List<String> merchantIds);

    List<String> findIdsByMerchantName(String merchantName);

    Map<String, Merchant> findNamesByIds(List<String> merchantIds);
}
