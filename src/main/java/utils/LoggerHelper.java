package utils;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class LoggerHelper {

    private static boolean isRootLoggerConfigured = false;

    public static Logger getLogger(Class<?> cls) {
        if (!isRootLoggerConfigured) {
            PropertyConfigurator.configure("src/test/resources/logs/log4j.properties");
            isRootLoggerConfigured = true;
        }
        return Logger.getLogger(cls);
    }
}
