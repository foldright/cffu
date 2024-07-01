package io.foldright.cffu.logger.slf4j;


import io.foldright.cffu.logger.Level;
import io.foldright.cffu.logger.Logger;

public class Slf4jLogger implements Logger {

    private final org.slf4j.Logger logger;



    public Slf4jLogger(org.slf4j.Logger logger) {
//         if (logger instanceof LocationAwareLogger) {
//             locationAwareLogger = (LocationAwareLogger) logger;
//         } else {
//             locationAwareLogger = null;
//         }
        this.logger = logger;
    }

    @Override
    public void trace(String msg) {

        logger.trace(msg);
    }

    @Override
    public void trace(Throwable e) {

        logger.trace(e.getMessage(), e);
    }

    @Override
    public void trace(String msg, Throwable e) {

        logger.trace(msg, e);
    }

    @Override
    public void debug(String msg) {

        logger.debug(msg);
    }

    @Override
    public void debug(Throwable e) {

        logger.debug(e.getMessage(), e);
    }

    @Override
    public void debug(String msg, Throwable e) {

        logger.debug(msg, e);
    }

    @Override
    public void info(String msg) {

        logger.info(msg);
    }

    @Override
    public void info(Throwable e) {

        logger.info(e.getMessage(), e);
    }

    @Override
    public void info(String msg, Throwable e) {

        logger.info(msg, e);
    }

    @Override
    public void warn(String msg) {

        logger.warn(msg);
    }

    @Override
    public void warn(Throwable e) {

        logger.warn(e.getMessage(), e);
    }

    @Override
    public void warn(String msg, Throwable e) {

        logger.warn(msg, e);
    }

    @Override
    public void error(String msg) {

        logger.error(msg);
    }

    @Override
    public void error(Throwable e) {

        logger.error(e.getMessage(), e);
    }

    @Override
    public void error(String msg, Throwable e) {

        logger.error(msg, e);
    }

    @Override
    public boolean isTraceEnabled() {
        return logger.isTraceEnabled();
    }

    @Override
    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    @Override
    public boolean isInfoEnabled() {
        return logger.isInfoEnabled();
    }

    @Override
    public boolean isWarnEnabled() {
        return logger.isWarnEnabled();
    }

    @Override
    public boolean isErrorEnabled() {
        return logger.isErrorEnabled();
    }

    public static Level getLevel(org.slf4j.Logger logger) {
        if (logger.isTraceEnabled()) {
            return Level.TRACE;
        }
        if (logger.isDebugEnabled()) {
            return Level.DEBUG;
        }
        if (logger.isInfoEnabled()) {
            return Level.INFO;
        }
        if (logger.isWarnEnabled()) {
            return Level.WARN;
        }
        if (logger.isErrorEnabled()) {
            return Level.ERROR;
        }
        return Level.OFF;
    }

    public Level getLevel() {
        return getLevel(logger);
    }
}
