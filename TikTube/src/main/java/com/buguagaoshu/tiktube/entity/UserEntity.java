package com.buguagaoshu.tiktube.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import com.buguagaoshu.tiktube.valid.ListValue;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;
import lombok.Data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * @author Pu Zhiwei
 * @email puzhiweipuzhiwei@foxmail.com
 * @date 2020-09-05 14:38:43
 */
@Data
@TableName("user")
public class UserEntity {

    /**
     * 用户ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度必须在 3 到 50 个字符之间")
    private String username;

    /**
     * 邮箱
     */
    @Email(message = "邮箱格式不正确")
    @NotBlank(message = "邮箱不能为空")
    @Size(min = 1, max = 200, message = "邮箱过长")
    private String mail;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 50, message = "密码长度不能小于6位")
    private String password;

    /**
     * 手机号
     */
    @Size(max = 50, message = "电话长度错误")
    private String phone;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 提交视频，图片，文章数
     */
    private Long submitCount;

    /**
     * 关注数
     */
    private Long followCount;

    /**
     * 粉丝数
     */
    private Long fansCount;

    /**
     * 头像
     */
    private String avatarUrl;

    /**
     * 简介
     */
    @Size(max = 999, message = "个性签名不能超过 999 个字")
    private String introduction;

    /**
     * 首页大图url
     */
    @Size(max = 999)
    private String topImgUrl;

    /**
     * 上次投稿时间
     */
    private Long lastPublishTime;


    /**
     * 是否开启 OTP 二次认证
	 * 0 关闭， 1 开启
     */
	@ListValue(value = {0, 1})
    private Integer otp;

    /**
     * TOTP 密钥
     * */
    private String otpSecret;


    private String otpRecovery;

    /**
     * 账号状态 0 正常， 1 被封禁
     */
    @ListValue(value = {0, 1})
    private Integer status;

    /**
     * 封禁截至日期
     * 0 为永久封禁
     */
    private Long blockEndTime;


    @NotBlank(message = "验证码不为空")
    @TableField(exist = false)
    private String verifyCode;

    @TableField(exist = false)
    private String emailCode;


    @TableField(exist = false)
    private String invitationCode;

    @TableField(exist = false)
    private boolean friend;


    public void clean() {
        this.password = null;
        this.phone = null;
        this.mail = null;
        this.invitationCode = null;
        this.verifyCode = null;
		this.otp = null;
        this.otpSecret = null;
        this.emailCode = null;
    }

    public Integer getOtp() {
        if (this.otp == null) {
            return 0;
        }
        return this.otp;
    }
}
