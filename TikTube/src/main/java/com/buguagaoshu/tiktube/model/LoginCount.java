package com.buguagaoshu.tiktube.model;

import com.buguagaoshu.tiktube.config.WebConstant;
import lombok.Getter;

import java.time.LocalDateTime;


/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * @create 2025-05-11
 */
@Getter
public class LoginCount {
    private int attempts = 0;
    private LocalDateTime lockExpiryTime = null;

    public synchronized void increment() {
        attempts++;
    }

    public synchronized void lock() {
        if (attempts >= WebConstant.MAX_ATTEMPTS) {
            lockExpiryTime = LocalDateTime.now().plusMinutes(WebConstant.LOCK_DURATION);
        }
    }

    public synchronized void reset() {
        attempts = 0;
        lockExpiryTime = null;
    }

    public boolean isLocked() {
        return lockExpiryTime != null && lockExpiryTime.isAfter(LocalDateTime.now());
    }
}
