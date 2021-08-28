package com.caicongyang.utils;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.Version;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

/**
 * 类TemplateUtils.java的实现描述：freemarker 模板处理工具类
 *
 * @author caicongyang 2015年12月31日 下午7:46:00
 */
public class FreemarkerTemplateUtils {


    private final static Logger LOGGER = LoggerFactory.getLogger(FreemarkerTemplateUtils.class);


    /**
     * 根据模板名称获取模板
     */
    public static Template getTemplateByName(String templateName) throws Exception {
        return getTemplate(templateName, "UTF-8");
    }

    public static Template getTemplate(String templateName, String charset) throws Exception {
        FreeMarkerConfigurationFactoryBean freemarkerConfiguration = new FreeMarkerConfigurationFactoryBean();
        freemarkerConfiguration.setTemplateLoaderPath("classpath:templates/");
        Configuration cfg = freemarkerConfiguration.createConfiguration();
        return cfg.getTemplate(templateName, charset);
    }

    /**
     * 根据模板名加载模板并解析
     */
    public static String getContentByTemplateName(String templateName,
        Map<String, Object> dataMap) {
        String result = null;
        try {
            Template template = getTemplateByName(templateName);
            result = FreeMarkerTemplateUtils.processTemplateIntoString(template, dataMap);
            if (result != null) {
                result = result.replaceAll("\\r", "").replaceAll("\\n", "").replaceAll("\\t", "");
            }
        } catch (Exception e) {
            LOGGER.error("Fill template {} error, pay attention to the given parameter. {}",
                templateName, dataMap, e);
        }

        return result;
    }

    /**
     * 根据内容加载模板内容并解析
     */
    public static String getContent(String templetString, Map<String, Object> dataMap) {
        Template template = null;
        String result = null;
        try {
            template = getTemplate(templetString);
            result = FreeMarkerTemplateUtils.processTemplateIntoString(template, dataMap);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 根据模板字符串获取模板
     */
    public static Template getTemplate(String templetString) throws Exception {
        Configuration cfg = new Configuration(new Version("2.3.28"));
        StringTemplateLoader stringLoader = new StringTemplateLoader();
        stringLoader.putTemplate("template", templetString);
        cfg.setTemplateLoader(stringLoader);
        return cfg.getTemplate("template", "UTF-8");
    }

}
