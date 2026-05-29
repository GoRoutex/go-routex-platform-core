package platform.merchant.service.interfaces.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import platform.merchant.service.application.command.campaign.ApplyPromotionCommand;
import platform.merchant.service.application.command.campaign.ApplyPromotionResult;
import platform.merchant.service.application.command.campaign.CreateCampaignCommand;
import platform.merchant.service.application.command.campaign.CreateCampaignResult;
import platform.merchant.service.application.command.campaign.ValidatePromotionCommand;
import platform.merchant.service.application.command.campaign.ValidatePromotionResult;
import platform.merchant.service.application.query.campaign.FetchCampaignsQuery;
import platform.merchant.service.application.query.campaign.FetchCampaignsResult;
import platform.merchant.service.application.service.CampaignService;
import platform.merchant.service.infrastructure.persistence.utils.HttpUtils;
import vn.com.go.routex.identity.security.log.SystemLog;
import platform.core.common.service.persistence.utils.ApiRequestUtils;
import platform.merchant.service.interfaces.factory.ApiResultFactory;
import platform.core.common.service.api.BaseRequest;
import platform.merchant.service.interfaces.model.campaign.request.ApplyPromotionRequest;
import platform.merchant.service.interfaces.model.campaign.request.CreateCampaignRequest;
import platform.merchant.service.interfaces.model.campaign.request.ValidatePromotionRequest;
import platform.merchant.service.interfaces.model.campaign.response.ApplyPromotionResponse;
import platform.merchant.service.interfaces.model.campaign.response.CreateCampaignResponse;
import platform.merchant.service.interfaces.model.campaign.response.FetchCampaignsResponse;
import platform.merchant.service.interfaces.model.campaign.response.ValidatePromotionResponse;

import java.util.stream.Collectors;

import static platform.core.common.service.persistence.constant.ApiConstant.API_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.API_VERSION;
import static platform.core.common.service.persistence.constant.ApiConstant.APPLY_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.CAMPAIGN_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.CREATE_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.FETCH_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.MERCHANT_SERVICE;
import static platform.core.common.service.persistence.constant.ApiConstant.VALIDATE_PATH;

@RestController
@RequestMapping(API_PATH + API_VERSION + MERCHANT_SERVICE)
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('campaign:management') or hasRole('MERCHANT_OWNER')")
public class MerchantCampaignController {

    private final CampaignService campaignService;
    private final ApiResultFactory apiResultFactory;
    private final SystemLog sLog = SystemLog.getLogger(this.getClass());

    @InitBinder
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.setDisallowedFields("requestId", "requestDateTime", "channel", "data");
    }

    @PostMapping(CAMPAIGN_PATH + CREATE_PATH)
    public ResponseEntity<CreateCampaignResponse> createCampaign(@Valid @RequestBody CreateCampaignRequest request,
                                                               HttpServletRequest servletRequest) {
        sLog.info("[CAMPAIGN-MANAGEMENT] Create Campaign Request: {}", request);
        String merchantId = ApiRequestUtils.requireMerchantId(servletRequest, request);

        CreateCampaignResult result = campaignService.createCampaign(CreateCampaignCommand.builder()
                .context(HttpUtils.toContext(request, merchantId))
                .merchantId(merchantId)
                .name(request.getData().getName())
                .description(request.getData().getDescription())
                .promotionCode(request.getData().getPromotionCode())
                .discountType(request.getData().getDiscountType())
                .discountValue(request.getData().getDiscountValue())
                .maxDiscountAmount(request.getData().getMaxDiscountAmount())
                .minOrderAmount(request.getData().getMinOrderAmount())
                .startDate(request.getData().getStartDate())
                .endDate(request.getData().getEndDate())
                .usageLimit(request.getData().getUsageLimit())
                .creator(request.getData().getCreator())
                .build());

        CreateCampaignResponse response = CreateCampaignResponse.builder()
                .result(apiResultFactory.buildSuccess())
                .data(CreateCampaignResponse.CreateCampaignResponseData.builder()
                        .id(result.id())
                        .promotionCode(result.promotionCode())
                        .build())
                .build();

        return HttpUtils.buildResponse(request, response);
    }

    @PostMapping(CAMPAIGN_PATH + APPLY_PATH)
    public ResponseEntity<ApplyPromotionResponse> applyPromotion(@Valid @RequestBody ApplyPromotionRequest request) {
        sLog.info("[CAMPAIGN-MANAGEMENT] Apply Promotion Request: {}", request);

        ApplyPromotionResult result = campaignService.applyPromotion(ApplyPromotionCommand.builder()
                .context(HttpUtils.toContext(request, request.getData().getMerchantId()))
                .merchantId(request.getData().getMerchantId())
                .promotionCode(request.getData().getPromotionCode())
                .orderAmount(request.getData().getOrderAmount())
                .build());

        ApplyPromotionResponse response = ApplyPromotionResponse.builder()
                .result(apiResultFactory.buildSuccess())
                .data(ApplyPromotionResponse.ApplyPromotionResponseData.builder()
                        .campaignId(result.campaignId())
                        .promotionCode(result.promotionCode())
                        .discountAmount(result.discountAmount())
                        .finalAmount(result.finalAmount())
                        .build())
                .build();

        return HttpUtils.buildResponse(request, response);
    }

    @PostMapping(CAMPAIGN_PATH + VALIDATE_PATH)
    public ResponseEntity<ValidatePromotionResponse> validatePromotion(@Valid @RequestBody ValidatePromotionRequest request) {
        sLog.info("[CAMPAIGN-MANAGEMENT] Validate Promotion Request: {}", request);

        ValidatePromotionResult result = campaignService.validatePromotion(ValidatePromotionCommand.builder()
                .context(HttpUtils.toContext(request, request.getData().getMerchantId()))
                .merchantId(request.getData().getMerchantId())
                .promotionCode(request.getData().getPromotionCode())
                .orderAmount(request.getData().getOrderAmount())
                .build());

        ValidatePromotionResponse response = ValidatePromotionResponse.builder()
                .result(apiResultFactory.buildSuccess())
                .data(ValidatePromotionResponse.ValidatePromotionResponseData.builder()
                        .campaignId(result.campaignId())
                        .promotionCode(result.promotionCode())
                        .valid(result.valid())
                        .discountAmount(result.discountAmount())
                        .finalAmount(result.finalAmount())
                        .message(result.message())
                        .build())
                .build();

        return HttpUtils.buildResponse(request, response);
    }

    @GetMapping(CAMPAIGN_PATH + FETCH_PATH)
    public ResponseEntity<FetchCampaignsResponse> fetchCampaigns(HttpServletRequest servletRequest,
                                                               @RequestParam(defaultValue = "1") int pageNumber,
                                                               @RequestParam(defaultValue = "10") int pageSize) {
        BaseRequest baseRequest = ApiRequestUtils.getBaseRequestOrDefault(servletRequest);
        String merchantId = ApiRequestUtils.requireMerchantId(servletRequest, baseRequest);

        FetchCampaignsResult result = campaignService.fetchCampaigns(FetchCampaignsQuery.builder()
                .context(HttpUtils.toContext(baseRequest, merchantId))
                .merchantId(merchantId)
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .build());

        FetchCampaignsResponse response = FetchCampaignsResponse.builder()
                .result(apiResultFactory.buildSuccess())
                .data(FetchCampaignsResponse.FetchCampaignsResponsePage.builder()
                        .items(result.items().stream().map(c -> FetchCampaignsResponse.FetchCampaignsResponseData.builder()
                                .id(c.getId())
                                .name(c.getName())
                                .description(c.getDescription())
                                .promotionCode(c.getPromotionCode())
                                .discountType(c.getDiscountType())
                                .discountValue(c.getDiscountValue())
                                .maxDiscountAmount(c.getMaxDiscountAmount())
                                .minOrderAmount(c.getMinOrderAmount())
                                .startDate(c.getStartDate())
                                .endDate(c.getEndDate())
                                .usageLimit(c.getUsageLimit())
                                .usedCount(c.getUsedCount())
                                .status(c.getStatus())
                                .createdAt(c.getCreatedAt())
                                .build()).collect(Collectors.toList()))
                        .pagination(FetchCampaignsResponse.Pagination.builder()
                                .pageNumber(result.pageNumber())
                                .pageSize(result.pageSize())
                                .totalElements(result.totalElements())
                                .totalPages(result.totalPages())
                                .build())
                        .build())
                .build();

        return HttpUtils.buildResponse(baseRequest, response);
    }
}
