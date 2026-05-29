package platform.driver.service.domain.shared.port;

/**
 * Strategy for generating IDs (DIP-friendly, testable).
 */
public interface IdGeneratorPort {
    String newId();
}
