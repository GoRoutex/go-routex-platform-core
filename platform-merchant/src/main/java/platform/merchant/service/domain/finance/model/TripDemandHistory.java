package platform.merchant.service.domain.finance.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.core.common.service.persistence.AbstractAuditingEntity;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class TripDemandHistory extends AbstractAuditingEntity {
    private String id; // tripId
    private String merchantId;
    private String routeId;
    private LocalDate departureDate;
    private Integer departureHour;
    private Integer dayOfWeek;
    private Integer totalSeats;
    private Integer bookedSeats;
    private Double occupancyRate;
    private Boolean isHoliday;
}
