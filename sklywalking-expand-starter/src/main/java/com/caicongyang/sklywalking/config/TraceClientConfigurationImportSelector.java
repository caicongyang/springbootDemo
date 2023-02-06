package com.caicongyang.sklywalking.config;


import com.caicongyang.sklywalking.annotation.EnableTraceClient;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class TraceClientConfigurationImportSelector implements ImportSelector {


    boolean webTrace = true;

    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        Map<String, Object> annotationAttributes = importingClassMetadata.getAnnotationAttributes(EnableTraceClient.class.getName());
        webTrace = (boolean) annotationAttributes.get("webTrace");
        List<String> needImportBeanNameList = new ArrayList<>();
        needImportBeanNameList.add(SklywalingExpandAutoConfig.class.getName());
        if (webTrace) {
            needImportBeanNameList.add(TraceClientWebBeanRegistrar.class.getName());
        }
        return needImportBeanNameList.toArray(new String[needImportBeanNameList.size()]);
    }


}
