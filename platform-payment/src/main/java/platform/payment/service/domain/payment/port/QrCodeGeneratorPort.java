package platform.payment.service.domain.payment.port;

public interface QrCodeGeneratorPort {
    String generateBase64Png(String qrContent, int width, int height);
}
