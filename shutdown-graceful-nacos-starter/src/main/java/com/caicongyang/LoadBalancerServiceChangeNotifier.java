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

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * 服务变更监听-loadBalancer
 *
 */

public class LoadBalancerServiceChangeNotifier extends Subscriber<InstancesChangeEvent> {

    private static final Logger log = LoggerFactory.getLogger(LoadBalancerServiceChangeNotifier.class);


    /**
     * 由于会有多个类型的 CacheManager bean, 这里的 defaultLoadBalancerCacheManager 名称不可修改
     */
    @Resource
    private CacheManager defaultLoadBalancerCacheManager;

    @PostConstruct
    public void init() {
        // 注册当前自定义的订阅者以获取通知
        NotifyCenter.registerSubscriber(this);
    }

    @Override
    public void onEvent(InstancesChangeEvent event) {
        String serviceName = event.getServiceName();
        // 使用 dubbo 时包含 rpc 服务类会注册以 providers: 或者 consumers: 开头的服务
        // 由于不是正式的服务, 这里需要进行排除, 如果未使用 dubbo 则不需要该处理
        if (serviceName.contains(":")) {
            return;
        }
        // serviceName 格式为 groupName@@name
        String split = Constants.SERVICE_INFO_SPLITER;
        if (serviceName.contains(split)) {
            serviceName = serviceName.substring(serviceName.indexOf(split) + split.length());
        }
        log.info("服务上下线: {}", serviceName);
        // 手动更新服务列表
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