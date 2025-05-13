package com.ccy.mq.branch.git;

import com.ccy.mq.branch.contant.MqBranchPropertyConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.env.PropertiesPropertySourceLoader;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.InputStreamResource;

import java.io.IOException;
import java.io.InputStream;

public class GitPropertiesCustomerLoader implements ApplicationContextInitializer {

    private static final Logger logger = LoggerFactory.getLogger(GitPropertiesCustomerLoader.class);
    @Override
    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
        ConfigurableEnvironment environment = configurableApplicationContext.getEnvironment();
        String enabled = environment.getProperty(MqBranchPropertyConstants.MQ_BRANCH_ENABLE, "false");

        if (!Boolean.valueOf(enabled)) {
            logger.debug("GitPropertiesCustomerLoader is not enabled for context {}, see property: ${{}}", configurableApplicationContext, MqBranchPropertyConstants.MQ_BRANCH_ENABLE);
            return;
        }

        PropertiesPropertySourceLoader loader = new PropertiesPropertySourceLoader();
        try {
            String gitPath = environment.getProperty(MqBranchPropertyConstants.GIT_PATH, "classpath:git.properties");
            String filePath = configurableApplicationContext.getEnvironment().resolvePlaceholders(gitPath);
            InputStream inputStream = configurableApplicationContext.getResource(filePath).getInputStream();
            PropertySource<?> propertySource = loader.load("gitProperties",new InputStreamResource(inputStream)).get(0);
            configurableApplicationContext.getEnvironment().getPropertySources().addFirst(propertySource);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}