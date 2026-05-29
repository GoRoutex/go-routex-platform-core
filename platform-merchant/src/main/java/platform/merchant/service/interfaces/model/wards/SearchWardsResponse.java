package platform.merchant.service.interfaces.model.wards;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.core.common.service.api.BaseResponse;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class SearchWardsResponse extends BaseResponse<List<SearchWardsResponse.SearchWardsResponseData>> {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class SearchWardsResponseData {
        private String id;
        private String name;
        private String provinceId;
    }
}
