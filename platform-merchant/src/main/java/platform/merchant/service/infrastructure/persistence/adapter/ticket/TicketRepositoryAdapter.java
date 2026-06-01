package platform.merchant.service.infrastructure.persistence.adapter.ticket;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import platform.core.common.service.application.command.common.PagedResult;
import platform.core.common.service.domain.ticket.model.Ticket;
import platform.core.common.service.domain.ticket.port.TicketRepositoryPort;
import platform.merchant.service.infrastructure.persistence.jpa.ticket.repository.TicketRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TicketRepositoryAdapter implements TicketRepositoryPort {

    private final TicketRepository ticketRepository;
    private final TicketPersistenceMapper ticketPersistenceMapper;

    @Override
    public Ticket save(Ticket ticket) {
        return ticketPersistenceMapper.toDomain(
                ticketRepository.save(ticketPersistenceMapper.toEntity(ticket))
        );
    }

    @Override
    public List<Ticket> saveAll(List<Ticket> tickets) {
        return ticketRepository.saveAll(tickets.stream()
                        .map(ticketPersistenceMapper::toEntity)
                        .toList())
                .stream()
                .map(ticketPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Ticket> findById(String id) {
        return ticketRepository.findById(id).map(ticketPersistenceMapper::toDomain);
    }

    @Override
    public Optional<Ticket> findByTicketCode(String ticketCode) {
        return ticketRepository.findByTicketCode(ticketCode).map(ticketPersistenceMapper::toDomain);
    }

    @Override
    public Page<Ticket> findAll(Pageable pageable) {
        return ticketRepository.findAll(pageable).map(ticketPersistenceMapper::toDomain);
    }

    @Override
    public Page<Ticket> search(String query, Pageable pageable) {
        return ticketRepository.search(query, pageable).map(ticketPersistenceMapper::toDomain);
    }

    @Override
    public String generateTicketCode() {
        String ticketCode;
        do {
            ticketCode = "TKT-" + java.time.OffsetDateTime.now().toEpochSecond() + "-" + java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (ticketRepository.existsByTicketCode(ticketCode));
        return ticketCode;
    }

    @Override
    public long countByMerchantId(String merchantId) {
        return ticketRepository.countByMerchantId(merchantId);
    }

    @Override
    public Page<Ticket> findAllByMerchantId(String merchantId, Pageable pageable) {
        return ticketRepository.findAllByMerchantId(merchantId, pageable).map(ticketPersistenceMapper::toDomain);
    }

    @Override
    public Page<Ticket> findByCustomer(String email, String phone, String ticketCode,
                                     OffsetDateTime fromDate, OffsetDateTime toDate,
                                     Pageable pageable) {
        return ticketRepository.findByCustomer(email, phone, ticketCode, fromDate, toDate, pageable)
                .map(ticketPersistenceMapper::toDomain);
    }

    public PagedResult<Ticket> fetchByCustomerId(String customerId, int pageNumber, int pageSize) {
        Page<Ticket> page = ticketRepository.findAllByCreatedBy(customerId, PageRequest.of(pageNumber, pageSize))
                .map(ticketPersistenceMapper::toDomain);
        return PagedResult.<Ticket>builder()
                .items(page.getContent())
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }

    public Optional<Ticket> findByIdAndCustomerId(String id, String customerId) {
        return ticketRepository.findByIdAndCreatedBy(id, customerId)
                .map(ticketPersistenceMapper::toDomain);
    }

    @Override
    public List<Ticket> findAllByTripId(String tripId) {
        return ticketRepository.findAllByTripId(tripId)
                .stream()
                .map(ticketPersistenceMapper::toDomain)
                .toList();
    }
}
