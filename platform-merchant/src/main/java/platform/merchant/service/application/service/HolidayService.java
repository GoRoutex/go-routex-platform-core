package platform.merchant.service.application.service;


import platform.merchant.service.application.command.holiday.CreateHolidayCommand;
import platform.merchant.service.application.command.holiday.DeleteHolidayCommand;
import platform.merchant.service.application.command.holiday.DeleteHolidayResult;
import platform.merchant.service.application.command.holiday.HolidayResult;
import platform.merchant.service.application.command.holiday.UpdateHolidayCommand;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface HolidayService {
    boolean isHolidayOrPeakDay(LocalDate date);
    String getHolidayName(LocalDate date);
    BigDecimal getSurchargeRate(LocalDate date);
    
    HolidayResult createHoliday(CreateHolidayCommand command);
    HolidayResult updateHoliday(UpdateHolidayCommand command);
    DeleteHolidayResult deleteHoliday(DeleteHolidayCommand command);
    List<HolidayResult> getAllHolidays();
}

