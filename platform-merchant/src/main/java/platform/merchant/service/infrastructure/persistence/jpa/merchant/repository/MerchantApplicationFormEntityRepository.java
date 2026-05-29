package platform.merchant.service.infrastructure.persistence.jpa.merchant.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import platform.merchant.service.domain.merchant.ApplicationFormStatus;
import platform.merchant.service.infrastructure.persistence.jpa.merchant.entity.MerchantApplicationFormEntity;

public interface MerchantApplicationFormEntityRepository extends JpaRepository<MerchantApplicationFormEntity, String> {

    boolean existsByFormCode(String formCode);

    @Query(value = """
            SELECT generate_merchant_application_form_code()
            """, nativeQuery = true)
    String generateFormCode();

    Page<MerchantApplicationFormEntity> findAll(Pageable pageable);

    Page<MerchantApplicationFormEntity> findByStatus(ApplicationFormStatus status, Pageable pageable);

}
