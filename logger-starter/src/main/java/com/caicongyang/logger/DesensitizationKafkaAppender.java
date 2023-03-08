package com.caicongyang.logger;

import ch.qos.logback.classic.spi.LoggingEvent;

public class DesensitizationKafkaAppender extends com.github.danielwegener.logback.kafka.KafkaAppender {

    @Override
    protected void append(Object event) {
        DesensitizationAppender appender = new DesensitizationAppender();
        appender.operation((LoggingEvent) event);
        super.append(event);
    }
}
