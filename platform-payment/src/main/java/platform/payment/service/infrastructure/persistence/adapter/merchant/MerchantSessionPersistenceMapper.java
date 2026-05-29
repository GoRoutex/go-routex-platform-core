package platform.payment.service.infrastructure.persistence.adapter.merchant;


import org.springframework.stereotype.Component;
import platform.payment.service.domain.merchant.model.MerchantSessionAggregate;
import platform.payment.service.infrastructure.persistence.jpa.merchant.entity.MerchantSessionEntity;

@Component
public class MerchantSessionPersistenceMapper {

    public MerchantSessionEntity toEntity(MerchantSessionAggregate aggregate) {
        if(aggregate == null ) {
            return null;
        }


        return MerchantSessionEntity.builder()
                .id(aggregate.getId())
                .paymentId(aggregate.getPaymentId())
                .paymentMerchant(aggregate.getPaymentMerchant())
                .merchantTxnRef(aggregate.getMerchantTxnRef())
                .checkoutUrl(aggregate.getCheckoutUrl())
                .deeplink(aggregate.getDeeplink())
                .qrPayload(aggregate.getQrPayload())
                .status(aggregate.getStatus())
                .attemptNo(aggregate.getAttemptNo())
                .requestPayload(aggregate.getRequestPayload())
                .responsePayload(aggregate.getResponsePayload())
                .errorCode(aggregate.getErrorCode())
                .errorMessage(aggregate.getErrorMessage())
                .expiredAt(aggregate.getExpiredAt())
                .build();
    }

    public MerchantSessionAggregate toDomain(MerchantSessionEntity merchantSessionEntity) {
        if(merchantSessionEntity == null) {
            return null;
        }

        return MerchantSessionAggregate.builder()
                .id(merchantSessionEntity.getId())
                .paymentId(merchantSessionEntity.getPaymentId())
                .paymentMerchant(merchantSessionEntity.getPaymentMerchant())
                .merchantTxnRef(merchantSessionEntity.getMerchantTxnRef())
                .checkoutUrl(merchantSessionEntity.getCheckoutUrl())
                .deeplink(merchantSessionEntity.getDeeplink())
                .qrPayload(merchantSessionEntity.getQrPayload())
                .status(merchantSessionEntity.getStatus())
                .attemptNo(merchantSessionEntity.getAttemptNo())
                .requestPayload(merchantSessionEntity.getRequestPayload())
                .responsePayload(merchantSessionEntity.getResponsePayload())
                .errorCode(merchantSessionEntity.getErrorCode())
                .errorMessage(merchantSessionEntity.getErrorMessage())
                .expiredAt(merchantSessionEntity.getExpiredAt())
                .build();
    }
}
