package platform.merchant.service.interfaces.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import platform.merchant.service.application.command.holiday.CreateHolidayCommand;
import platform.merchant.service.application.command.holiday.DeleteHolidayCommand;
import platform.merchant.service.application.command.holiday.DeleteHolidayResult;
import platform.merchant.service.application.command.holiday.HolidayResult;
import platform.merchant.service.application.command.holiday.UpdateHolidayCommand;
import platform.merchant.service.application.service.HolidayService;
import platform.core.common.service.persistence.utils.ApiRequestUtils;
import platform.merchant.service.infrastructure.persistence.utils.HttpUtils;
import platform.merchant.service.interfaces.factory.ApiResultFactory;
import platform.merchant.service.interfaces.model.holiday.request.CreateHolidayRequest;
import platform.merchant.service.interfaces.model.holiday.request.DeleteHolidayRequest;
import platform.merchant.service.interfaces.model.holiday.request.UpdateHolidayRequest;
import platform.merchant.service.interfaces.model.holiday.response.DeleteHolidayResponse;
import platform.merchant.service.interfaces.model.holiday.response.HolidayResponse;

import java.util.List;

import static platform.core.common.service.persistence.constant.ApiConstant.API_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.API_VERSION;
import static platform.core.common.service.persistence.constant.ApiConstant.DELETE_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.HOLIDAY_PATH;
import static platform.core.common.service.persistence.constant.ApiConstant.MERCHANT_SERVICE;
import static platform.core.common.service.persistence.constant.ApiConstant.UPDATE_PATH;

@RestController
@RequestMapping(API_PATH + API_VERSION + MERCHANT_SERVICE + HOLIDAY_PATH)
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN') or hasRole('MERCHANT_OWNER')")
public class HolidayController {
    private final HolidayService holidayService;
    private final ApiResultFactory apiResultFactory;

    @InitBinder
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.setDisallowedFields("requestId", "requestDateTime", "channel", "data");
    }

    @PostMapping
    public ResponseEntity<HolidayResponse> createHoliday(
            @Valid @RequestBody CreateHolidayRequest request) {
        CreateHolidayCommand command = CreateHolidayCommand.builder()
                .holidayDate(request.getData().getHolidayDate())
                .name(request.getData().getName())
                .isPeakDay(request.getData().getIsPeakDay())
                .surchargeRate(request.getData().getSurchargeRate())
                .description(request.getData().getDescription())
                .context(ApiRequestUtils.getRequestContext(request))
                .build();

        HolidayResult result = holidayService.createHoliday(command);

        HolidayResponse response = HolidayResponse.builder()
                .result(apiResultFactory.buildSuccess())
                .data(mapToData(result))
                .build();

        return HttpUtils.buildResponse(request, response);
    }

    @PostMapping(UPDATE_PATH + "/{id}")
    public ResponseEntity<HolidayResponse> updateHoliday(
            @PathVariable String id,
            @Valid @RequestBody UpdateHolidayRequest request) {

        UpdateHolidayCommand command = UpdateHolidayCommand.builder()
                .id(id)
                .holidayDate(request.getData().getHolidayDate())
                .name(request.getData().getName())
                .isPeakDay(request.getData().getIsPeakDay())
                .surchargeRate(request.getData().getSurchargeRate())
                .description(request.getData().getDescription())
                .context(ApiRequestUtils.getRequestContext(request))
                .build();

        HolidayResult result = holidayService.updateHoliday(command);

        HolidayResponse response = HolidayResponse.builder()
                .result(apiResultFactory.buildSuccess())
                .data(mapToData(result))
                .build();

        return HttpUtils.buildResponse(request, response);
    }

    @PostMapping(DELETE_PATH)
    public ResponseEntity<DeleteHolidayResponse> deleteHoliday(
            @Valid @RequestBody DeleteHolidayRequest request) {

        DeleteHolidayCommand command = DeleteHolidayCommand.builder()
                .id(request.getData().getId())
                .context(ApiRequestUtils.getRequestContext(request))
                .build();

        DeleteHolidayResult result = holidayService.deleteHoliday(command);

        DeleteHolidayResponse response = DeleteHolidayResponse.builder()
                .result(apiResultFactory.buildSuccess())
                .data(DeleteHolidayResponse.DeleteHolidayData.builder()
                        .id(result.id())
                        .status(result.status())
                        .build())
                .build();

        return HttpUtils.buildResponse(request, response);
    }


    @GetMapping
    public ResponseEntity<List<HolidayResponse.HolidayData>> getAllHolidays() {
        return ResponseEntity.ok(holidayService.getAllHolidays().stream()
                .map(this::mapToData)
                .toList());
    }

    private HolidayResponse.HolidayData mapToData(HolidayResult result) {
        return HolidayResponse.HolidayData.builder()
                .id(result.id())
                .holidayDate(result.holidayDate())
                .name(result.name())
                .isPeakDay(result.isPeakDay())
                .surchargeRate(result.surchargeRate())
                .description(result.description())
                .build();
    }
}


