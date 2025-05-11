package com.buguagaoshu.tiktube.repository;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * @create 2025-05-11
 * 存储次数限制信息
 */
public interface CountLimitRepository {
    /**
     * 登录次数限制
     * */
    boolean allowLogin(String key);

    void recordFailedAttempt(String email);

    void recordSuccess(String email);

    int getRemainingAttempts(String email);
    
    /**
     * 检查是否允许发送邮件
     * @param key 邮箱地址或IP地址
     * @return 是否允许发送
     */
    boolean allowSendEmail(String key);
    
    /**
     * 记录一次邮件发送
     * @param key 邮箱地址或IP地址
     */
    void recordEmailSent(String key);
    
    /**
     * 获取剩余邮件发送次数
     * @param key 邮箱地址或IP地址
     * @return 剩余次数
     */
    int getRemainingEmailSendAttempts(String key);
}
