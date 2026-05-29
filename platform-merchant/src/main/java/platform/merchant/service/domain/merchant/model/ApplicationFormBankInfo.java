package platform.merchant.service.domain.merchant.model;


import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Embeddable
public class ApplicationFormBankInfo {
    private String bankAccountName;
    private String bankAccountNumber;
    private String bankBranch;
    private String bankName;
}
