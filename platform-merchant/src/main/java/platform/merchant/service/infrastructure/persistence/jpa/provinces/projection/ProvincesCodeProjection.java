package platform.merchant.service.infrastructure.persistence.jpa.provinces.projection;

public interface ProvincesCodeProjection {

    String getOriginCode();
    String getDestinationCode();
    String getOriginName();
    String getDestinationName();
}
