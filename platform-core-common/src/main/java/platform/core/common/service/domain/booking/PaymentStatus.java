package platform.core.common.service.domain.booking;

public enum PaymentStatus {
    UNPAID,
    PENDING,
    PROCESSING,
    PAID,
    FAILED,
    REFUNDED;

    public boolean isFinal() {
        return this == PAID || this == FAILED || this == REFUNDED;
    }
}
