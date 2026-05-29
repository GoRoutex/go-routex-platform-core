package platform.merchant.service.infrastructure.persistence.adapter.route;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import platform.merchant.service.domain.route.model.ProvincesInformationPair;
import platform.merchant.service.domain.route.port.RouteProvincesLookupPort;
import platform.merchant.service.infrastructure.persistence.jpa.provinces.projection.ProvincesCodeProjection;
import platform.merchant.service.infrastructure.persistence.jpa.provinces.repository.ProvincesEntityRepository;

@Component
@RequiredArgsConstructor
public class RouteProvincesLookupAdapter implements RouteProvincesLookupPort {

    private final ProvincesEntityRepository provincesEntityRepository;

    @Override
    public ProvincesInformationPair getCodes(String origin, String destination) {
        ProvincesCodeProjection view = provincesEntityRepository.selectProvincesCode(origin, destination);
        return new ProvincesInformationPair(view.getOriginCode(), view.getDestinationCode(), view.getOriginName(), view.getDestinationName());
    }
}
