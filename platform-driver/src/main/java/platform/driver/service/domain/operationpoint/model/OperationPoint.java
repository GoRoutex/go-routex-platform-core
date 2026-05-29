package platform.driver.service.domain.operationpoint.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.core.common.service.persistence.AbstractAuditingEntity;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class OperationPoint extends AbstractAuditingEntity {
    private String id;
    private String code;
    private String name;
    private String merchantId;
    private OperationPointType type;
    private String address;
    private String city;
    private Double latitude;
    private Double longitude;
    private OperationPointStatus status;
}
