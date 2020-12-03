/*
package com.caicongyang.controllers;

import com.caicongyang.component.RocketMqConsumerConfig;
import com.caicongyang.utils.HttpUtil;
import java.util.Collection;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.registry.Registry;
import org.apache.dubbo.registry.support.AbstractRegistryFactory;
import org.apache.dubbo.registry.zookeeper.ZookeeperRegistry;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/")
public class HealthController {


    @Value("${health.allowIp}")
    private String allowIp;

    private static final Logger logger = LoggerFactory.getLogger(HealthController.class);


    @Autowired
    DefaultMQPushConsumer consumer;


    @Autowired
    RocketMqConsumerConfig rocketMqConsumerConfig;


    @Autowired
    ThreadPoolTaskExecutor threadPoolTaskExecutor;


    private boolean checkIp(String ip) {
        if (StringUtils.isBlank(allowIp)) {
            return false;
        }
        String[] ipArr = StringUtils.split(allowIp, "\\,");
        for (String anIpArr : ipArr) {
            if (StringUtils.equalsIgnoreCase(ip, anIpArr)) {
                return true;
            }
        }
        logger.error("非法ip。ip={}", ip);
        return false;
    }


    */
/**
     * 下线应用
     *//*

    @RequestMapping(value = "/offline", method = {RequestMethod.GET, RequestMethod.POST,
        RequestMethod.HEAD})
    public void offline(HttpServletRequest request, HttpServletResponse response) {
        String ip = HttpUtil.getIpAddress(request);
        if (!checkIp(ip)) {
            logger.info("HealthCheckController.offline not allowed ip={}", ip);
            response.setStatus(403);
            return;
        }

        logger.info("HealthCheckController.offline**************offline*************ip={}", ip);
        dubboOffline();
        rocketMqOffline();
        threadPoolOffline();

    }

    */
/**
     * 线程池优雅停机
     *//*


    public void threadPoolOffline() {
        threadPoolTaskExecutor.shutdown();
    }


    */
/**
     * 下线rocketMq
     *//*

    private void rocketMqOffline() {
        consumer.resume();
        consumer.unsubscribe(rocketMqConsumerConfig.getTopics());
        consumer.shutdown();
    }


    */
/**
     * 下线dubbo
     *//*

    private void dubboOffline() {
        Collection<Registry> registries = AbstractRegistryFactory.getRegistries();
        for (Registry registry : registries) {
            if (registry instanceof ZookeeperRegistry) {
                // 暂时实现基于zookeeper注册中心的上线下线功能，其他注册服务暂时未实现
                ZookeeperRegistry zkr = (ZookeeperRegistry) registry;
                Set<URL> registeredURL = zkr.getRegistered();
                for (URL url : registeredURL) {
                    try {
                        logger.info("doUnregister for {}", url);
                        zkr.doUnregister(url);
                    } catch (Exception e) {
                        logger.error("doUnregister for " + url.toString() + " error", e);
                    }
                }
            } else {
                logger.warn("not zookeeper registry, can not offline");
            }
        }
    }


}
*/
