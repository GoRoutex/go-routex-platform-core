package platform.merchant.service.interfaces.model.route;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.merchant.service.domain.route.RouteStatus;
import platform.core.common.service.api.BaseResponse;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class FetchRouteResponse extends BaseResponse<FetchRouteResponse.FetchRouteResponsePage> {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class FetchRouteResponsePage {
        private List<FetchRouteResponseData> items;
        private Pagination pagination;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class FetchRouteResponseData {
        private String id;
        private String creator;
        private String originCode;
        private String originName;
        private String originDepartmentId;
        private String originDepartmentName;
        private String destinationCode;
        private String destinationName;
        private String destinationDepartmentId;
        private String destinationDepartmentName;
        private Long duration;
        private RouteStatus status;
        private List<SearchRouteResponse.SearchRoutePoints> routePoints;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @SuperBuilder
    public static class Pagination {
        private int pageNumber;
        private int pageSize;
        private long totalElements;
        private int totalPages;
    }
}
