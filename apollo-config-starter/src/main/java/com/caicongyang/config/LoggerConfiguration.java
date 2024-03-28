package com.caicongyang.config;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfig;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfigChangeListener;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggingSystem;

import javax.annotation.Resource;
import java.util.Set;

/**
 * @Description TODO
 * @Author caicongyang
 * @Date 2020/9/8 17:56
 */
public class LoggerConfiguration {
    private static final Logger log = LoggerFactory.getLogger(LoggerConfiguration.class);
    private static final String LOGGER_TAG = "logging.level.";

    @Resource
    private LoggingSystem loggingSystem;

    @ApolloConfig
    private Config config;

    @ApolloConfigChangeListener(interestedKeyPrefixes = "logging")
    private void configChangeListener(ConfigChangeEvent changeEvent) {
        log.info("logger configure refresh");
        refreshLoggingLevels();
    }


    private void refreshLoggingLevels() {
        Set<String> keyNames = config.getPropertyNames();
        for (String key : keyNames) {
            if (StringUtils.containsIgnoreCase(key, LOGGER_TAG)) {
                String strLevel = config.getProperty(key, "info");
                LogLevel level = LogLevel.valueOf(strLevel.toUpperCase());

                loggingSystem.setLogLevel(key.replace(LOGGER_TAG, ""), level);
                log.info("{}:{}", key, strLevel);
            }
        }
    }
}
