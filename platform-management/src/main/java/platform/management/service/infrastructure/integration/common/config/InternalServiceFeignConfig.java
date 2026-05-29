package platform.management.service.infrastructure.integration.common.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.RequestInterceptor;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import platform.management.service.infrastructure.persistence.config.RequestAttributes;
import platform.management.service.infrastructure.persistence.exception.feign.CustomerFeignException;
import platform.core.common.service.api.BaseResponse;
import platform.core.common.service.api.ApiResult;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static platform.management.service.infrastructure.persistence.constant.ErrorConstant.SYSTEM_ERROR;
import static platform.management.service.infrastructure.persistence.constant.ErrorConstant.SYSTEM_ERROR_MESSAGE;

@Configuration
public class InternalServiceFeignConfig {

    @Bean
    RequestInterceptor internalServiceRequestInterceptor() {
        return requestTemplate -> {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                var request = attributes.getRequest();
                copyHeader(requestTemplate, RequestAttributes.REQUEST_ID, request.getHeader(RequestAttributes.REQUEST_ID));
                copyHeader(requestTemplate, RequestAttributes.REQUEST_DATE_TIME, request.getHeader(RequestAttributes.REQUEST_DATE_TIME));
                copyHeader(requestTemplate, RequestAttributes.CHANNEL, request.getHeader(RequestAttributes.CHANNEL));
                copyHeader(requestTemplate, "Authorization", request.getHeader("Authorization"));
            }

            ensureHeader(requestTemplate, RequestAttributes.REQUEST_ID, UUID.randomUUID().toString());
            ensureHeader(requestTemplate, RequestAttributes.REQUEST_DATE_TIME,
                    OffsetDateTime.now(ZoneOffset.ofHours(7)).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
            ensureHeader(requestTemplate, RequestAttributes.CHANNEL, "ONL");
        };
    }

    @Bean
    ErrorDecoder internalServiceErrorDecoder(ObjectMapper objectMapper) {
        return new InternalServiceErrorDecoder(objectMapper);
    }

    private static void copyHeader(feign.RequestTemplate template, String name, String value) {
        if (value != null && !value.isBlank()) {
            template.header(name, value);
        }
    }

    private static void ensureHeader(feign.RequestTemplate template, String name, String fallbackValue) {
        if (!template.headers().containsKey(name) || template.headers().get(name).isEmpty()) {
            template.header(name, fallbackValue);
        }
    }

    private static final class InternalServiceErrorDecoder implements ErrorDecoder {

        private final ObjectMapper objectMapper;
        private final ErrorDecoder defaultDecoder = new Default();

        private InternalServiceErrorDecoder(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
        }

        @Override
        public Exception decode(String methodKey, Response response) {
            if (response.body() == null) {
                return new CustomerFeignException(response.status(), systemError());
            }

            try {
                BaseResponse<Void> body = objectMapper.readValue(response.body().asInputStream(), new TypeReference<>() {
                });
                ApiResult result = body.getResult() == null ? systemError() : body.getResult();
                return new CustomerFeignException(response.status(), result);
            } catch (IOException ex) {
                return defaultDecoder.decode(methodKey, response);
            }
        }

        private ApiResult systemError() {
            return ApiResult.builder().responseCode(SYSTEM_ERROR).description(SYSTEM_ERROR_MESSAGE).build();
        }
    }
}
