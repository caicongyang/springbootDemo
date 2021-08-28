package com.caicongyang.utils;

import com.itextpdf.text.pdf.BaseFont;
import com.lowagie.text.DocumentException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.xhtmlrenderer.pdf.ITextFontResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

/**
 * Created by caicongyang on 2017/2/27.
 */


public class PdfUtil {


    private final static Logger LOGGER = LoggerFactory.getLogger(PdfUtil.class);


    private static String path = "";

    static {
        try {
            Resource resource = new DefaultResourceLoader().getResource("");
            if (resource != null) {
                path = resource.getFile().getPath().replaceAll("\\\\", "/");
            }
        } catch (Exception e) {
            LOGGER.error("getResourcePath error", e);
        }
    }

    public static void generate(String html, OutputStream out)
        throws Exception {
        ITextRenderer render = new ITextRenderer();
        ITextFontResolver fontResolver = render.getFontResolver();
        fontResolver
            .addFont(path + "/fonts/simsun.ttc", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
        render.setDocumentFromString(html);
        render.layout();
        render.createPDF(out);
        render.finishPDF();
        render = null;
    }

    public static byte[] generatePdf(String html) throws IOException {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ITextRenderer render = new ITextRenderer();
            ITextFontResolver fontResolver = render.getFontResolver();
            fontResolver
                .addFont(path + "/fonts/simsun.ttc", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
            render.setDocumentFromString(html);
            render.layout();
            render.createPDF(out);
            render.finishPDF();
            render = null;
            return out.toByteArray();
        } catch (DocumentException e) {
            LOGGER.error("PdfUtil.generatePdf", e);
            return null;
        }
    }

}
