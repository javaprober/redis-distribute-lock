package com.hyxt.distribute.lock.client;

import com.hyxt.distribute.lock.exception.RedisLockException;
import com.hyxt.distribute.lock.util.InternMap;
import com.hyxt.distribute.lock.util.PropertyUtils;

/**
 * base_config 读取
 * Created by andy on 2016/8/22.
 */
public interface PropertiesHandle {
    String PROPERTIES_NAME = "base_config";
    class Factory {
        private final static InternMap<String, String> intern
                = new InternMap<String, String>(
                new InternMap.ValueConstructor<String, String>() {
                    public String create(String key) throws RedisLockException {
                        String value = PropertyUtils.getPropertyString(PropertiesHandle.PROPERTIES_NAME, key);
                        return value;
                    }
                });
        public static String propertiesValue(String key) throws RedisLockException {
            return intern.interned(key);
        }
    }
}
