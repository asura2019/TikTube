package com.buguagaoshu.tiktube.repository.impl;

import com.buguagaoshu.tiktube.config.WebConstant;
import com.buguagaoshu.tiktube.model.EmailSendCount;
import com.buguagaoshu.tiktube.model.LoginCount;
import com.buguagaoshu.tiktube.repository.CountLimitRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * @create 2025-05-11
 */
@Repository("countLimitRepositoryImpl")
public class CountLimitRepositoryImpl implements CountLimitRepository {

    private final ConcurrentMap<String, LoginCount> loginCount = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, EmailSendCount> emailSendCount = new ConcurrentHashMap<>();

    @Override
    public boolean allowLogin(String key) {
        LoginCount count = loginCount.computeIfAbsent(key, k -> new LoginCount());
        if (count.isLocked()) {
            if (count.getLockExpiryTime().isBefore(LocalDateTime.now())) {
                // 锁定时间已过，重置尝试次数
                count.reset();
                return true;
            }
            return false;
        }
        return true;
    }

    // 记录一次登录失败
    @Override
    public void recordFailedAttempt(String email) {
        LoginCount attempt = loginCount.computeIfAbsent(email, k -> new LoginCount());
        attempt.increment();

        if (attempt.getAttempts() >= WebConstant.MAX_ATTEMPTS) {
            attempt.lock();
        }
    }

    // 记录登录成功，重置尝试次数
    @Override
    public void recordSuccess(String email) {
        loginCount.remove(email);
    }

    // 获取用户剩余尝试次数
    @Override
    public int getRemainingAttempts(String email) {
        LoginCount attempt = loginCount.get(email);
        return attempt != null ? Math.max(0, WebConstant.MAX_ATTEMPTS - attempt.getAttempts()) : WebConstant.MAX_ATTEMPTS;
    }
    
    // 检查是否允许发送邮件
    @Override
    public boolean allowSendEmail(String key) {
        EmailSendCount count = emailSendCount.computeIfAbsent(key, k -> new EmailSendCount());
        return !count.isLimited();
    }
    
    // 记录一次邮件发送
    @Override
    public void recordEmailSent(String key) {
        EmailSendCount count = emailSendCount.computeIfAbsent(key, k -> new EmailSendCount());
        count.increment();
    }
    
    // 获取剩余邮件发送次数
    @Override
    public int getRemainingEmailSendAttempts(String key) {
        EmailSendCount count = emailSendCount.get(key);
        return count != null ? count.getRemainingAttempts() : new EmailSendCount().getRemainingAttempts();
    }
}
