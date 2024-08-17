package org.example.basic;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerBase {

    public static final String SYSTEM = "system";

    private static Logger logger = LoggerFactory.getLogger(SYSTEM);

    protected static void log(LogAct logAct) {
        //记录类名
        //LoggerCtx.target(target);

        //TODO: 暂时不做租户日志隔离
        //logAct.apply(LogCtx.logger());
        logAct.apply(logger);

        LogCtx.adjustStep();

        //LoggerCtx.rmTarget();
    }

    protected static Level getLevel() {
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        Configuration config = context.getConfiguration();
        return config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME).getLevel();
    }
}
