package platform.merchant.service.infrastructure.persistence.jpa.ticket.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import platform.core.common.service.domain.ticket.TicketStatus;
import platform.merchant.service.infrastructure.persistence.jpa.ticket.entity.TicketEntity;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<TicketEntity, String> {
    Optional<TicketEntity> findByTicketCode(String ticketCode);

    @Query("SELECT t FROM TicketEntity t WHERE " +
            "LOWER(t.ticketCode) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(t.customerName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(t.customerPhone) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(t.customerEmail) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<TicketEntity> search(@Param("query") String query, Pageable pageable);

    boolean existsByTicketCode(String ticketCode);

    long countByMerchantId(String merchantId);

    Page<TicketEntity> findAllByMerchantId(String merchantId, Pageable pageable);

    @Query("SELECT t FROM TicketEntity t WHERE " +
            "t.merchantId = :merchantId AND " +
            "(:query IS NULL OR :query = '' OR " +
            "LOWER(t.ticketCode) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(t.customerName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(t.customerPhone) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(t.customerEmail) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
            "(:status IS NULL OR t.status = :status) AND " +
            "(CAST(:issuedFrom AS timestamp) IS NULL OR t.issuedAt >= :issuedFrom) AND " +
            "(CAST(:issuedTo AS timestamp) IS NULL OR t.issuedAt < :issuedTo)")
    Page<TicketEntity> findByMerchantFilters(@Param("merchantId") String merchantId,
                                             @Param("query") String query,
                                             @Param("status") TicketStatus status,
                                             @Param("issuedFrom") OffsetDateTime issuedFrom,
                                             @Param("issuedTo") OffsetDateTime issuedTo,
                                             Pageable pageable);

    Page<TicketEntity> findAllByCreatedBy(String createdBy, Pageable pageable);

    Optional<TicketEntity> findByIdAndCreatedBy(String id, String createdBy);

    List<TicketEntity> findAllByTripId(String tripId);

    @Query("SELECT t FROM TicketEntity t WHERE " +
            "(:email IS NULL OR t.customerEmail = :email) AND " +
            "(:phone IS NULL OR t.customerPhone = :phone) AND " +
            "(:ticketCode IS NULL OR t.ticketCode = :ticketCode) AND " +
            "(CAST(:fromDate AS timestamp) IS NULL OR t.createdAt >= :fromDate) AND " +
            "(CAST(:toDate AS timestamp) IS NULL OR t.createdAt <= :toDate)")
    Page<TicketEntity> findByCustomer(@Param("email") String email,
                                     @Param("phone") String phone,
                                     @Param("ticketCode") String ticketCode,
                                     @Param("fromDate") java.time.OffsetDateTime fromDate,
                                     @Param("toDate") java.time.OffsetDateTime toDate,
                                     Pageable pageable);
}
