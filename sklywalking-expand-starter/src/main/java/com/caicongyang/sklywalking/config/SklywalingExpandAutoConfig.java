package com.caicongyang.sklywalking.config;


import com.caicongyang.sklywalking.common.TraceProperties;
import com.caicongyang.sklywalking.http.FeignRequestTraceInterceptor;
import com.caicongyang.sklywalking.http.RestTemplateTraceBeanPostProcessor;
import com.caicongyang.sklywalking.http.ResultStatusDecoder;
import com.caicongyang.sklywalking.http.TraceInterceptor;
import feign.codec.Decoder;
import feign.optionals.OptionalDecoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.info.GitProperties;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(TraceProperties.class)
public class SklywalingExpandAutoConfig {

    @Bean
    @ConditionalOnBean(FeignAutoConfiguration.class)
    public FeignRequestTraceInterceptor feignRequestTraceInterceptor() {
        return new FeignRequestTraceInterceptor();
    }

    @Bean
    @ConditionalOnBean(FeignAutoConfiguration.class)
    public Decoder feignDecoder(ObjectFactory<HttpMessageConverters> messageConverters) {
        return new ResultStatusDecoder(new OptionalDecoder(new ResponseEntityDecoder(new SpringDecoder(messageConverters))));
    }


    @Bean
    public TraceInterceptor traceInterceptor(TraceProperties traceProperties, GitProperties gitProperties) {
        return new TraceInterceptor(traceProperties, gitProperties);
    }


    @Bean
    public RestTemplateTraceBeanPostProcessor traceRestTemplateBeanPostProcessor() {
        return new RestTemplateTraceBeanPostProcessor();
    }
}
