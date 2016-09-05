package com.hyxt.distribute.lock.util;

import com.hyxt.distribute.lock.config.ModeConfig;
import org.redisson.Config;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by andy on 2016/8/9.
 */
public class FilePathUtil {

//    private static Logger logger = LoggerFactory.getLogger(FilePathUtil.class);

    public static InputStream getFile(String filePath) {

        if(filePath.startsWith("/")) {
            try {
                InputStream resourceAsStream = FilePathUtil.class.getResourceAsStream(filePath);
                if(resourceAsStream != null) {
                    return resourceAsStream;
                }
            } catch (Throwable e) {
                /*logger.warn("Failed to load " + filePath + " file from "
                        + filePath + "(ingore this file): " + e.getMessage(), e);*/
            }
        }

        try {
            InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(filePath);
            if (in == null) {
                in = FilePathUtil.class.getResourceAsStream(filePath);
            }
            return in;
        } catch (Throwable t) {
            /*logger.warn(
                    "Fail to load " + filePath + " file: " + t.getMessage(), t);*/
        }
        
        return null;
    }

    public static void main(String[] args) throws IOException {
        InputStream inputStream = getFile("/" + ModeConfig.PRE_CONFIG + ModeConfig.SENTINEL_MODE);
        Config config = new Config(Config.fromYAML(inputStream));
        System.out.println(config);
    }
}
