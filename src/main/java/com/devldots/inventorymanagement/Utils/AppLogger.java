package com.devldots.inventorymanagement.Utils;

import com.devldots.inventorymanagement.Configs.AppConfig;
import org.w3c.dom.ls.LSOutput;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public final class AppLogger {

    public static Logger getAppLogger(String loggerName){

        Logger logger = Logger.getLogger(loggerName);

        FileHandler fileHandler;
        try {
            String logFilePath = Path.of(AppConfig.LOGS_DIR, LocalDate.now() + ".log").toString();
            fileHandler = new FileHandler(logFilePath, true);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
        } catch (Exception ex) {
            System.getLogger(AppLogger.class.getName())
                .log(System.Logger.Level.ERROR, ex.getMessage(), ex);
        }

        return logger;

    }

}
