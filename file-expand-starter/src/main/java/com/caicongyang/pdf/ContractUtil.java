package com.caicongyang.pdf;

import com.caicongyang.utils.FreemarkerTemplateUtils;
import com.caicongyang.utils.PdfUtil;
import java.io.IOException;
import java.util.Map;
import org.springframework.stereotype.Component;


/**
 * @author caicongyang
 */
@Component
public class ContractUtil {


    /**
     * @param dataMap 模板中需要填入的参数
     * @param templateName 在resources/templates下的模板名称
     */
    public byte[] generateContract(String templateName, Map<String, Object> dataMap)
        throws IOException {

        String htmlStr = FreemarkerTemplateUtils
            .getContentByTemplateName(templateName, dataMap);
        byte[] bytes = PdfUtil.generatePdf(htmlStr);
        return bytes;

    }
}
