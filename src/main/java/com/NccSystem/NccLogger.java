package com.NccSystem;

import com.Ncc;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.LoggerRepository;

import java.util.logging.Level;

/**
 * Created by root on 26.09.16.
 */
public class NccLogger extends Logger {

    private String loggerName;

    public NccLogger(String name) {
        super(name);
        loggerName = name;
        getLogger(loggerName).setLevel(org.apache.log4j.Level.toLevel(Ncc.logLevel));
    }

    public Logger setFilename(String logFile) {
        LoggerRepository repository = getLoggerRepository();
        if(repository!=null) repository.resetConfiguration();
        FileAppender fileAppender = new FileAppender();
        fileAppender.setName(this.getClass().getName() + "Logger");
        fileAppender.setFile(logFile);
        fileAppender.setLayout(new PatternLayout("%d{ISO8601} [%-5p] %m%n"));
        fileAppender.setAppend(true);
        fileAppender.activateOptions();

        getLogger(loggerName).addAppender(fileAppender);
        return getLogger(loggerName);
    }
}
