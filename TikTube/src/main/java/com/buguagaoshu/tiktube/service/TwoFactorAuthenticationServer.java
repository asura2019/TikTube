package com.buguagaoshu.tiktube.service;

import com.buguagaoshu.tiktube.vo.TwoFactorData;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * @create 2025-04-25
 * 两步认证接口
 */
public interface TwoFactorAuthenticationServer {
    /**
     * 创建 TOTP 认证基础信息
     * */
    TwoFactorData createTOTPInfo(Long userId, String email);




    boolean verifyTOTPCode(Long userId, String secret, String userInputCode);
}
