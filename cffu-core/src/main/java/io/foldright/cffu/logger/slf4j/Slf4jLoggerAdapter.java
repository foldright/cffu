package io.foldright.cffu.logger.slf4j;


import io.foldright.cffu.logger.Level;
import io.foldright.cffu.logger.Logger;
import io.foldright.cffu.logger.LoggerAdapter;
import org.slf4j.LoggerFactory;

import java.io.File;

public class Slf4jLoggerAdapter implements LoggerAdapter {
    public static final String NAME = "slf4j";

    private Level level;
    private File file;

    private static final org.slf4j.Logger ROOT_LOGGER = LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);

    public Slf4jLoggerAdapter() {
        this.level = Slf4jLogger.getLevel(ROOT_LOGGER);
    }

    @Override
    public Logger getLogger(String key) {
        return new Slf4jLogger(LoggerFactory.getLogger(key));
    }

    @Override
    public Logger getLogger(Class<?> key) {
        return new Slf4jLogger(LoggerFactory.getLogger(key));
    }

    @Override
    public Level getLevel() {
        return level;
    }

    @Override
    public void setLevel(Level level) {
        System.err.printf(
                "The level of slf4j logger current can not be set, using the default level: %s \n",
                Slf4jLogger.getLevel(ROOT_LOGGER));
        this.level = level;
    }

    @Override
    public File getFile() {
        return file;
    }

    @Override
    public void setFile(File file) {
        this.file = file;
    }

    @Override
    public boolean isConfigured() {
        try {
            Class.forName("org.slf4j.impl.StaticLoggerBinder");
            return true;
        } catch (ClassNotFoundException ignore) {
            // ignore
        }
        return false;
    }
}
