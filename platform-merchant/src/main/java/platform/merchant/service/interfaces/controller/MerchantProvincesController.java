package platform.merchant.service.interfaces.controller;


import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.com.go.routex.identity.security.log.SystemLog;
import platform.merchant.service.application.command.provinces.FetchProvincesQuery;
import platform.merchant.service.application.command.provinces.FetchProvincesResult;
import platform.merchant.service.application.command.provinces.SearchProvincesQuery;
import platform.merchant.service.application.command.provinces.SearchProvincesResult;
import platform.merchant.service.application.service.ProvincesManagementService;
import platform.core.common.service.persistence.utils.ApiRequestUtils;
import platform.merchant.service.infrastructure.persistence.utils.HttpUtils;
import platform.merchant.service.interfaces.factory.ApiResultFactory;
import platform.core.common.service.api.BaseRequest;
import platform.merchant.service.interfaces.model.provinces.FetchProvincesResponse;
import platform.merchant.service.interfaces.model.provinces.SearchProvincesResponse;

import java.util.List;
import java.util.stream.Collectors;

import static platform.core.common.service.persistence.constant.ApiConstant.API_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.API_VERSION;
import static platform.core.common.service.persistence.constant.ApiConstant.FETCH_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.MERCHANT_SERVICE;
import static platform.core.common.service.persistence.constant.ApiConstant.PROVINCES;
import static platform.core.common.service.persistence.constant.ApiConstant.SEARCH_PATH;

@RequiredArgsConstructor
@RestController
@RequestMapping(API_PATH + API_VERSION + MERCHANT_SERVICE)
public class MerchantProvincesController {

    private final ApiResultFactory apiResultFactory;
    private final ProvincesManagementService provincesManagementService;
    private final SystemLog sLog = SystemLog.getLogger(this.getClass());

    @InitBinder
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.setDisallowedFields("requestId", "requestDateTime", "channel", "data");
    }

    @GetMapping(PROVINCES + SEARCH_PATH)
    public ResponseEntity<SearchProvincesResponse> searchProvinces(
            @RequestParam String keyword,
            @RequestParam int page,
            @RequestParam int size) {

        SearchProvincesResult result = provincesManagementService.searchProvinces(SearchProvincesQuery.builder()
                .keyword(keyword)
                .page(page)
                .size(size)
                .build());

        SearchProvincesResponse response = SearchProvincesResponse.builder()
                .data(result.data().stream()
                        .map(item -> SearchProvincesResponse.SearchProvincesResponseData.builder()
                                .id(item.id())
                                .name(item.name())
                                .code(item.code())
                                .type(item.type())
                                .build())
                        .toList())
                .build();

        sLog.info("[SEARCH-PROVINCES] Search Provinces Response: {}", response);
        return ResponseEntity.ok(response);
    }

    @GetMapping(PROVINCES + FETCH_PATH)
    public ResponseEntity<FetchProvincesResponse> fetchProvinces(
            HttpServletRequest servletRequest,
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        BaseRequest baseRequest = ApiRequestUtils.getBaseRequestOrDefault(servletRequest);

        FetchProvincesResult result = provincesManagementService.fetchProvinces(
                FetchProvincesQuery.builder()
                        .context(HttpUtils.toContext(baseRequest))
                        .pageSize(String.valueOf(pageSize))
                        .pageNumber(String.valueOf(pageNumber))
                        .build()
        );

        List<FetchProvincesResponse.FetchProvincesResponseData> dataList = result.items().stream()
                .map(p -> FetchProvincesResponse.FetchProvincesResponseData.builder()
                        .id(p.id())
                        .name(p.name())
                        .code(p.code())
                        .build())
                .collect(Collectors.toList());

        FetchProvincesResponse response = FetchProvincesResponse.builder()
                .requestId(baseRequest.getRequestId())
                .requestDateTime(baseRequest.getRequestDateTime())
                .channel(baseRequest.getChannel())
                .result(apiResultFactory.buildSuccess())
                .data(FetchProvincesResponse.FetchProvincesResponsePage.builder()
                        .items(dataList)
                        .pagination(FetchProvincesResponse.Pagination.builder()
                                .pageNumber(result.pageNumber())
                                .pageSize(result.pageSize())
                                .totalElements(result.totalElements())
                                .totalPages(result.totalPages())
                                .build())
                        .build())
                .build();


        sLog.info("[FETCH-PROVINCES] Fetch Provinces Response: {}", response);
        return HttpUtils.buildResponse(baseRequest, response);
    }

}
