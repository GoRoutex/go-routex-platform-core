package platform.driver.service.interfaces.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import platform.core.common.service.api.ApiResult;
import platform.core.common.service.persistence.utils.HttpUtils;
import platform.driver.service.application.dto.passengers.PassengerCheckinCommand;
import platform.driver.service.application.dto.passengers.PassengerCheckinResult;
import platform.driver.service.application.dto.passengers.TripLifecycleCommand;
import platform.driver.service.application.dto.passengers.TripLifecycleResult;
import platform.driver.service.application.services.TripPassengerService;
import platform.driver.service.interfaces.models.passengers.PassengerCheckinRequest;
import platform.driver.service.interfaces.models.passengers.PassengerCheckinResponse;
import platform.driver.service.interfaces.models.passengers.TripLifecycleRequest;
import platform.driver.service.interfaces.models.passengers.TripLifecycleResponse;

import static platform.core.common.service.persistence.constant.ApiConstant.API_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.API_VERSION;
import static platform.core.common.service.persistence.constant.ApiConstant.BOARDING_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.CHECKIN_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.COMPLETE_TRIP_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.START_TRIP_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.TRIP_PASSENGERS;


@RestController
@RequestMapping(API_PATH + API_VERSION + TRIP_PASSENGERS)
@RequiredArgsConstructor
public class TripPassengersController {

    private final TripPassengerService tripPassengerService;

    @InitBinder
    public void initBinder(WebDataBinder webDataBinder, WebRequest webRequest) {
        webDataBinder.setDisallowedFields("requestId", "requestDateTime", "channel", "data");
    }

    @PostMapping(CHECKIN_PATH)
    public ResponseEntity<PassengerCheckinResponse> checkIn(@Valid @RequestBody PassengerCheckinRequest request) {
        PassengerCheckinResult result = tripPassengerService.checkInAction(toCommand(request));
        return HttpUtils.buildResponse(request, toResponse(request, result));
    }

    @PostMapping(BOARDING_PATH)
    public ResponseEntity<PassengerCheckinResponse> board(@Valid @RequestBody PassengerCheckinRequest request) {
        PassengerCheckinResult result = tripPassengerService.boardAction(toCommand(request));
        return HttpUtils.buildResponse(request, toResponse(request, result));
    }

    @PostMapping(START_TRIP_PATH)
    public ResponseEntity<TripLifecycleResponse> startTrip(@Valid @RequestBody TripLifecycleRequest request) {
        TripLifecycleResult result = tripPassengerService.startTrip(toTripLifecycleCommand(request));
        return HttpUtils.buildResponse(request, toTripLifecycleResponse(request, result));
    }

    @PostMapping(COMPLETE_TRIP_PATH)
    public ResponseEntity<TripLifecycleResponse> completeTrip(@Valid @RequestBody TripLifecycleRequest request) {
        TripLifecycleResult result = tripPassengerService.completeTrip(toTripLifecycleCommand(request));
        return HttpUtils.buildResponse(request, toTripLifecycleResponse(request, result));
    }

    private PassengerCheckinCommand toCommand(PassengerCheckinRequest request) {
        return PassengerCheckinCommand.builder()
                .context(HttpUtils.toContext(request))
                .ticketId(request.getData().getTicketId())
                .performedBy(request.getData().getPerformedBy())
                .deviceId(request.getData().getDeviceId())
                .build();
    }

    private TripLifecycleCommand toTripLifecycleCommand(TripLifecycleRequest request) {
        return TripLifecycleCommand.builder()
                .context(HttpUtils.toContext(request))
                .tripId(request.getData().getTripId())
                .performedBy(request.getData().getPerformedBy())
                .deviceId(request.getData().getDeviceId())
                .build();
    }

    private PassengerCheckinResponse toResponse(PassengerCheckinRequest request, PassengerCheckinResult result) {
        return PassengerCheckinResponse.builder()
                .requestId(request.getRequestId())
                .requestDateTime(request.getRequestDateTime())
                .channel(request.getChannel())
                .result(ApiResult.buildSuccess())
                .data(PassengerCheckinResponse.PassengerCheckinResponseData.builder()
                        .ticketCode(result.ticketCode())
                        .customerName(result.customerName())
                        .seatNumber(result.seatNumber())
                        .tripId(result.tripId())
                        .status(result.status())
                        .checkedInAt(result.checkedInAt())
                        .boardedAt(result.boardedAt())
                        .build())
                .build();
    }

    private TripLifecycleResponse toTripLifecycleResponse(TripLifecycleRequest request, TripLifecycleResult result) {
        return TripLifecycleResponse.builder()
                .requestId(request.getRequestId())
                .requestDateTime(request.getRequestDateTime())
                .channel(request.getChannel())
                .result(ApiResult.buildSuccess())
                .data(TripLifecycleResponse.TripLifecycleResponseData.builder()
                        .tripId(result.tripId())
                        .tripStatus(result.tripStatus())
                        .totalTickets(result.totalTickets())
                        .boardedTickets(result.boardedTickets())
                        .completedTickets(result.completedTickets())
                        .expiredTickets(result.expiredTickets())
                        .unchangedTickets(result.unchangedTickets())
                        .build())
                .build();
    }
}
