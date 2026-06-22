package platform.merchant.service.domain.department.port;



import platform.core.common.service.application.command.common.PagedResult;
import platform.merchant.service.domain.department.DepartmentStatus;
import platform.merchant.service.domain.department.model.Department;

import java.util.List;
import java.util.Optional;

public interface DepartmentRepositoryPort {


    Optional<Department> findByName(String name, String merchantId);

    Optional<Department> findById(String id);

    Optional<Department> findById(String id, String merchantId);

    boolean existsByName(String name);

    boolean existsByName(String name, String merchantId);

    List<Department> findByMerchantId(String merchantId);

    void save(Department department);

    PagedResult<Department> fetch(int pageNumber, int pageSize);

    PagedResult<Department> fetch(String merchantId, int pageNumber, int pageSize);

    PagedResult<Department> fetch(String merchantId, String provinceId, int pageNumber, int pageSize);

    PagedResult<Department> fetch(String merchantId, String provinceId, DepartmentStatus status, int pageNumber, int pageSize);

    List<Department> findAllByIdIn(List<String> departmentIds);

    List<Department> search(String keyword, int page, int size);
}
