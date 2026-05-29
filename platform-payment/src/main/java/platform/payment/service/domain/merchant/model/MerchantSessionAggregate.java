package platform.payment.service.domain.merchant.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.core.common.service.persistence.AbstractAuditingEntity;
import platform.payment.service.domain.merchant.MerchantSessionStatus;
import platform.core.common.service.domain.payment.PaymentMethod;

import java.time.OffsetDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class MerchantSessionAggregate extends AbstractAuditingEntity {

    private String id;
    private String paymentId;
    private PaymentMethod paymentMerchant;
    private String merchantTxnRef;
    private String checkoutUrl;
    private String deeplink;
    private String qrPayload;
    private MerchantSessionStatus status;
    private int attemptNo;
    private String requestPayload;
    private String responsePayload;
    private String errorCode;
    private String errorMessage;
    private OffsetDateTime expiredAt;

    public boolean isReusable(OffsetDateTime now) {
        return MerchantSessionStatus.CREATED.equals(status)
                && expiredAt != null
                && expiredAt.isAfter(now)
                && checkoutUrl != null;
    }
}
