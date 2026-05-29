package platform.management.service.infrastructure.persistence.jpa.provinces.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import platform.management.service.infrastructure.persistence.jpa.provinces.entity.ProvincesEntity;
import platform.management.service.infrastructure.persistence.jpa.provinces.projection.ProvincesCodeProjection;

import java.util.Optional;

public interface ProvincesEntityRepository extends JpaRepository<ProvincesEntity, Integer> {
    Optional<ProvincesEntity> findByCode(String code);

    @Query(value = """
            SELECT * FROM PROVINCES
            """, nativeQuery = true)
    Page<ProvincesEntity> fetchAll(Pageable pageable);

    @Query(value = """
            SELECT p.* FROM PROVINCES p
            WHERE (:keyword IS NULL OR :keyword = '' OR LOWER(p.NAME) LIKE LOWER(CONCAT('%', :keyword, '%')))
            """, nativeQuery = true)
    Page<ProvincesEntity> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query(value = """
            SELECT  o.code AS originCode,
                    d.code AS destinationCode
            FROM PROVINCES o
            JOIN PROVINCES d
            ON d.name = :destination
            WHERE o.name = :origin
            """, nativeQuery = true)
    ProvincesCodeProjection selectProvincesCode(@Param("origin") String origin,
                                                @Param("destination") String destination);


}
