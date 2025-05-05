package com.buguagaoshu.tiktube.service;

/**
 * @author puzhiwei
 * 发送验证消息
 * */
public interface SendMessageService {

    /**
     * @param key 邮箱或其它验证信息
     * @param message 验证码
     * */
    void send(String key, String message);
}
