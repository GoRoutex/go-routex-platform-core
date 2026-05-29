package platform.merchant.service.interfaces.internal;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.com.go.routex.identity.security.log.SystemLog;
import platform.merchant.service.application.service.InternalBookingContextService;
import platform.core.common.service.persistence.utils.ApiRequestUtils;
import platform.merchant.service.infrastructure.persistence.utils.HttpUtils;
import platform.merchant.service.interfaces.factory.ApiResultFactory;
import platform.core.common.service.api.BaseRequest;
import platform.core.common.service.api.BaseResponse;
import platform.merchant.service.interfaces.model.internal.booking.InternalBookingContextResponses;

import static platform.core.common.service.persistence.constant.ApiConstant.API_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.API_VERSION;
import static platform.core.common.service.persistence.constant.ApiConstant.BOOKING_CONTEXT_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.INTERNAL_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.MERCHANT_SERVICE;
import static platform.core.common.service.persistence.constant.ApiConstant.SEAT_BLUEPRINT_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.TRIPS_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.VEHICLE_PATH;

@RestController
@RequiredArgsConstructor
@RequestMapping(API_PATH + API_VERSION + MERCHANT_SERVICE + INTERNAL_PATH)
public class InternalBookingContextController {

    private final InternalBookingContextService internalBookingContextService;
    private final ApiResultFactory apiResultFactory;
    private final SystemLog sLog = SystemLog.getLogger(this.getClass());

    @GetMapping(TRIPS_PATH + BOOKING_CONTEXT_PATH)
    public ResponseEntity<BaseResponse<InternalBookingContextResponses.TripBookingContextData>> fetchTripBookingContext(
            HttpServletRequest servletRequest,
            @RequestParam String tripId
    ) {
        BaseRequest baseRequest = ApiRequestUtils.getBaseRequestOrDefault(servletRequest);
        InternalBookingContextResponses.TripBookingContextData data =
                internalBookingContextService.fetchTripBookingContext(tripId, HttpUtils.toContext(baseRequest));

        BaseResponse<InternalBookingContextResponses.TripBookingContextData> response =
                BaseResponse.<InternalBookingContextResponses.TripBookingContextData>builder()
                        .result(apiResultFactory.buildSuccess())
                        .data(data)
                        .build();
        return HttpUtils.buildResponse(baseRequest, response);
    }

    @GetMapping(VEHICLE_PATH + SEAT_BLUEPRINT_PATH)
    public ResponseEntity<BaseResponse<InternalBookingContextResponses.VehicleSeatBlueprintData>> fetchVehicleSeatBlueprint(
            HttpServletRequest servletRequest,
            @RequestParam String vehicleId
    ) {
        BaseRequest baseRequest = ApiRequestUtils.getBaseRequestOrDefault(servletRequest);
        InternalBookingContextResponses.VehicleSeatBlueprintData data =
                internalBookingContextService.fetchVehicleSeatBlueprint(vehicleId, HttpUtils.toContext(baseRequest));

        BaseResponse<InternalBookingContextResponses.VehicleSeatBlueprintData> response =
                BaseResponse.<InternalBookingContextResponses.VehicleSeatBlueprintData>builder()
                        .result(apiResultFactory.buildSuccess())
                        .data(data)
                        .build();
        return HttpUtils.buildResponse(baseRequest, response);
    }
}
