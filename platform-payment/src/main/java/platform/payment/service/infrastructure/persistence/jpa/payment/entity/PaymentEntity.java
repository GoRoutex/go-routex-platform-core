package platform.payment.service.infrastructure.persistence.jpa.payment.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.core.common.service.domain.booking.PaymentStatus;
import platform.core.common.service.domain.payment.PaymentMethod;
import platform.core.common.service.persistence.AbstractAuditingEntity;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "PAYMENT")
public class PaymentEntity extends AbstractAuditingEntity {

    @Id
    private String id;

    @Column(name = "BOOKING_CODE", nullable = false)
    private String bookingCode;

    @Column(name = "METHOD")
    @Enumerated(EnumType.STRING)
    private PaymentMethod method;

    @Column(name = "AMOUNT")
    private BigDecimal amount;

    @Column(name = "CURRENCY")
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS")
    private PaymentStatus status;

    @Column(name = "TXN_REF", unique = true)
    private String txnRef;

    @Column(name = "PAID_AT")
    private OffsetDateTime paidAt;

    @Column(name = "FAILED_AT")
    private OffsetDateTime failedAt;

    @Column(name = "FAILURE_REASON")
    private String failureReason;

    @Column(name = "DESCRIPTION")
    private String description;
}
