package platform.merchant.service.domain.merchant.port;

import platform.core.common.service.application.command.common.PagedResult;
import platform.merchant.service.domain.merchant.ApplicationFormStatus;
import platform.merchant.service.domain.merchant.model.MerchantApplicationForm;

import java.util.Optional;

public interface MerchantApplicationFormRepositoryPort {

    MerchantApplicationForm save(MerchantApplicationForm merchantApplicationForm);

    boolean existsByFormCode(String formCode);

    String generateFormCode();

    Optional<MerchantApplicationForm> findById(String id);

    PagedResult<MerchantApplicationForm> fetch(int pageNumber, int pageSize);

    PagedResult<MerchantApplicationForm> fetchByStatus(ApplicationFormStatus status, int pageNumber, int pageSize);
}
