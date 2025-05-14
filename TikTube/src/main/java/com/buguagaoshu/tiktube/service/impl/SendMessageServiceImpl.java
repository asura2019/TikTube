package com.buguagaoshu.tiktube.service.impl;

import com.buguagaoshu.tiktube.cache.WebSettingCache;
import com.buguagaoshu.tiktube.service.MailService;
import com.buguagaoshu.tiktube.service.SendMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author puzhiwei
 */
@Service
@Slf4j
public class SendMessageServiceImpl implements SendMessageService {

    private final MailService mailService;

    private final WebSettingCache webSettingCache;

    @Autowired
    public SendMessageServiceImpl(MailService mailService,
                                  WebSettingCache webSettingCache) {
        this.mailService = mailService;
        this.webSettingCache = webSettingCache;
    }

    @Override
    public void send(String key, String message) {
        log.info("Send to key:[{}] with message:[{}]", key, message);
        // 发送邮箱验证码
        if (webSettingCache.getWebConfigData().getOpenEmail()) {
            boolean res = mailService.sendMail(
                    key,
                    webSettingCache.getWebConfigData().getName(),
                    mailService.verificationCodeInfo(message)
            );
        }

    }
}
