package org.example.basic;

import org.apache.logging.log4j.Level;

public class Logger extends LoggerBase {

    public static void trace(String message, Object... obj) {
        if (getLevel() == Level.TRACE) {
            log(l -> l.trace(message, obj));
        }
    }

    public static void debug(String message, Object... obj) {
        Level level = getLevel();
        if (level == Level.TRACE || level == Level.DEBUG) {
            System.out.println("level2:" + level.toString());
            log(l -> l.debug(message, obj));
        }
    }

    public static void info(String message, Object... obj) {
        log(l -> l.info(message, obj));
    }

    public static void warn(String message, Object... obj) {
        log(l -> l.warn(message, obj));
    }

    public static void error(String message, Object... obj) {
        log(l -> l.error(message, obj));
    }

    public static void trace(Object obj) {
        if (getLevel() == Level.TRACE) {
            log(l -> l.trace("{}", obj));
        }
    }

    public static void debug(Object obj) {
        Level level = getLevel();
        if (level == Level.TRACE || level == Level.DEBUG) {
            log(l -> l.debug("{}", obj));
        }
    }

    public static void info(Object obj) {
        log(l -> l.info("{}", obj));
    }

    public static void warn(Object obj) {
        log(l -> l.warn("{}", obj));
    }

    public static void error(Object obj) {
        log(l -> l.error("{}", obj));
    }
}
