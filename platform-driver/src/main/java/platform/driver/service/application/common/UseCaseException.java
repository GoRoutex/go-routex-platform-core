package platform.driver.service.application.common;

import lombok.Getter;

@Getter
public class UseCaseException extends RuntimeException {
    private final String code;
    private final String description;

    public UseCaseException(String code, String description) {
        super(code + ": " + description);
        this.code = code;
        this.description = description;
    }
}
