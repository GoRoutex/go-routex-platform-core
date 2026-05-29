package platform.merchant.service.application.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import platform.merchant.service.application.command.holiday.CreateHolidayCommand;
import platform.merchant.service.application.command.holiday.DeleteHolidayCommand;
import platform.merchant.service.application.command.holiday.DeleteHolidayResult;
import platform.merchant.service.application.command.holiday.HolidayResult;
import platform.merchant.service.application.command.holiday.UpdateHolidayCommand;
import platform.merchant.service.application.service.HolidayService;
import platform.merchant.service.infrastructure.persistence.jpa.finance.entity.HolidayEntity;
import platform.merchant.service.infrastructure.persistence.jpa.finance.repository.HolidayRepository;
import platform.merchant.service.interfaces.factory.ApiResultFactory;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;



@Service
@RequiredArgsConstructor
public class HolidayServiceImpl implements HolidayService {

    private final HolidayRepository holidayRepository;
    private final ApiResultFactory apiResultFactory;

    @Override
    public boolean isHolidayOrPeakDay(LocalDate date) {
        // 1. Check in Database (Admin defined holidays/peak days)
        Optional<HolidayEntity> holiday = holidayRepository.findByHolidayDate(date);
        if (holiday.isPresent()) {
            return true;
        }

        // 2. Weekend logic (Friday evening to Sunday are usually peak in transport)
        DayOfWeek dow = date.getDayOfWeek();
        return dow == DayOfWeek.FRIDAY || dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY;
    }

    @Override
    public String getHolidayName(LocalDate date) {
        return holidayRepository.findByHolidayDate(date)
                .map(HolidayEntity::getName)
                .orElse(isWeekend(date) ? "Cuối tuần" : null);
    }

    @Override
    public BigDecimal getSurchargeRate(LocalDate date) {
        return holidayRepository.findByHolidayDate(date)
                .map(HolidayEntity::getSurchargeRate)
                .orElse(BigDecimal.ZERO);
    }

    @Override
    @Transactional
    public HolidayResult createHoliday(CreateHolidayCommand command) {
        HolidayEntity entity = HolidayEntity.builder()
                .id(UUID.randomUUID().toString())
                .holidayDate(command.holidayDate())
                .name(command.name())
                .isPeakDay(command.isPeakDay())
                .surchargeRate(command.surchargeRate())
                .description(command.description())
                .build();
        
        HolidayEntity saved = holidayRepository.save(entity);
        return toResult(saved);
    }

    @Override
    @Transactional
    public HolidayResult updateHoliday(UpdateHolidayCommand command) {
        HolidayEntity entity = holidayRepository.findById(command.id())
                .orElseThrow(() -> new RuntimeException("Holiday not found"));

        if (command.holidayDate() != null) entity.setHolidayDate(command.holidayDate());
        if (command.name() != null) entity.setName(command.name());
        if (command.isPeakDay() != null) entity.setIsPeakDay(command.isPeakDay());
        if (command.surchargeRate() != null) entity.setSurchargeRate(command.surchargeRate());
        if (command.description() != null) entity.setDescription(command.description());

        HolidayEntity saved = holidayRepository.save(entity);
        return toResult(saved);
    }

    @Override
    @Transactional
    public DeleteHolidayResult deleteHoliday(DeleteHolidayCommand command) {
        holidayRepository.deleteById(command.id());
        return DeleteHolidayResult.builder()
                .id(command.id())
                .status("DELETED")
                .build();
    }


    @Override
    public List<HolidayResult> getAllHolidays() {
        return holidayRepository.findAll().stream()
                .map(this::toResult)
                .toList();
    }

    private HolidayResult toResult(HolidayEntity entity) {
        return HolidayResult.builder()
                .id(entity.getId())
                .holidayDate(entity.getHolidayDate())
                .name(entity.getName())
                .isPeakDay(entity.getIsPeakDay())
                .surchargeRate(entity.getSurchargeRate())
                .description(entity.getDescription())
                .build();
    }

    private boolean isWeekend(LocalDate date) {
        DayOfWeek dow = date.getDayOfWeek();
        return dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY;
    }
}
