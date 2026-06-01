package platform.core.common.service.domain.ticket.port;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import platform.core.common.service.application.command.common.PagedResult;
import platform.core.common.service.domain.ticket.model.Ticket;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface TicketRepositoryPort {
    Ticket save(Ticket ticket);

    List<Ticket> saveAll(List<Ticket> tickets);

    Optional<Ticket> findById(String id);

    Optional<Ticket> findByTicketCode(String ticketCode);

    Page<Ticket> findAll(Pageable pageable);

    Page<Ticket> search(String query, Pageable pageable);

    String generateTicketCode();

    long countByMerchantId(String merchantId);

    Page<Ticket> findAllByMerchantId(String merchantId, Pageable pageable);

    Page<Ticket> findByCustomer(
            String email,
            String phone,
            String ticketCode,
            OffsetDateTime fromDate,
            OffsetDateTime toDate,
            Pageable pageable
    );

    PagedResult<Ticket> fetchByCustomerId(String customerId, int pageNumber, int pageSize);

    Optional<Ticket> findByIdAndCustomerId(String id, String customerId);

    List<Ticket> findAllByTripId(String tripId);
}
