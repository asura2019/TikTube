package com.buguagaoshu.tiktube.vo;

import com.buguagaoshu.tiktube.dto.LoginDetails;
import com.buguagaoshu.tiktube.entity.UserEntity;
import lombok.Data;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * @create 2025-04-25
 */
@Data
public class TOTPLoginKey {
    private String code;

    private UserEntity user;

    private LoginDetails loginDetails;

    private Long expire;

    private String key;
}
