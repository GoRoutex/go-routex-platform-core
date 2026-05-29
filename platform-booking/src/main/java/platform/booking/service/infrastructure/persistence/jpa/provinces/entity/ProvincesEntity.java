package platform.booking.service.infrastructure.persistence.jpa.provinces.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import platform.core.common.service.persistence.AbstractAuditingEntity;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "Booking_ProvincesEntity")
@Table(name = "PROVINCES")
public class ProvincesEntity extends AbstractAuditingEntity {
    @Id
    private String id;

    @Column(name = "NAME", nullable = false, length = 100, unique = true)
    private String name;

    @Column(name = "CODE", nullable = false, length = 10, unique = true)
    private String code;
}
