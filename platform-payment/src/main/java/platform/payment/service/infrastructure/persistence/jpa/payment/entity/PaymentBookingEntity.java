package platform.payment.service.infrastructure.persistence.jpa.payment.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.core.common.service.persistence.AbstractAuditingEntity;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "PAYMENT_BOOKING")
public class PaymentBookingEntity extends AbstractAuditingEntity {

    @Id
    private String id;

    @Column(name = "PAYMENT_ID", nullable = false)
    private String paymentId;

    @Column(name = "BOOKING_CODE", nullable = false)
    private String bookingCode;
}
