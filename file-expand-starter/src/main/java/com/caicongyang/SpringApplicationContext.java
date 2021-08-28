package com.caicongyang;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.util.Assert;

/**
 * @author WuBo
 * @version 1.0.1
 * @CreateDate 2010-11-26
 */
public class SpringApplicationContext implements ApplicationContextAware {

    private static ApplicationContext ctx;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ctx = applicationContext;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getBean(String beanName) {
        Assert.notNull(ctx, "ApplicationContext cannot be null");

        return (T) ctx.getBean(beanName);
    }

    public static <T> T getBean(String beanName, Class<T> type) {
        Assert.notNull(ctx, "ApplicationContext cannot be null");

        return (T) ctx.getBean(beanName, type);
    }

    public static <T> T getBean(Class<T> type) {
        Assert.notNull(ctx, "ApplicationContext cannot be null");

        return (T) ctx.getBean(type);
    }

    public static void publishEvent(ApplicationEvent event) {
        Assert.notNull(ctx, "ApplicationContext cannot be null");

        ctx.publishEvent(event);
    }

    public static ApplicationContext getCtx() {
        return ctx;
    }
}