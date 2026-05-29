package platform.management.service.interfaces.models.trip;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.core.common.service.api.BaseResponse;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class FetchTripDetailResponse extends BaseResponse<FetchTripResponse.FetchTripResponseData> {
}
