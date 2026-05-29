package platform.merchant.service.infrastructure.persistence.adapter.ticket;

import org.springframework.stereotype.Component;
import platform.core.common.service.domain.ticket.model.Ticket;
import platform.merchant.service.infrastructure.persistence.jpa.ticket.entity.TicketEntity;

@Component
public class TicketPersistenceMapper {

    public Ticket toDomain(TicketEntity entity) {
        if (entity == null) return null;
        return Ticket.builder()
                .id(entity.getId())
                .ticketCode(entity.getTicketCode())
                .bookingId(entity.getBookingId())
                .bookingSeatId(entity.getBookingSeatId())
                .merchantId(entity.getMerchantId())
                .tripId(entity.getTripId())
                .vehicleId(entity.getVehicleId())
                .seatNumber(entity.getSeatNumber())
                .customerName(entity.getCustomerName())
                .customerPhone(entity.getCustomerPhone())
                .customerEmail(entity.getCustomerEmail())
                .price(entity.getPrice())
                .status(entity.getStatus())
                .issuedAt(entity.getIssuedAt())
                .checkedInAt(entity.getCheckedInAt())
                .boardedAt(entity.getBoardedAt())
                .cancelledAt(entity.getCancelledAt())
                .checkedInBy(entity.getCheckedInBy())
                .boardedBy(entity.getBoardedBy())
                .cancelledBy(entity.getCancelledBy())
                .createdAt(entity.getCreatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedAt(entity.getUpdatedAt())
                .updatedBy(entity.getUpdatedBy())
                .pickupType(entity.getPickupType())
                .pickupStopId(entity.getPickupStopId())
                .pickupAddress(entity.getPickupAddress())
                .dropoffType(entity.getDropoffType())
                .dropoffStopId(entity.getDropoffStopId())
                .dropoffAddress(entity.getDropoffAddress())
                .build();
    }

    public TicketEntity toEntity(Ticket domain) {
        if (domain == null) return null;
        return TicketEntity.builder()
                .id(domain.getId())
                .ticketCode(domain.getTicketCode())
                .bookingId(domain.getBookingId())
                .bookingSeatId(domain.getBookingSeatId())
                .merchantId(domain.getMerchantId())
                .tripId(domain.getTripId())
                .vehicleId(domain.getVehicleId())
                .seatNumber(domain.getSeatNumber())
                .customerName(domain.getCustomerName())
                .customerPhone(domain.getCustomerPhone())
                .customerEmail(domain.getCustomerEmail())
                .price(domain.getPrice())
                .status(domain.getStatus())
                .issuedAt(domain.getIssuedAt())
                .checkedInAt(domain.getCheckedInAt())
                .boardedAt(domain.getBoardedAt())
                .cancelledAt(domain.getCancelledAt())
                .checkedInBy(domain.getCheckedInBy())
                .boardedBy(domain.getBoardedBy())
                .cancelledBy(domain.getCancelledBy())
                .createdAt(domain.getCreatedAt())
                .createdBy(domain.getCreatedBy())
                .updatedAt(domain.getUpdatedAt())
                .updatedBy(domain.getUpdatedBy())
                .pickupType(domain.getPickupType())
                .pickupStopId(domain.getPickupStopId())
                .pickupAddress(domain.getPickupAddress())
                .dropoffType(domain.getDropoffType())
                .dropoffStopId(domain.getDropoffStopId())
                .dropoffAddress(domain.getDropoffAddress())
                .build();
    }
}
