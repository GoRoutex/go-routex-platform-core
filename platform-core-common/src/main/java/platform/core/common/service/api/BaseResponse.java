package platform.core.common.service.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class BaseResponse<T> implements Serializable {
    private String requestId;
    private String requestDateTime;
    private String channel;
    private ApiResult result;
    private T data;
}
