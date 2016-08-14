package com.hyxt.distribute.lock;

import com.hyxt.distribute.lock.util.PropertyUtils;
import org.junit.Test;

/**
 * Created by andy on 2016/8/11.
 */
public class LockUtilTest {
    @Test
    public void testProperiesRead() {
        String propertyString = PropertyUtils.getPropertyString("base_config", "redis.lock.default.waittime");
        System.out.println(propertyString);
    }
}
