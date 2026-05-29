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

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class SetPermissionRequest extends BaseRequest {

    @Valid
    @NotNull
    private SetPermissionRequestData data;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class SetPermissionRequestData {
        @NotBlank
        @NotNull
        private String roleId;

        @NotNull
        private Set<String> authoritiesCode;

    }
}
