package com.caicongyang.sklywalking.config;


import com.caicongyang.sklywalking.annotation.EnableTraceClient;
import com.caicongyang.sklywalking.common.TraceConstant;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.web.servlet.handler.MappedInterceptor;


/**
 *
 */
public class TraceClientWebBeanRegistrar implements ImportBeanDefinitionRegistrar {


    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {

        String[] includePatterns = getAnnotationAttributesValue(importingClassMetadata, "includePatterns");
        String[] excludePatterns = getAnnotationAttributesValue(importingClassMetadata, "excludePatterns");

        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClass(MappedInterceptor.class);

        beanDefinition.getConstructorArgumentValues().addIndexedArgumentValue(0, includePatterns);
        beanDefinition.getConstructorArgumentValues().addIndexedArgumentValue(1, excludePatterns);
        beanDefinition.getConstructorArgumentValues().addIndexedArgumentValue(2, new RuntimeBeanReference(TraceConstant.TRACE_INTERCEPTOR_BEAN_NAME));
        BeanDefinitionReaderUtils.registerWithGeneratedName(beanDefinition, registry);
    }

    private String[] getAnnotationAttributesValue(AnnotationMetadata metadata, String attribute) {

        AnnotationAttributes attributes = AnnotationAttributes.fromMap(
                metadata.getAnnotationAttributes(EnableTraceClient.class.getName()));

        String[] basePackages = attributes.getStringArray(attribute);

        return basePackages;
    }

}
