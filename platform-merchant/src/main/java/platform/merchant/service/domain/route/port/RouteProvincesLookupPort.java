package platform.merchant.service.domain.route.port;


import platform.merchant.service.domain.route.model.ProvincesInformationPair;

public interface RouteProvincesLookupPort {
    ProvincesInformationPair getCodes(String origin, String destination);
}
