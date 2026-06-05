package platform.payment.service.infrastructure.persistence.jpa.merchant.entity;


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
import platform.core.common.service.domain.payment.PaymentMethod;
import platform.payment.service.domain.merchant.MerchantSessionStatus;
import platform.payment.service.infrastructure.persistence.jpa.entity.AbstractAuditingEntity;

import java.time.OffsetDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "MERCHANT_SESSION")
public class MerchantSessionEntity extends AbstractAuditingEntity {

    @Id
    private String id;

    @Column(name = "PAYMENT_ID", nullable = false)
    private String paymentId;

    @Column(name = "PAYMENT_MERCHANT")
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMerchant;

    @Column(name = "MERCHANT_TXN_REF")
    private String merchantTxnRef;

    @Column(name = "CHECKOUT_URL", length = 5000)
    private String checkoutUrl;

    @Column(name = "DEEP_LINK")
    private String deeplink;

    @Column(name = "QR_PAYLOAD")
    private String qrPayload;

    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    private MerchantSessionStatus status;

    @Column(name = "ATTEMPT_NO")
    private int attemptNo;

    @Column(name = "REQUEST_PAYLOAD")
    private String requestPayload;

    @Column(name = "RESPONSE_PAYLOAD")
    private String responsePayload;

    @Column(name = "ERROR_CODE")
    private String errorCode;

    @Column(name = "ERROR_MESSAGE")
    private String errorMessage;

    @Column(name = "EXPIRED_AT")
    private OffsetDateTime expiredAt;
}
