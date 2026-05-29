package platform.merchant.service.domain.merchant.model;


import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class ApplicationFormContact {
    private String contactEmail;
    private String contactName;
    private String contactPhone;
}
