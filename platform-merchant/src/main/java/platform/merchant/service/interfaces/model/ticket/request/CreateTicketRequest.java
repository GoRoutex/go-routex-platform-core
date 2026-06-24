package platform.merchant.service.interfaces.model.ticket.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.core.common.service.api.BaseRequest;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class CreateTicketRequest extends BaseRequest {
    
    @NotEmpty
    private List<CreateTicketData> data;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class CreateTicketData {
        @NotBlank
        private String bookingId;
        @NotBlank
        private String bookingSeatId;
        @NotBlank
        private String merchantId;
        @NotBlank
        private String tripId;
        @NotBlank
        private String vehicleId;
        @NotBlank
        private String seatNumber;
        private String customerId;
        @NotBlank
        private String customerName;
        @NotBlank
        private String customerPhone;
        private String customerEmail;
        @NotNull
        private BigDecimal price;
        private OffsetDateTime issuedAt;
        private String promotionCode;
        private String creator;
        private String pickupType;
        private String pickupStopId;
        private String pickupAddress;
        private String dropoffType;
        private String dropoffStopId;
        private String dropoffAddress;
    }
}
