package platform.payment.service.infrastructure.integration.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "payment.vnpay")
public record VNPayProperties(
        String payUrl,
        String returnUrl,
        String tmnCode,
        String hashSecret
) {
    private static final String DEFAULT_PAY_URL = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
    private static final String DEFAULT_RETURN_URL = "https://4cdc-2402-800-63ba-d725-1df8-c792-6892-555e.ngrok-free.app/api/v1/payment-service/return-url";
    private static final String DEFAULT_TMN_CODE = "2Y0B395W";
    private static final String DEFAULT_HASH_SECRET = "KJEMY4DV3S2QBQVW6Y1MVGUQJJQVVEYP";

    public VNPayProperties {
        payUrl = defaultIfBlank(payUrl, DEFAULT_PAY_URL);
        returnUrl = defaultIfBlank(returnUrl, DEFAULT_RETURN_URL);
        tmnCode = defaultIfBlank(tmnCode, DEFAULT_TMN_CODE);
        hashSecret = defaultIfBlank(hashSecret, DEFAULT_HASH_SECRET);
    }

    private static String defaultIfBlank(String value, String defaultValue) {
        return value == null || value.trim().isEmpty() ? defaultValue : value.trim();
    }
}
