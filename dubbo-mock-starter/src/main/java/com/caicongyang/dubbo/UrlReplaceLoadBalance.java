package com.caicongyang.dubbo;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.common.extension.SPI;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.cluster.LoadBalance;
import org.apache.dubbo.rpc.cluster.loadbalance.RandomLoadBalance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@SPI("mockLoadBalance")
@Activate(group = {CommonConstants.CONSUMER})
public class UrlReplaceLoadBalance implements LoadBalance {


    @Autowired
    DubboMockProperties dubboMockProperties;


    @Override
    public <T> Invoker<T> select(List<Invoker<T>> invokers, URL url, Invocation invocation) throws RpcException {
        Map<String, String> interfaceMap = dubboMockProperties.getUrlMap();
        if (CollectionUtils.isEmpty(interfaceMap)) {
            return new RandomLoadBalance().select(invokers, url, invocation);
        }

        if (!interfaceMap.keySet().contains(invokers.get(0))) {
            return new RandomLoadBalance().select(invokers, url, invocation);
        }


        URL targetUrl = URL.valueOf(interfaceMap.get(invokers.get(0).getInterface()));

        //根据配置中心去除某一个interface 配置对应的url
        Optional<Invoker<T>> self = invokers.stream().filter(v -> v.getUrl().equals(targetUrl)).findFirst();
        if (self.isPresent()) {
            return self.get();
        } else {
            return new RandomLoadBalance().select(invokers, url, invocation);
        }
    }
}
