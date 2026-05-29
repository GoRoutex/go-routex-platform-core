package platform.core.common.service.infrastructure.redisson;

import java.util.List;

public interface RedisDistributedService {

    RedisDistributedLocker getMultiLock(List<String> lockKeys);
    RedisDistributedLocker getDistributedLock(String lockKey);
}
