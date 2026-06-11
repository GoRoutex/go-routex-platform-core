package platform.merchant.service.domain.trip.readmodel;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.core.common.service.domain.trip.TripStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class TripFetchView {
    private String id;
    private String tripCode;
    private String routeId;
    private String creator;
    private String merchantId;
    private String merchantName;
    private String originCode;
    private String originName;
    private String destinationCode;
    private String destinationName;
    private String originProvinceId;
    private String destinationProvinceId;
    private String originDepartmentName;
    private String destinationDepartmentName;
    private String originDepartmentId;
    private String destinationDepartmentId;
    private OffsetDateTime departureTime;
    private String rawDepartureTime;
    private String rawDepartureDate;
    private Long durationMinutes;
    private TripStatus status;
    private BigDecimal ticketPrice;
    private int availableSeat;
}