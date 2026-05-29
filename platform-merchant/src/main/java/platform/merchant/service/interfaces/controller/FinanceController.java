package platform.merchant.service.interfaces.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import platform.merchant.service.application.command.finance.FetchMerchantRevenueQuery;
import platform.merchant.service.application.command.finance.FetchSystemRevenueQuery;
import platform.merchant.service.application.service.FinanceService;
import platform.core.common.service.persistence.utils.ApiRequestUtils;
import platform.merchant.service.infrastructure.persistence.utils.HttpUtils;
import platform.core.common.service.api.BaseRequest;
import platform.merchant.service.interfaces.model.finance.response.MerchantRevenueResponse;
import platform.merchant.service.interfaces.model.finance.response.SystemRevenueResponse;

import java.time.OffsetDateTime;

import static platform.core.common.service.persistence.constant.ApiConstant.API_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.API_VERSION;
import static platform.core.common.service.persistence.constant.ApiConstant.FINANCE_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.MERCHANT_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.MERCHANT_SERVICE;
import static platform.core.common.service.persistence.constant.ApiConstant.REVENUE_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.SYSTEM_PATH;

@RestController
@RequestMapping(API_PATH + API_VERSION + MERCHANT_SERVICE + FINANCE_PATH)
@RequiredArgsConstructor
@PreAuthorize("hasRole('MERCHANT_OWNER')")
public class FinanceController {

    private final FinanceService financeService;

    @InitBinder
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.setDisallowedFields("requestId", "requestDateTime", "channel", "data");
    }

    @GetMapping(REVENUE_PATH + SYSTEM_PATH)
    public ResponseEntity<SystemRevenueResponse> getSystemRevenue(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime endDate,
            HttpServletRequest servletRequest) {

        BaseRequest baseRequest = ApiRequestUtils.getBaseRequestOrDefault(servletRequest);

        FetchSystemRevenueQuery query = FetchSystemRevenueQuery.builder()
                .startDate(startDate)
                .endDate(endDate)
                .context(ApiRequestUtils.getRequestContext(baseRequest))
                .build();

        SystemRevenueResponse response = financeService.getSystemRevenue(query);
        return HttpUtils.buildResponse(baseRequest, response);
    }

    @GetMapping(REVENUE_PATH + MERCHANT_PATH)
    public ResponseEntity<MerchantRevenueResponse> getMerchantRevenue(
            @RequestParam(required = false) String merchantId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime endDate,
            HttpServletRequest servletRequest) {

        BaseRequest baseRequest = ApiRequestUtils.getBaseRequestOrDefault(servletRequest);

        String resolvedMerchantId = (merchantId != null) ? merchantId : ApiRequestUtils.getMerchantId(servletRequest);

        FetchMerchantRevenueQuery query = FetchMerchantRevenueQuery.builder()
                .merchantId(resolvedMerchantId)
                .startDate(startDate)
                .endDate(endDate)
                .context(ApiRequestUtils.getRequestContext(baseRequest))
                .build();

        MerchantRevenueResponse response = financeService.getMerchantRevenue(query);
        return HttpUtils.buildResponse(baseRequest, response);
    }
}

