package platform.merchant.service.infrastructure.persistence.adapter.finance;

import org.springframework.stereotype.Component;
import platform.merchant.service.domain.finance.model.MerchantDailyStats;
import platform.merchant.service.domain.finance.model.TripDemandHistory;
import platform.merchant.service.infrastructure.persistence.jpa.finance.entity.MerchantDailyStatsEntity;
import platform.merchant.service.infrastructure.persistence.jpa.finance.entity.TripDemandHistoryEntity;

@Component
public class FinancePersistenceMapper {

    public MerchantDailyStats toDomain(MerchantDailyStatsEntity entity) {
        if (entity == null) return null;
        return MerchantDailyStats.builder()
                .id(entity.getId())
                .merchantId(entity.getMerchantId())
                .statsDate(entity.getStatsDate())
                .totalTickets(entity.getTotalTickets())
                .totalRevenue(entity.getTotalRevenue())
                .totalDiscount(entity.getTotalDiscount())
                .merchantShare(entity.getMerchantShare())
                .systemCommission(entity.getSystemCommission())
                .occupancyRate(entity.getOccupancyRate())
                .createdAt(entity.getCreatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedAt(entity.getUpdatedAt())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }

    public MerchantDailyStatsEntity toEntity(MerchantDailyStats domain) {
        if (domain == null) return null;
        return MerchantDailyStatsEntity.builder()
                .id(domain.getId())
                .merchantId(domain.getMerchantId())
                .statsDate(domain.getStatsDate())
                .totalTickets(domain.getTotalTickets())
                .totalRevenue(domain.getTotalRevenue())
                .totalDiscount(domain.getTotalDiscount())
                .merchantShare(domain.getMerchantShare())
                .systemCommission(domain.getSystemCommission())
                .occupancyRate(domain.getOccupancyRate())
                .createdAt(domain.getCreatedAt())
                .createdBy(domain.getCreatedBy())
                .updatedAt(domain.getUpdatedAt())
                .updatedBy(domain.getUpdatedBy())
                .build();
    }

    public TripDemandHistory toDomain(TripDemandHistoryEntity entity) {
        if (entity == null) return null;
        return TripDemandHistory.builder()
                .id(entity.getId())
                .merchantId(entity.getMerchantId())
                .routeId(entity.getRouteId())
                .departureDate(entity.getDepartureDate())
                .departureHour(entity.getDepartureHour())
                .dayOfWeek(entity.getDayOfWeek())
                .totalSeats(entity.getTotalSeats())
                .bookedSeats(entity.getBookedSeats())
                .occupancyRate(entity.getOccupancyRate())
                .isHoliday(entity.getIsHoliday())
                .createdAt(entity.getCreatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedAt(entity.getUpdatedAt())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }

    public TripDemandHistoryEntity toEntity(TripDemandHistory domain) {
        if (domain == null) return null;
        return TripDemandHistoryEntity.builder()
                .id(domain.getId())
                .merchantId(domain.getMerchantId())
                .routeId(domain.getRouteId())
                .departureDate(domain.getDepartureDate())
                .departureHour(domain.getDepartureHour())
                .dayOfWeek(domain.getDayOfWeek())
                .totalSeats(domain.getTotalSeats())
                .bookedSeats(domain.getBookedSeats())
                .occupancyRate(domain.getOccupancyRate())
                .isHoliday(domain.getIsHoliday())
                .createdAt(domain.getCreatedAt())
                .createdBy(domain.getCreatedBy())
                .updatedAt(domain.getUpdatedAt())
                .updatedBy(domain.getUpdatedBy())
                .build();
    }
}
