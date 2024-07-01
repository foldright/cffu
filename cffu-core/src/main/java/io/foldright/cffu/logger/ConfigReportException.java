package io.foldright.cffu.logger;

import edu.umd.cs.findbugs.annotations.Nullable;
import io.foldright.cffu.logger.jdk.JdkLoggerAdapter;
import io.foldright.cffu.logger.log4j.Log4jLoggerAdapter;
import io.foldright.cffu.logger.log4j2.Log4j2LoggerAdapter;
import io.foldright.cffu.logger.slf4j.Slf4jLoggerAdapter;


public class ConfigReportException {

    private  static Logger logger;

    /**
     * cffu.uncaught.exception.report=none | oneline | short(default) | full
     * corresponding system print|error|warn|info
     * @param msg
     * @param ex
     * @param <T>
     * @return
     */
    @Nullable
    @SuppressWarnings("SameReturnValue")
    public static <T> T reportException(String msg, Throwable ex) {

        String loggerLevel = System.getProperty("cffu.uncaught.exception.report.log.level", "none");
        switch (loggerLevel){
            case  "warn":
                logger.warn(msg, ex);
                break;
            case  "info":
                logger.info(msg, ex);
                break;
            case  "error":
                logger.error(msg, ex);
                break;
            case "none":
            default:
                //no handle
                break;
        }
        return  null;
    }

    static {
        String appLogger = System.getProperty("cffu.uncaught.exception.report.log.type", "none");
        LoggerAdapter loggerAdapter;
        switch (appLogger){
            case  "log4j":
                loggerAdapter = new Log4jLoggerAdapter();
                break;
            case  "log4j2":
                loggerAdapter = new Log4j2LoggerAdapter();
                break;
            case  "slf4j":
                loggerAdapter = new Slf4jLoggerAdapter();
                break;
            case "none":
            default:
                loggerAdapter = new JdkLoggerAdapter();
                break;
        }
        logger = loggerAdapter.getLogger(ConfigReportException.class.getName());

        String loggerLevel = System.getProperty("cffu.uncaught.exception.report.log.level", "none");
        switch (loggerLevel){
            case  "warn":
                loggerAdapter.setLevel(Level.WARN);
                break;
            case  "info":
                loggerAdapter.setLevel(Level.INFO);
                break;
            case  "error":
                loggerAdapter.setLevel(Level.ERROR);
                break;
            case "none":
            default:
                loggerAdapter.setLevel(Level.OFF);
                //no handle
                break;
        }

    }

}
