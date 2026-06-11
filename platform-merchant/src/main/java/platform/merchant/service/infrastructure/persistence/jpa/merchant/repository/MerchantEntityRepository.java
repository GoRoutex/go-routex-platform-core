package platform.merchant.service.infrastructure.persistence.jpa.merchant.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import platform.merchant.service.infrastructure.persistence.jpa.merchant.entity.MerchantEntity;

import java.util.List;

public interface MerchantEntityRepository extends JpaRepository<MerchantEntity, String> {

    boolean existsByCode(String code);

    Page<MerchantEntity> findByDisplayNameContainingIgnoreCaseOrLegalNameContainingIgnoreCase(String displayName, String legalName, Pageable pageable);

    List<MerchantEntity> findByDisplayNameContainingIgnoreCaseOrLegalNameContainingIgnoreCase(String displayName, String legalName);

    @Query(value = """
            SELECT generate_merchant_code()
            """, nativeQuery = true)
    String generateMerchantcode();

    List<MerchantEntity> findAllByIdIn(List<String> merchantIds);
}
