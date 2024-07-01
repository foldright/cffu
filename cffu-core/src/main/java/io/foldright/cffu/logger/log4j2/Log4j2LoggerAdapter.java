package io.foldright.cffu.logger.log4j2;



import io.foldright.cffu.logger.Level;
import io.foldright.cffu.logger.Logger;
import io.foldright.cffu.logger.LoggerAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.Configurator;

import java.io.File;


public class Log4j2LoggerAdapter implements LoggerAdapter {
    public static final String NAME = "log4j2";

    private Level level;

    public Log4j2LoggerAdapter() {
        try {
            org.apache.logging.log4j.Logger logger = LogManager.getRootLogger();
            this.level = fromLog4j2Level(logger.getLevel());
        } catch (Exception t) {
            // ignore
        }
    }

    private static org.apache.logging.log4j.Level toLog4j2Level(Level level) {
        if (level == Level.ALL) {
            return org.apache.logging.log4j.Level.ALL;
        }
        if (level == Level.TRACE) {
            return org.apache.logging.log4j.Level.TRACE;
        }
        if (level == Level.DEBUG) {
            return org.apache.logging.log4j.Level.DEBUG;
        }
        if (level == Level.INFO) {
            return org.apache.logging.log4j.Level.INFO;
        }
        if (level == Level.WARN) {
            return org.apache.logging.log4j.Level.WARN;
        }
        if (level == Level.ERROR) {
            return org.apache.logging.log4j.Level.ERROR;
        }
        return org.apache.logging.log4j.Level.OFF;
    }

    private static Level fromLog4j2Level(org.apache.logging.log4j.Level level) {
        if (level == org.apache.logging.log4j.Level.ALL) {
            return Level.ALL;
        }
        if (level == org.apache.logging.log4j.Level.TRACE) {
            return Level.TRACE;
        }
        if (level == org.apache.logging.log4j.Level.DEBUG) {
            return Level.DEBUG;
        }
        if (level == org.apache.logging.log4j.Level.INFO) {
            return Level.INFO;
        }
        if (level == org.apache.logging.log4j.Level.WARN) {
            return Level.WARN;
        }
        if (level == org.apache.logging.log4j.Level.ERROR) {
            return Level.ERROR;
        }
        return Level.OFF;
    }

    @Override
    public Logger getLogger(Class<?> key) {
        return new Log4j2Logger(LogManager.getLogger(key));
    }

    @Override
    public Logger getLogger(String key) {
        return new Log4j2Logger(LogManager.getLogger(key));
    }

    @Override
    public Level getLevel() {
        return level;
    }

    @Override
    public void setLevel(Level level) {
        this.level = level;
        Configurator.setLevel(LogManager.getRootLogger(), toLog4j2Level(level));
    }

    @Override
    public File getFile() {
        return null;
    }

    @Override
    public void setFile(File file) {
        // ignore
    }

    @Override
    public boolean isConfigured() {
        return true;
    }
}
