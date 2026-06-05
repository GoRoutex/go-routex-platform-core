package platform.payment.service.interfaces.controller;


import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import platform.core.common.service.api.BaseRequest;
import platform.core.common.service.domain.payment.PaymentMethod;
import platform.payment.service.application.command.payment.GetPaymentUrlCommand;
import platform.payment.service.application.command.payment.GetPaymentUrlResult;
import platform.payment.service.application.command.payment.PollingPaymentStatusCommand;
import platform.payment.service.application.command.payment.PollingPaymentStatusResult;
import platform.payment.service.application.services.merchant.PaymentOrchestrationService;
import platform.payment.service.infrastructure.persistence.utils.ApiRequestUtils;
import platform.payment.service.infrastructure.persistence.utils.ApiResultFactory;
import platform.payment.service.infrastructure.persistence.utils.HttpUtils;
import platform.payment.service.interfaces.model.payment.GetPaymentUrlResponse;
import platform.payment.service.interfaces.model.payment.PollingPaymentStatus;
import vn.com.go.routex.identity.security.log.SystemLog;

import java.math.BigDecimal;

import static platform.payment.service.infrastructure.persistence.constant.ApiConstant.API_PATH;
import static platform.payment.service.infrastructure.persistence.constant.ApiConstant.API_VERSION;
import static platform.payment.service.infrastructure.persistence.constant.ApiConstant.GET_PAYMENT_URL;
import static platform.payment.service.infrastructure.persistence.constant.ApiConstant.PAYMENT_PATH;
import static platform.payment.service.infrastructure.persistence.constant.ApiConstant.POLLING_STATUS;

@RequestMapping(API_PATH + API_VERSION + PAYMENT_PATH)
@RestController
@RequiredArgsConstructor
public class PaymentOrchestrationController {

    private final PaymentOrchestrationService paymentOrchestrationService;
    private final ApiResultFactory apiResultFactory;
    private final SystemLog sLog = SystemLog.getLogger(this.getClass());

    @InitBinder
    public void initBinder(WebDataBinder webDataBinder,  WebRequest webRequest) {
        webDataBinder.setDisallowedFields("requestId", "requestDateTime", "channel", "data");
    }


    @GetMapping(POLLING_STATUS)
    public ResponseEntity<PollingPaymentStatus> getPaymentDetail(
            @RequestParam String bookingCode,
            HttpServletRequest servletRequest) {

        BaseRequest request = ApiRequestUtils.getBaseRequestOrDefault(servletRequest);
        PollingPaymentStatusResult result = paymentOrchestrationService.pollingStatus(PollingPaymentStatusCommand.builder()
                .bookingCode(bookingCode)
                .context(HttpUtils.toContext(request))
                .build());


        PollingPaymentStatus response = PollingPaymentStatus.builder()
                .requestId(request.getRequestId())
                .requestDateTime(request.getRequestDateTime())
                .channel(request.getChannel())
                .result(apiResultFactory.buildSuccess())
                .data(
                        PollingPaymentStatus.PollingPaymentStatusData.builder()
                                .bookingCode(result.bookingCode())
                                .status(result.status())
                                .amount(result.amount())
                                .build()
                )
                .build();

        sLog.info("[POLLING-STATUS] Polling Payment Status Response: {}", response);

        return HttpUtils.buildResponse(request, response);
    }
    @GetMapping(GET_PAYMENT_URL)
    public ResponseEntity<GetPaymentUrlResponse> getPaymentUrl(
            @RequestParam String bookingCode,
            @RequestParam PaymentMethod method,
            @RequestParam BigDecimal amount,
            HttpServletRequest servletRequest
    ) {

        BaseRequest request = ApiRequestUtils.getBaseRequestOrDefault(servletRequest);

        GetPaymentUrlCommand command = GetPaymentUrlCommand.builder()
                .context(HttpUtils.toContext(request))
                .amount(amount)
                .clientIp(resolveClientIp(servletRequest))
                .bookingCode(bookingCode)
                .method(method)
                .build();

        GetPaymentUrlResult result = paymentOrchestrationService.getPaymentUrl(command);


        GetPaymentUrlResponse response = GetPaymentUrlResponse.builder()
                .requestId(request.getRequestId())
                .requestDateTime(request.getRequestDateTime())
                .channel(request.getChannel())
                .result(apiResultFactory.buildSuccess())
                .data(GetPaymentUrlResponse.GetPaymentResponseData.builder()
                        .bookingCode(result.bookingCode())
                        .amount(result.amount())
                        .qrCodeUrl(result.qrCodeUrl())
                        .paymentUrl(result.paymentUrl())
                        .deeplink(result.deeplink())
                        .expiredTime(result.expiredTime())
                        .build())
                .build();



        return HttpUtils.buildResponse(request, response);
    }

    private String resolveClientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-FORWARDED-FOR");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
