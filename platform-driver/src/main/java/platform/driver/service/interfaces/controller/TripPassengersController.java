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
import platform.driver.service.application.services.TripPassengerService;
import platform.driver.service.interfaces.models.passengers.PassengerCheckinRequest;
import platform.driver.service.interfaces.models.passengers.PassengerCheckinResponse;

import static platform.core.common.service.persistence.constant.ApiConstant.API_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.API_VERSION;
import static platform.core.common.service.persistence.constant.ApiConstant.CHECKIN_PATH;
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
        return null;
    }
}
