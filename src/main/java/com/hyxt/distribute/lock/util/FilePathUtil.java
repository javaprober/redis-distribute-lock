package com.hyxt.distribute.lock.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;

/**
 * Created by andy on 2016/8/9.
 */
public class FilePathUtil {

    private static Logger logger = LoggerFactory.getLogger(FilePathUtil.class);

    public static File getFile(String filePath) {

        if(filePath.startsWith("/")) {
            try {
                File file = new File(filePath);
                if (file != null && file.exists()) {
                    return file;
                }
            } catch (Throwable e) {
                logger.warn("Failed to load " + filePath + " file from "
                        + filePath + "(ingore this file): " + e.getMessage(), e);
            }
        }

        try {
            URL resource = ClassHelpUtil.getClassLoader()
                    .getResource(filePath);
            if(resource != null) {
                return new File(resource.getFile());
            }
        } catch (Throwable t) {
            logger.warn(
                    "Fail to load " + filePath + " file: " + t.getMessage(), t);
        }
        
        return null;
    }
}
