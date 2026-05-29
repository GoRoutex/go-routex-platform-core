package platform.management.service.infrastructure.integration.merchantplatform.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MerchantPlatformFetchMerchantsRequest {
    private List<String> merchantIds;
}
