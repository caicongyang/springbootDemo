package com.caicongyang.logger;

import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.ConsoleAppender;

/**
 * 日志脱敏
 */
public class DesensitizationConsoleAppender extends ConsoleAppender {
    @Override
    protected void subAppend(Object event) {
        DesensitizationAppender appender = new DesensitizationAppender();
        appender.operation((LoggingEvent) event);
        super.subAppend(event);
    }

}
