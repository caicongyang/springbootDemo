package com.caicongyang;

import com.alibaba.nacos.api.common.Constants;
import com.alibaba.nacos.client.naming.event.InstancesChangeEvent;
import com.alibaba.nacos.common.notify.Event;
import com.alibaba.nacos.common.notify.NotifyCenter;
import com.alibaba.nacos.common.notify.listener.Subscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cloud.loadbalancer.core.CachingServiceInstanceListSupplier;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;

/**
 * 服务变更监听-loadBalancer
 */
public class LoadBalancerServiceChangeNotifier extends Subscriber<InstancesChangeEvent> {

    private static final Logger log = LoggerFactory.getLogger(LoadBalancerServiceChangeNotifier.class);

    @Resource
    private CacheManager defaultLoadBalancerCacheManager;

    @PostConstruct
    public void init() {
        NotifyCenter.registerSubscriber(this);
    }

    @Override
    public void onEvent(InstancesChangeEvent event) {
        String serviceName = event.getServiceName();
        if (serviceName.contains(":")) {
            return;
        }
        String split = Constants.SERVICE_INFO_SPLITER;
        if (serviceName.contains(split)) {
            serviceName = serviceName.substring(serviceName.indexOf(split) + split.length());
        }
        log.info("服务上下线: {}", serviceName);
        Cache cache = defaultLoadBalancerCacheManager.getCache(
                CachingServiceInstanceListSupplier.SERVICE_INSTANCE_CACHE_NAME);
        if (cache != null && cache.get(serviceName) != null) {
            cache.evict(serviceName);
        }
    }

    @Override
    public Class<? extends Event> subscribeType() {
        return InstancesChangeEvent.class;
    }
}
