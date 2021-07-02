package com.hao.community;

import com.hao.community.util.MailClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MailTests {
    @Autowired
    private MailClient mailClient;


    // 模板引擎  发邮件需要主动去调用模板引擎
    @Autowired
    private TemplateEngine templateEngine;


    @Test
    public void testTextMail() {
        mailClient.sendMail("1332941292@qq.com", "逗比", "gbgbgbgb.");
    }
    
    @Test
    public void testHtmlMail() {
        Context context = new Context();
        context.setVariable("username", "sunday");

        String content = templateEngine.process("/mail/demo", context);
        System.out.println(content);


        mailClient.sendMail("1332941292@qq.com", "HTML", content);
    }
}
