package platform.merchant.service.interfaces.model.department;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.core.common.service.api.BaseResponse;
import platform.merchant.service.domain.department.DepartmentStatus;
import platform.merchant.service.domain.department.DepartmentType;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class FetchDepartmentResponse extends BaseResponse<FetchDepartmentResponse.FetchDepartmentResponsePage> {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class FetchDepartmentResponsePage {
        private List<FetchDepartmentResponseData> items;
        private Pagination pagination;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class FetchDepartmentResponseData {
        private String id;
        private String name;
        private DepartmentType type;
        private String address;
        private String wardId;
        private String wardName;
        private String provinceId;
        private String provinceName;
        private String openingTime;
        private String closingTime;
        private String onlineOpeningTime;
        private String onlineClosingTime;
        private Double latitude;
        private Double longitude;
        private DepartmentStatus status;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class Pagination {
        private int pageNumber;
        private int pageSize;
        private long totalElements;
        private int totalPages;
    }
}

