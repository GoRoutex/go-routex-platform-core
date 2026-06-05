package platform.merchant.service.interfaces.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import platform.core.common.service.api.BaseRequest;
import platform.core.common.service.persistence.utils.ApiRequestUtils;
import platform.merchant.service.application.command.wards.FetchWardsQuery;
import platform.merchant.service.application.command.wards.FetchWardsResult;
import platform.merchant.service.application.command.wards.SearchWardsQuery;
import platform.merchant.service.application.command.wards.SearchWardsResult;
import platform.merchant.service.application.service.WardManagementService;
import platform.merchant.service.infrastructure.persistence.utils.HttpUtils;
import platform.merchant.service.interfaces.factory.ApiResultFactory;
import platform.merchant.service.interfaces.model.wards.FetchWardsResponse;
import platform.merchant.service.interfaces.model.wards.SearchWardsResponse;

import java.util.List;
import java.util.stream.Collectors;

import static platform.core.common.service.persistence.constant.ApiConstant.API_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.API_VERSION;
import static platform.core.common.service.persistence.constant.ApiConstant.FETCH_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.MERCHANT_SERVICE;
import static platform.core.common.service.persistence.constant.ApiConstant.SEARCH_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.WARDS;

@RequiredArgsConstructor
@RestController
@RequestMapping(API_PATH + API_VERSION + MERCHANT_SERVICE)
@PreAuthorize("hasRole('MERCHANT_OWNER') or hasAuthority('ward:management')")
public class MerchantWardsController {

    private final ApiResultFactory apiResultFactory;
    private final WardManagementService wardManagementService;

    @InitBinder
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.setDisallowedFields("requestId", "requestDateTime", "channel", "data");
    }

    @GetMapping(WARDS + FETCH_PATH)
    public ResponseEntity<FetchWardsResponse> fetchWards(
            HttpServletRequest servletRequest,
            @RequestParam(required = false) String provinceId,
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        BaseRequest baseRequest = ApiRequestUtils.getBaseRequestOrDefault(servletRequest);

        FetchWardsResult result = wardManagementService.fetchWards(
                FetchWardsQuery.builder()
                        .context(HttpUtils.toContext(baseRequest))
                        .provinceId(provinceId)
                        .pageSize(String.valueOf(pageSize))
                        .pageNumber(String.valueOf(pageNumber))
                        .build()
        );

        List<FetchWardsResponse.FetchWardsResponseData> dataList = result.items().stream()
                .map(w -> FetchWardsResponse.FetchWardsResponseData.builder()
                        .id(w.id())
                        .name(w.name())
                        .provinceId(w.provinceId())
                        .build())
                .collect(Collectors.toList());

        FetchWardsResponse response = FetchWardsResponse.builder()
                .requestId(baseRequest.getRequestId())
                .requestDateTime(baseRequest.getRequestDateTime())
                .channel(baseRequest.getChannel())
                .result(apiResultFactory.buildSuccess())
                .data(FetchWardsResponse.FetchWardsResponsePage.builder()
                        .items(dataList)
                        .pagination(FetchWardsResponse.Pagination.builder()
                                .pageNumber(result.pageNumber())
                                .pageSize(result.pageSize())
                                .totalElements(result.totalElements())
                                .totalPages(result.totalPages())
                                .build())
                        .build())
                .build();

        return HttpUtils.buildResponse(baseRequest, response);
    }

    @GetMapping(WARDS + SEARCH_PATH)
    public ResponseEntity<SearchWardsResponse> searchWards(
            HttpServletRequest servletRequest,
            @RequestParam String keyword,
            @RequestParam(required = false) String provinceId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        BaseRequest baseRequest = ApiRequestUtils.getBaseRequestOrDefault(servletRequest);

        SearchWardsResult result = wardManagementService.searchWards(
                SearchWardsQuery.builder()
                        .context(HttpUtils.toContext(baseRequest))
                        .keyword(keyword)
                        .provinceId(provinceId)
                        .page(page)
                        .size(size)
                        .build()
        );

        List<SearchWardsResponse.SearchWardsResponseData> dataList = result.data().stream()
                .map(w -> SearchWardsResponse.SearchWardsResponseData.builder()
                        .id(w.id())
                        .name(w.name())
                        .provinceId(w.provinceId())
                        .build())
                .collect(Collectors.toList());

        SearchWardsResponse response = SearchWardsResponse.builder()
                .requestId(baseRequest.getRequestId())
                .requestDateTime(baseRequest.getRequestDateTime())
                .channel(baseRequest.getChannel())
                .result(apiResultFactory.buildSuccess())
                .data(dataList)
                .build();

        return HttpUtils.buildResponse(baseRequest, response);
    }
}
