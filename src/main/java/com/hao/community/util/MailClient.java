package com.hao.community.util;


// 工具类  邮箱客户端

// 提供发邮件的功能， 委托给新浪去做， 代替了客户端

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Component
public class MailClient {

    // 给spring 去管理  通用的bean在哪儿都能用

    private static final Logger logger = LoggerFactory.getLogger(MailClient.class);


    // 核心组件  Spring 管理
    @Autowired
    private JavaMailSender mailSender;


    // 值注入
    @Value("${spring.mail.username}")
    private String from;

    // 发邮件的  发向方  标题  内容
    public void sendMail(String to, String subject, String content) {
        try {
            // 构建MimeMessage  然后send 即可
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);

            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            // 加了参数表示是html文本  允许支持html文本
            helper.setText(content, true);


            mailSender.send(helper.getMimeMessage());
        } catch (MessagingException e) {
            logger.error("发送邮件失败"+e.getMessage());
        }

    }



}
