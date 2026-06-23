package platform.booking.service.infrastructure.persistence.adapter.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import platform.booking.service.infrastructure.persistence.jpa.booking.repository.BookingLegEntityRepository;
import platform.core.common.service.domain.booking.model.BookingLeg;
import platform.core.common.service.domain.booking.port.BookingLegRepositoryPort;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BookingLegRepositoryAdapter implements BookingLegRepositoryPort {

    private final BookingLegEntityRepository bookingLegEntityRepository;
    private final BookingPersistenceMapper bookingPersistenceMapper;

    @Override
    public BookingLeg save(BookingLeg bookingLeg) {
        return bookingPersistenceMapper.toDomain(
                bookingLegEntityRepository.save(bookingPersistenceMapper.toEntity(bookingLeg))
        );
    }

    @Override
    public List<BookingLeg> saveAll(List<BookingLeg> bookingLegs) {
        return bookingLegEntityRepository.saveAll(
                bookingLegs.stream().map(bookingPersistenceMapper::toEntity).toList()
        ).stream().map(bookingPersistenceMapper::toDomain).toList();
    }

    @Override
    public List<BookingLeg> findAllByBookingId(String bookingId) {
        return bookingLegEntityRepository.findByBookingId(bookingId).stream()
                .map(bookingPersistenceMapper::toDomain)
                .toList();
    }
}
