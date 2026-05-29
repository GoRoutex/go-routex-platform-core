package platform.merchant.service.interfaces.model.provinces;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.core.common.service.api.BaseResponse;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class CreateProvinceResponse extends BaseResponse<CreateProvinceResponse.CreateProvinceResponseData> {

    @Getter
    @Setter
    @NoArgsConstructor
    @SuperBuilder
    public static class CreateProvinceResponseData {
        private Integer id;
        private String name;
        private String code;
    }
}

