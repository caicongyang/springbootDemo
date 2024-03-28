package com.caicongyang;

import com.alibaba.nacos.api.common.Constants;
import com.alibaba.nacos.client.naming.event.InstancesChangeEvent;
import com.alibaba.nacos.common.notify.Event;
import com.alibaba.nacos.common.notify.NotifyCenter;
import com.alibaba.nacos.common.notify.listener.Subscriber;
import com.netflix.loadbalancer.ZoneAwareLoadBalancer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.ribbon.SpringClientFactory;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Optional;

public class RibbonServiceChangeNotifier extends Subscriber<InstancesChangeEvent> {

    private static final Logger log = LoggerFactory.getLogger(RibbonServiceChangeNotifier.class);


    @Resource
    private SpringClientFactory springClientFactory;

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
        // 针对服务进行后续更新操作
        // 如果自定义负载均衡方式则将默认的 ZoneAwareLoadBalancer 替换为自己的实现即可
        Optional.ofNullable(springClientFactory.getLoadBalancer(serviceName))
                .ifPresent(loadBalancer ->
                        ((ZoneAwareLoadBalancer<?>) loadBalancer).updateListOfServers());
    }

    @Override
    public Class<? extends Event> subscribeType() {
        return InstancesChangeEvent.class;
    }

}
