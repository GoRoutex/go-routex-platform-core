package platform.management.service.infrastructure.persistence.utils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;
import platform.core.common.service.persistence.config.ApplicationConfig;
import platform.core.common.service.persistence.exception.BusinessException;
import platform.core.common.service.persistence.utils.ExceptionUtils;

import static platform.core.common.service.persistence.constant.ErrorConstant.TIMEOUT_ERROR;
import static platform.core.common.service.persistence.constant.ErrorConstant.TIMEOUT_ERROR_MESSAGE;


@UtilityClass

public class JsonUtils {

    private final ObjectMapper objectMapper = ApplicationConfig.getObjectMapper();

    public <T> T convertValue(Object source, Class<T> clazz) {
        try {
            return objectMapper.convertValue(source, clazz);
        } catch (Exception e) {
            throw new BusinessException(ExceptionUtils.buildResultResponse(TIMEOUT_ERROR, TIMEOUT_ERROR_MESSAGE));
        }
    }


    public String parseToJsonStr(Object message) throws JsonProcessingException {
        try {
            return objectMapper.writeValueAsString(message);

        } catch (Exception e) {
            throw new BusinessException(ExceptionUtils.buildResultResponse(TIMEOUT_ERROR, TIMEOUT_ERROR_MESSAGE));
        }
    }

    public Object parseToObject(String message, Class<Object> clazz) throws JsonProcessingException {
        try {
            return objectMapper.readValue(message, clazz);
        } catch (Exception e) {
            throw new BusinessException(ExceptionUtils.buildResultResponse(TIMEOUT_ERROR, TIMEOUT_ERROR_MESSAGE));
        }
    }

    public <T> T parseToKafkaObject(String message, TypeReference<T> type) {
        try {
            return objectMapper.readValue(message, type);
        } catch (Exception e) {
            throw new BusinessException(ExceptionUtils.buildResultResponse(TIMEOUT_ERROR, TIMEOUT_ERROR_MESSAGE));
        }
    }

}
