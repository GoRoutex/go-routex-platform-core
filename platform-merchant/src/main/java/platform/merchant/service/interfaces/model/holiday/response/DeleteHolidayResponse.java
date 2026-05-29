package platform.merchant.service.interfaces.model.holiday.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.core.common.service.api.BaseResponse;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class DeleteHolidayResponse extends BaseResponse<DeleteHolidayResponse.DeleteHolidayData> {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class DeleteHolidayData {
        private String id;
        private String status;
    }
}
