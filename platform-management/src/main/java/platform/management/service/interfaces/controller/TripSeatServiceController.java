package platform.management.service.interfaces.controller;


import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import platform.core.common.service.api.BaseRequest;
import platform.management.service.application.command.seat.SearchSeatCommand;
import platform.management.service.application.command.seat.SearchSeatResult;
import platform.management.service.application.services.TripSeatService;
import platform.management.service.infrastructure.persistence.utils.ApiRequestUtils;
import platform.management.service.infrastructure.persistence.utils.HttpUtils;
import platform.management.service.interfaces.factory.ApiResultFactory;
import platform.management.service.interfaces.models.seat.SearchSeatResponse;

import java.util.List;
import java.util.stream.Collectors;

import static platform.core.common.service.persistence.constant.ApiConstant.API_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.API_VERSION;
import static platform.core.common.service.persistence.constant.ApiConstant.MANAGEMENT_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.SEARCH_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.SEAT_DIAGRAM;


@RestController
@RequestMapping( API_PATH + API_VERSION + MANAGEMENT_PATH+ SEAT_DIAGRAM)
@RequiredArgsConstructor
public class TripSeatServiceController {

    private final TripSeatService tripSeatService;
    private final ApiResultFactory apiResultFactory;

    @GetMapping(SEARCH_PATH)
    public ResponseEntity<SearchSeatResponse> searchSeat(@RequestParam(defaultValue = "1") int pageNumber,
                                                         @RequestParam(defaultValue = "10") int pageSize,
                                                         @RequestParam String tripId,
                                                         HttpServletRequest servletRequest) {
        BaseRequest baseRequest = ApiRequestUtils.getBaseRequestOrDefault(servletRequest);

        SearchSeatResult result = tripSeatService.searchSeat(SearchSeatCommand.builder()
                .context(HttpUtils.toContext(baseRequest))
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .tripId(tripId)
                .build());


        List<SearchSeatResponse.SearchSeatResponseData> seatDataList = result.data()
                .stream()
                .map(r -> SearchSeatResponse.SearchSeatResponseData.builder()
                        .seatId(r.seatId())
                        .code(r.code())
                        .status(r.status())
                        .floor(r.floor())
                        .rowNo(r.rowNo())
                        .colNo(r.colNo())
                        .build())
                .collect(Collectors.toList());

        SearchSeatResponse response = SearchSeatResponse
                .builder()
                .requestId(baseRequest.getRequestId())
                .requestDateTime(baseRequest.getRequestDateTime())
                .channel(baseRequest.getChannel())
                .result(apiResultFactory.buildSuccess())
                .data(SearchSeatResponse.SearchSeatResponsePage.builder()
                        .items(seatDataList)
                        .build())
                .build();

        return HttpUtils.buildResponse(baseRequest, response);
    }
}
