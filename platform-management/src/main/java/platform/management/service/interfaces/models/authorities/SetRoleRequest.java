package platform.management.service.interfaces.models.authorities;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import platform.core.common.service.api.BaseRequest;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class SetRoleRequest extends BaseRequest {

    @Valid
    @NotNull
    private SetRoleRequestData data;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class SetRoleRequestData {

        @NotNull
        @NotBlank
        private String userId;

        @NotNull
        @NotBlank
        private String roleId;
    }
}
