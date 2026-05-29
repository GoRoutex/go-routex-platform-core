package platform.core.common.service.infrastructure.redis.service;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface RedisInfraService {


    void setString(String key, String value);
    String getString(String key);


    void setObject(String key, Object value);
    <T> T getObject(String key, Class<T> targetClass) throws JsonProcessingException;
}
