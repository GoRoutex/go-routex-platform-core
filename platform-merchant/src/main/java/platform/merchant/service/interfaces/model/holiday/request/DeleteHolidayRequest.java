package platform.merchant.service.interfaces.model.holiday.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import platform.core.common.service.api.BaseRequest;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DeleteHolidayRequest extends BaseRequest {
    private DeleteHolidayData data;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DeleteHolidayData {
        private String id;
    }
}
