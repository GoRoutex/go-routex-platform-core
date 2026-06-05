package platform.booking.service.infrastructure.persistence.adapter.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import platform.booking.service.infrastructure.persistence.jpa.booking.repository.BookingSeatEntityRepository;
import platform.core.common.service.domain.booking.BookingSeatStatus;
import platform.core.common.service.domain.booking.model.BookingSeat;
import platform.core.common.service.domain.booking.port.BookingSeatRepositoryPort;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class BookingSeatRepositoryAdapter implements BookingSeatRepositoryPort {

    private final BookingSeatEntityRepository bookingSeatJpaRepository;
    private final BookingPersistenceMapper bookingPersistenceMapper;

    @Override
    public List<BookingSeat> saveAll(List<BookingSeat> bookingSeats) {
        return bookingSeatJpaRepository.saveAll(bookingSeats.stream()
                        .map(bookingPersistenceMapper::toEntity)
                        .toList()).stream()
                .map(bookingPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public BookingSeat save(BookingSeat bookingSeat) {
        return bookingPersistenceMapper.toDomain(
                bookingSeatJpaRepository.save(bookingPersistenceMapper.toEntity(bookingSeat))
        );
    }

    @Override
    public List<BookingSeat> findAllByBookingId(String bookingId) {
        return bookingSeatJpaRepository.findAllByBookingId(bookingId).stream()
                .map(bookingPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public List<BookingSeat> findByBookingId(String bookingId) {
        return findAllByBookingId(bookingId);
    }

    @Override
    public Optional<BookingSeat> findOneByBookingId(String bookingId) {
        return bookingSeatJpaRepository.findFirstByBookingId(bookingId)
                .map(bookingPersistenceMapper::toDomain);
    }

    @Override
    public List<BookingSeat> findByBookingIdAndStatus(String id, BookingSeatStatus bookingSeatStatus) {
        return bookingSeatJpaRepository.findByBookingIdAndStatus(id, bookingSeatStatus).stream()
                .map(bookingPersistenceMapper::toDomain)
                .toList();
    }
}
