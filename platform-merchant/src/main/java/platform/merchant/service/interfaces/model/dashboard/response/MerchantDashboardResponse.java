package platform.merchant.service.interfaces.model.dashboard.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.core.common.service.api.BaseResponse;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class MerchantDashboardResponse extends BaseResponse<MerchantDashboardResponse.DashboardData> implements Serializable {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class DashboardData {
        private Statistics stats;
        private List<RevenueChartData> revenueChart;
        private List<PopularRouteData> popularRoutes;
        private List<RecentTripData> recentTrips;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class Statistics {
        private Integer ticketsSold;
        private BigDecimal totalRevenue;
        private BigDecimal merchantShare;
        private Double ticketGrowthRate; // so với kỳ trước
        private Double revenueGrowthRate;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class RevenueChartData {
        private String label; // "Thứ 2", "Ngày 01/01", etc.
        private BigDecimal revenue;
        private OffsetDateTime date;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class PopularRouteData {
        private String routeId;
        private String routeName;
        private Integer ticketCount;
        private Double occupancyRate;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class RecentTripData {
        private String tripId;
        private String routeName;
        private String vehiclePlate;
        private OffsetDateTime departureTime;
        private String status;
        private Integer bookedSeats;
    }
}
