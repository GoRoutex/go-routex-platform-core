package platform.core.common.service.persistence.utils;

import lombok.experimental.UtilityClass;
import platform.core.common.service.persistence.exception.BusinessException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import static platform.core.common.service.persistence.constant.ErrorConstant.INVALID_INPUT_ERROR;


@UtilityClass
public class DateTimeUtils {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public int parseIntOrThrow(String v, String field, String requestId, String requestDateTime, String channel) {
        if (v == null || v.isBlank()) {
            throw new BusinessException(requestId, requestDateTime, channel,
                    ExceptionUtils.buildResultResponse(INVALID_INPUT_ERROR, field + " is required"));
        }
        if (!v.trim().matches("^\\d{1,3}$")) {
            throw new BusinessException(requestId, requestDateTime, channel,
                    ExceptionUtils.buildResultResponse(INVALID_INPUT_ERROR, field + " must be numeric"));
        }
        return Integer.parseInt(v.trim());
    }

    public LocalDate toLocalDate(String v) {
        if(v == null || v.isBlank()) {
            throw new BusinessException(ExceptionUtils.buildResultResponse(INVALID_INPUT_ERROR, "LocalDate String is Null or blank"));
        }

        try {
            return LocalDate.parse(v, FORMATTER);
        } catch (DateTimeParseException e) {
            throw new BusinessException(ExceptionUtils.buildResultResponse(INVALID_INPUT_ERROR, "LocalDate String is invalid"));
        }
    }
}
