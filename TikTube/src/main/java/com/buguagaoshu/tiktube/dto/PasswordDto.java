package com.buguagaoshu.tiktube.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;


/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-09-08 15:22
 */
@Data
public class PasswordDto {
    @NotBlank(message = "请输入原始密码")
    @Size(max = 50)
    private String oldPassword;

    @NotBlank(message = "请输入新密码")
    @Size(max = 50)
    private String newPassword;


    @NotBlank(message = "验证码不能为空")
    @Size(max = 8)
    private String verifyCode;
}
