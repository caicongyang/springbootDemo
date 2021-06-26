package com.caicongyang.stock.mail;

import com.caicongyang.stock.mail.impl.MailServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.caicongyang.stock.BaseApplicationTest;

public class MailServiceImplTest extends BaseApplicationTest {

    @Autowired
    MailServiceImpl mailService;

    @Test
    public void test() {
        String to = "1491318829@qq.com";
        String subject = "猜猜我今天买了啥";
        String content = "<html><head></head><body><h3>哈哈，什么都没有</h3></body></html>";
        try {
            mailService.sendHtmlMail(to, subject, content);
            System.out.println("成功了");
        } catch (Exception e) {
            System.out.println("失败了");
            e.printStackTrace();
        }
    }

}
