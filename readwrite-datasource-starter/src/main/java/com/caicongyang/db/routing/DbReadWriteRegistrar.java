package com.caicongyang.db.routing;

import com.caicongyang.db.routing.anno.EnableDbReadWrite;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DbReadWriteRegistrar implements ImportBeanDefinitionRegistrar {

    private String pointcutBeanName = "dbRoutingPointcut";

    private String routingAdviceBeanName = "dbRoutingInterceptor";

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry registry) {
        Map<String, Object> annotationAttributes = annotationMetadata.getAnnotationAttributes(EnableDbReadWrite.class.getName());
        AnnotationAttributes attributes = AnnotationAttributes.fromMap(annotationAttributes);
        buildRoutingAdvisor(attributes, registry);
    }

    /**
     * 构建切面
     *
     * @param attributes
     * @param registry
     */
    private void buildRoutingAdvisor(AnnotationAttributes attributes, BeanDefinitionRegistry registry) {
        String[] interceptionPackage = attributes.getStringArray("routingInterceptionPackage");
        String[] packages = Arrays.stream(interceptionPackage).flatMap(
                pkg -> Arrays.stream(StringUtils.tokenizeToStringArray(pkg, ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS))
        ).toArray(String[]::new);

        String pointcutBeanName = buildRoutingPointcut(registry, packages);
        String interceptorBeanName = buildRoutingInterceptor(attributes, registry);

        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.rootBeanDefinition(DefaultPointcutAdvisor.class);
        beanDefinitionBuilder.addConstructorArgReference(pointcutBeanName);
        beanDefinitionBuilder.addConstructorArgReference(interceptorBeanName);
        beanDefinitionBuilder.addPropertyValue("order", -1);
        registry.registerBeanDefinition(pointcutBeanName + "-routingAdvisor", beanDefinitionBuilder.getBeanDefinition());
    }

    /**
     * 构建拦截器
     *
     * @param attributes
     * @param registry
     * @return
     */
    private String buildRoutingInterceptor(AnnotationAttributes attributes, BeanDefinitionRegistry registry) {
        String beanName = routingAdviceBeanName;
        if (registry.containsBeanDefinition(beanName)) {
            return beanName;
        }

        Map<String, String> methodDataSourceMapping = new HashMap<String, String>();
        String[] writePackages = attributes.getStringArray("writePackage");
        String[] readPackages = attributes.getStringArray("readPackage");
        for (String name : writePackages) {
            methodDataSourceMapping.put(name, "write");
        }
        for (String name : readPackages) {
            methodDataSourceMapping.put(name, "read");
        }
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.rootBeanDefinition(DbReadWriteRoutingInterceptor.class);
        beanDefinitionBuilder.addPropertyValue("packageDataSourceMapping", methodDataSourceMapping);
        registry.registerBeanDefinition(beanName, beanDefinitionBuilder.getBeanDefinition());
        return beanName;
    }


    /**
     * 构建切点
     *
     * @param registry
     * @param packages
     * @return
     */
    private String buildRoutingPointcut(BeanDefinitionRegistry registry, String[] packages) {
        String beanName = pointcutBeanName;

        if (registry.containsBeanDefinition(beanName)) {
            return beanName;
        }

        StringBuilder expression = new StringBuilder("execution(* com.caicongyang.*Service.*(..))");
        for (String pkg : packages) {
            expression.append(" or execution(* ").append(pkg).append("..*.*(..))");
        }

        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.rootBeanDefinition(AspectJExpressionPointcut.class);
        beanDefinitionBuilder.addPropertyValue("expression", expression.toString());
        registry.registerBeanDefinition(beanName, beanDefinitionBuilder.getBeanDefinition());
        return beanName;
    }
}
