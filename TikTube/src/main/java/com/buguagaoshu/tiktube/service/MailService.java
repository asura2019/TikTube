package com.buguagaoshu.tiktube.service;

import com.buguagaoshu.tiktube.cache.WebSettingCache;
import com.buguagaoshu.tiktube.model.MailConfigData;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Properties;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * @create 2025-05-05
 */
@Service
@Slf4j
public class MailService {
    private final JavaMailSenderImpl javaMailSender;

    private final WebSettingCache webSettingCache;

    @Autowired
    public MailService(WebSettingCache webSettingCache) {
        this.webSettingCache = webSettingCache;
        this.javaMailSender = new JavaMailSenderImpl();
    }

    /**
     * 初始化邮件配置
     * */
    public void initMainConfig() {
        MailConfigData mailConfig = webSettingCache.getWebConfigData().getMailConfig();
        javaMailSender.setDefaultEncoding("utf-8");
        javaMailSender.setHost(mailConfig.getHost());              // 设置邮箱服务器
        javaMailSender.setPort(mailConfig.getPort());                        // 设置端口
        javaMailSender.setUsername(mailConfig.getUsername());    // 设置用户名
        javaMailSender.setPassword(mailConfig.getPassword());      // 设置密码（记得替换为你实际的密码、授权码）
        javaMailSender.setProtocol(mailConfig.getProtocol());                // 设置协议
        Properties properties = new Properties();           // 配置项
        properties.put("mail.smtp.auth", true);
        properties.put("mail.smtp.starttls.enable", true);
        properties.put("mail.smtp.starttls.required", true);
        properties.put("mail.smtp.ssl.enable", true);


        javaMailSender.setJavaMailProperties(properties); // 设置配置项
    }

    public void sendMail(String to, String subject, String content) {
        // 创建一个邮件消息
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            // 创建 MimeMessageHelper
            MimeMessageHelper helper = new MimeMessageHelper(message, false);
            helper.setFrom(webSettingCache.getWebConfigData().getMailConfig().getUsername(), webSettingCache.getWebConfigData().getName());
            // 收件人邮箱
            helper.setTo(to);
            // 邮件标题
            helper.setSubject(subject);
            // 邮件正文，第二个参数表示是否是HTML正文
            helper.setText(content, true);

            // 发送
            javaMailSender.send(message);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }


    public String verificationCodeInfo(String verificationCode) {
        return "欢迎使用：" + webSettingCache.getWebConfigData().getName() + "，你的验证码是：" + verificationCode;
    }
}
