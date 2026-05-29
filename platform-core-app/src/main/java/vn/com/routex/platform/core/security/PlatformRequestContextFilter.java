package vn.com.routex.platform.core.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;
import vn.com.go.routex.identity.security.jwt.JwtService;
import vn.com.go.routex.identity.security.log.SystemLog;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
@Order(Ordered.LOWEST_PRECEDENCE - 20)
@RequiredArgsConstructor
public class PlatformRequestContextFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;
    private final JwtService jwtService;
    private final SystemLog sLog = SystemLog.getLogger(this.getClass());

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        if (shouldBypass(requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }

        boolean cacheBody = shouldCacheBody(request);
        HttpServletRequest requestToUse = cacheBody ? new CachedBodyRequest(request) : request;
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        try {
            RequestEnvelope envelope = extractEnvelope(requestToUse, cacheBody);
            applyAttributes(request, envelope);
            applyJwtAttributes(request);
        } catch (RuntimeException e) {
            sLog.warn("Invalid request envelope: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Invalid Request");
            response.getWriter().flush();
            return;
        }

        try {
            filterChain.doFilter(requestToUse, responseWrapper);
        } finally {
            String responseMessage = new String(responseWrapper.getContentAsByteArray(), StandardCharsets.UTF_8);
            sLog.info("{}", responseMessage);
            responseWrapper.copyBodyToResponse();
        }
    }

    private boolean shouldBypass(String requestURI) {
        return requestURI.startsWith("/actuator/")
                || requestURI.startsWith("/swagger-ui")
                || requestURI.startsWith("/v3/api-docs")
                || requestURI.equals("/error")
                || requestURI.equals("/api/v1/payment-service/vnpay-ipn")
                || requestURI.equals("/api/v1/payment-service/return-url");
    }

    private boolean shouldCacheBody(HttpServletRequest request) {
        String method = request.getMethod();
        if ("GET".equalsIgnoreCase(method) || "DELETE".equalsIgnoreCase(method) || "OPTIONS".equalsIgnoreCase(method)) {
            return false;
        }
        String contentType = request.getContentType();
        return contentType != null && contentType.toLowerCase().startsWith(MediaType.APPLICATION_JSON_VALUE);
    }

    private RequestEnvelope extractEnvelope(HttpServletRequest request, boolean bodyRequest) throws IOException {
        if (bodyRequest) {
            String requestBody = new String(request.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            if (!requestBody.isBlank()) {
                JsonNode root = objectMapper.readTree(requestBody);
                String requestId = text(root, "requestId", "request_id");
                String requestDateTime = text(root, "requestDateTime", "request_date_time");
                String channel = text(root, "channel");
                if (hasEnvelope(requestId, requestDateTime, channel)) {
                    return new RequestEnvelope(requestId, requestDateTime, channel);
                }
            }
        }

        String requestId = firstNonBlank(
                request.getHeader("requestId"),
                request.getHeader("RT-REQUEST-ID"),
                request.getHeader("X-Request-Id"),
                request.getParameter("requestId")
        );
        String requestDateTime = firstNonBlank(
                request.getHeader("requestDateTime"),
                request.getHeader("RT-REQUEST-DATE-TIME"),
                request.getHeader("X-Request-DateTime"),
                request.getParameter("requestDateTime")
        );
        String channel = firstNonBlank(
                request.getHeader("channel"),
                request.getHeader("X-Channel"),
                request.getParameter("channel")
        );

        if (!hasEnvelope(requestId, requestDateTime, channel)) {
            throw new IllegalArgumentException("Missing request envelope");
        }
        return new RequestEnvelope(requestId, requestDateTime, channel);
    }

    private String text(JsonNode root, String... names) {
        for (String name : names) {
            JsonNode node = root.get(name);
            if (node != null && !node.isNull() && !node.asText().isBlank()) {
                return node.asText();
            }
        }
        return null;
    }

    private boolean hasEnvelope(String requestId, String requestDateTime, String channel) {
        return requestId != null && requestDateTime != null && channel != null;
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return null;
    }

    private void applyAttributes(HttpServletRequest request, RequestEnvelope envelope) {
        setAttributes(request, List.of(
                "platform.core.common.service.common.RequestAttributes",
                "platform.booking.service.infrastructure.persistence.config.RequestAttributes",
                "platform.driver.service.infrastructure.web.RequestAttributes",
                "platform.management.service.infrastructure.persistence.config.RequestAttributes",
                "platform.payment.service.infrastructure.persistence.config.RequestAttributes"
        ), "REQUEST_ID", envelope.requestId());
        setAttributes(request, List.of(
                "platform.core.common.service.common.RequestAttributes",
                "platform.booking.service.infrastructure.persistence.config.RequestAttributes",
                "platform.driver.service.infrastructure.web.RequestAttributes",
                "platform.management.service.infrastructure.persistence.config.RequestAttributes",
                "platform.payment.service.infrastructure.persistence.config.RequestAttributes"
        ), "REQUEST_DATE_TIME", envelope.requestDateTime());
        setAttributes(request, List.of(
                "platform.core.common.service.common.RequestAttributes",
                "platform.booking.service.infrastructure.persistence.config.RequestAttributes",
                "platform.driver.service.infrastructure.web.RequestAttributes",
                "platform.management.service.infrastructure.persistence.config.RequestAttributes",
                "platform.payment.service.infrastructure.persistence.config.RequestAttributes"
        ), "CHANNEL", envelope.channel());
    }

    private void applyJwtAttributes(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return;
        }
        try {
            String token = authorization.substring(7);
            var claims = jwtService.extractAllClaims(token);
            String merchantId = firstNonBlank(claims.get("merchantId", String.class), String.valueOf(claims.get("merchant_id")));
            String email = firstNonBlank(claims.get("email", String.class), claims.getSubject());
            String phone = firstNonBlank(claims.get("phone", String.class), claims.get("phone_number", String.class));

            setOptionalAttributes(request, "MERCHANT_ID", merchantId);
            setOptionalAttributes(request, "USER_EMAIL", email);
            setOptionalAttributes(request, "USER_PHONE", phone);
        } catch (Exception ignored) {
            // Authentication filters handle invalid tokens.
        }
    }

    private void setOptionalAttributes(HttpServletRequest request, String fieldName, String value) {
        if (value == null || value.isBlank() || "null".equals(value)) {
            return;
        }
        setAttributes(request, List.of(
                "platform.core.common.service.common.RequestAttributes",
                "platform.booking.service.infrastructure.persistence.config.RequestAttributes",
                "platform.driver.service.infrastructure.web.RequestAttributes",
                "platform.management.service.infrastructure.persistence.config.RequestAttributes",
                "platform.payment.service.infrastructure.persistence.config.RequestAttributes"
        ), fieldName, value);
    }

    private void setAttributes(HttpServletRequest request, List<String> classNames, String fieldName, String value) {
        for (String className : classNames) {
            try {
                Class<?> type = Class.forName(className);
                Object attributeName = type.getField(fieldName).get(null);
                if (attributeName instanceof String name) {
                    request.setAttribute(name, value);
                }
            } catch (ReflectiveOperationException ignored) {
                // Attribute does not exist in every module.
            }
        }
    }

    private record RequestEnvelope(String requestId, String requestDateTime, String channel) {}

    private static final class CachedBodyRequest extends HttpServletRequestWrapper {
        private final byte[] body;

        private CachedBodyRequest(HttpServletRequest request) throws IOException {
            super(request);
            this.body = request.getInputStream().readAllBytes();
        }

        @Override
        public jakarta.servlet.ServletInputStream getInputStream() {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(body);
            return new jakarta.servlet.ServletInputStream() {
                @Override
                public int read() {
                    return inputStream.read();
                }

                @Override
                public boolean isFinished() {
                    return inputStream.available() == 0;
                }

                @Override
                public boolean isReady() {
                    return true;
                }

                @Override
                public void setReadListener(jakarta.servlet.ReadListener readListener) {
                    // Synchronous request body access.
                }
            };
        }
    }
}
