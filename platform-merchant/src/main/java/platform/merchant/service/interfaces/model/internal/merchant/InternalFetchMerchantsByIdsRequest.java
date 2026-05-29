package platform.merchant.service.interfaces.model.internal.merchant;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class InternalFetchMerchantsByIdsRequest {
    private List<String> merchantIds;
}
