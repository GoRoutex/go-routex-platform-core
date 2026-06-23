package platform.booking.service.interfaces.controller;

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
import platform.booking.service.application.command.seat.HoldRoundTripSeatCommand;
import platform.booking.service.application.command.seat.HoldRoundTripSeatResult;
import platform.booking.service.application.command.seat.HoldSeatCommand;
import platform.booking.service.application.command.seat.HoldSeatResult;
import platform.booking.service.application.services.HoldSeatService;
import platform.booking.service.interfaces.models.seat.HoldRoundTripSeatRequest;
import platform.booking.service.interfaces.models.seat.HoldRoundTripSeatResponse;
import platform.booking.service.interfaces.models.seat.HoldSeatRequest;
import platform.booking.service.interfaces.models.seat.HoldSeatResponse;
import platform.core.common.service.api.ApiResult;
import platform.core.common.service.persistence.utils.HttpUtils;
import vn.com.go.routex.identity.security.log.SystemLog;

import java.util.List;
import java.util.stream.Collectors;

import static platform.core.common.service.persistence.constant.ApiConstant.API_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.API_VERSION;
import static platform.core.common.service.persistence.constant.ApiConstant.BOOKING_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.HOLD_SEAT_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.ROUND_TRIP_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.TRIP_PATH;


@RestController
@RequestMapping(API_PATH + API_VERSION + BOOKING_PATH)
@RequiredArgsConstructor
public class TripSeatManagementController {

    private final HoldSeatService holdSeatService;
    private final SystemLog sLog = SystemLog.getLogger(this.getClass());

    @InitBinder
    public void initBinder(WebDataBinder webDataBinder, WebRequest webRequest) {
        webDataBinder.setDisallowedFields("requestId", "requestDateTime", "channel", "data");
    }

    @PostMapping(TRIP_PATH + HOLD_SEAT_PATH)
    public ResponseEntity<HoldSeatResponse> holdSeat(@Valid @RequestBody HoldSeatRequest request) {
        sLog.info("[HOLD-SEAT] Hold Seat Request: {}", request);

        HoldSeatResult result = holdSeatService.holdSeat(HoldSeatCommand.builder()
                .context(HttpUtils.toContext(request))
                .creator(request.getCreator())
                .tripId(request.getData().getTripId())
                .seatNos(request.getData().getSeatNos())
                .holdBy(request.getData().getHoldBy())
                .customerName(request.getInfo().getCustomerName())
                .customerPhone(request.getInfo().getCustomerPhone())
                .customerEmail(request.getInfo().getCustomerEmail())
                .pickupType(request.getData().getPickupType())
                .pickupStopId(request.getData().getPickupStopId())
                .pickupAddress(request.getData().getPickupAddress())
                .dropOffType(request.getData().getDropOffType())
                .dropOffStopId(request.getData().getDropOffStopId())
                .dropOffAddress(request.getData().getDropOffAddress())
                .build());

        List<HoldSeatResponse.HoldSeatResponseData> responseData = toHoldSeatResponseData(result);

        HoldSeatResponse.HoldSeatResponseBookingInfo bookingInfo = getHoldSeatResponseBookingInfo(result);

        HoldSeatResponse response = HoldSeatResponse.builder()
                .requestId(request.getRequestId())
                .requestDateTime(request.getRequestDateTime())
                .channel(request.getChannel())
                .result(ApiResult.buildSuccess())
                .data(responseData)
                .booking(bookingInfo)
                .build();


        sLog.info("[HOLD-SEAT] Hold Seat Response: {}", response);

        return HttpUtils.buildResponse(request, response);
    }

    @PostMapping(TRIP_PATH + HOLD_SEAT_PATH + ROUND_TRIP_PATH)
    public ResponseEntity<HoldRoundTripSeatResponse> holdRoundTripSeat(@Valid @RequestBody HoldRoundTripSeatRequest request) {
        sLog.info("[HOLD-ROUND-TRIP-SEAT] Hold Round Trip Seat Request: {}", request);

        HoldRoundTripSeatResult result = holdSeatService.holdRoundTripSeat(HoldRoundTripSeatCommand.builder()
                .context(HttpUtils.toContext(request))
                .creator(request.getCreator())
                .holdBy(resolveHoldBy(request.getData()))
                .customerName(request.getInfo().getCustomerName())
                .customerPhone(request.getInfo().getCustomerPhone())
                .customerEmail(request.getInfo().getCustomerEmail())
                .outboundTrip(toRoundTripLegCommand(request.getData().getOutboundTrip()))
                .returnTrip(toRoundTripLegCommand(request.getData().getReturnTrip()))
                .build());

        HoldRoundTripSeatResponse response = HoldRoundTripSeatResponse.builder()
                .requestId(request.getRequestId())
                .requestDateTime(request.getRequestDateTime())
                .channel(request.getChannel())
                .result(ApiResult.buildSuccess())
                .data(HoldRoundTripSeatResponse.HoldRoundTripSeatResponseData.builder()
                        .outboundTrip(toRoundTripLegResponse(result.outboundTrip()))
                        .returnTrip(toRoundTripLegResponse(result.returnTrip()))
                        .build())
                .build();

        sLog.info("[HOLD-ROUND-TRIP-SEAT] Hold Round Trip Seat Response: {}", response);

        return HttpUtils.buildResponse(request, response);
    }

    private HoldRoundTripSeatCommand.HoldRoundTripSeatLegCommand toRoundTripLegCommand(HoldSeatRequest.HoldSeatRequestData data) {
        return HoldRoundTripSeatCommand.HoldRoundTripSeatLegCommand.builder()
                .tripId(data.getTripId())
                .seatNos(data.getSeatNos())
                .pickupType(data.getPickupType())
                .pickupStopId(data.getPickupStopId())
                .pickupAddress(data.getPickupAddress())
                .dropOffType(data.getDropOffType())
                .dropOffStopId(data.getDropOffStopId())
                .dropOffAddress(data.getDropOffAddress())
                .build();
    }

    private HoldRoundTripSeatResponse.HoldRoundTripSeatLegResponse toRoundTripLegResponse(HoldSeatResult result) {
        return HoldRoundTripSeatResponse.HoldRoundTripSeatLegResponse.builder()
                .booking(getHoldSeatResponseBookingInfo(result))
                .seats(toHoldSeatResponseData(result))
                .build();
    }

    private String resolveHoldBy(HoldRoundTripSeatRequest.HoldRoundTripSeatRequestData data) {
        if (data.getHoldBy() != null && !data.getHoldBy().isBlank()) {
            return data.getHoldBy();
        }
        if (data.getOutboundTrip().getHoldBy() != null && !data.getOutboundTrip().getHoldBy().isBlank()) {
            return data.getOutboundTrip().getHoldBy();
        }
        return data.getReturnTrip().getHoldBy();
    }

    private static List<HoldSeatResponse.HoldSeatResponseData> toHoldSeatResponseData(HoldSeatResult result) {
        return result.seats().stream()
                .map(item -> HoldSeatResponse.HoldSeatResponseData.builder()
                        .tripId(item.tripId())
                        .seatNo(item.seatNo())
                        .status(item.status())
                        .holdToken(item.holdToken())
                        .build())
                .collect(Collectors.toList());
    }

    private static HoldSeatResponse.HoldSeatResponseBookingInfo getHoldSeatResponseBookingInfo(HoldSeatResult item) {
        return HoldSeatResponse.HoldSeatResponseBookingInfo.builder()
                .bookingId(item.booking().bookingId())
                .bookingCode(item.booking().bookingCode())
                .holdUntil(item.booking().holdUntil())
                .seatCount(item.booking().seatCount())
                .totalAmount(item.booking().totalAmount())
                .currency(item.booking().currency())
                .build();
    }
}
