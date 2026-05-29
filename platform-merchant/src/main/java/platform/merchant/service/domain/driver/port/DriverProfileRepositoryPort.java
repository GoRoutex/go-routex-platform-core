package platform.merchant.service.domain.driver.port;



import platform.core.common.service.application.command.common.PagedResult;
import platform.merchant.service.domain.driver.OperationStatus;
import platform.merchant.service.domain.driver.model.DriverProfile;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Domain repository port (no Spring Data/JPA dependency).
 * Infrastructure layer provides an adapter implementation.
 */
public interface DriverProfileRepositoryPort {
    Optional<DriverProfile> findById(String id);
    Optional<DriverProfile> findById(String id, String merchantId);
    Optional<DriverProfile> findByUserId(String userId);
    Optional<DriverProfile> findByUserId(String userId, String merchantId);
    Optional<DriverProfile> findByEmployeeCode(String employeeCode, String merchantId);
    boolean existsByUserId(String userId, String merchantId);
    boolean existsByEmployeeCode(String employeeCode, String merchantId);
    List<DriverProfile> findByMerchantId(String merchantId);
    PagedResult<DriverProfile> fetch(String merchantId, int pageNumber, int pageSize);
    PagedResult<DriverProfile> fetch(String merchantId, OperationStatus status, int pageNumber, int pageSize);

    DriverProfile save(DriverProfile profile);

    List<DriverProfile> findByIdIn(Set<String> vehicleIds);
}
