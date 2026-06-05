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
import platform.core.common.service.api.BaseRequest;
import platform.core.common.service.common.RequestContext;
import platform.driver.service.application.dto.manifest.GetTripManifestQuery;
import platform.driver.service.application.dto.manifest.TripManifestView;
import platform.driver.service.application.services.TripManifestService;
import platform.driver.service.interfaces.mapper.TripManifestMapper;
import platform.driver.service.interfaces.models.manifest.GetTripManifestRequest;
import platform.driver.service.interfaces.models.manifest.GetTripManifestResponse;

import static platform.core.common.service.persistence.constant.ApiConstant.API_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.API_VERSION;
import static platform.core.common.service.persistence.constant.ApiConstant.DRIVER_PREFIX;
import static platform.core.common.service.persistence.constant.ApiConstant.TRIP_MANIFEST;

@RestController
@RequestMapping(API_PATH + API_VERSION + DRIVER_PREFIX)
@RequiredArgsConstructor
//@PreAuthorize("hasRole('ADMIN') and hasRole('DRIVER')")
public class TripManifestController {

    private final TripManifestService tripManifestService;
    private final TripManifestMapper tripManifestMapper;

    @InitBinder
    public void initBinder(WebDataBinder webDataBinder, WebRequest webRequest) {
        webDataBinder.setDisallowedFields("requestId", "requestDateTime", "channel", "data");
    }

    @PostMapping(TRIP_MANIFEST)
    public ResponseEntity<GetTripManifestResponse> getTripManifest(@Valid @RequestBody GetTripManifestRequest request) {
        String tripId = request.getData().getTripId() != null ? request.getData().getTripId() : request.getData().getRouteId();
        TripManifestView view = tripManifestService.getTripManifest(GetTripManifestQuery
                .builder()
                .context(toContext(request))
                .routeId(tripId)
                .build());

        GetTripManifestResponse response = tripManifestMapper.toGetTripManifestResponse(view);

        return ResponseEntity.ok(response);
    }

    private RequestContext toContext(BaseRequest request) {
        return RequestContext.builder()
                .requestId(request.getRequestId())
                .requestDateTime(request.getRequestDateTime())
                .channel(request.getChannel())
                .build();
    }
}
