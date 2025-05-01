package com.buguagaoshu.tiktube.vo;

import com.buguagaoshu.tiktube.entity.UserRoleEntity;
import com.buguagaoshu.tiktube.valid.ListValue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-09-05 15:09
 */
@Data
public class User {
    private Long id;

    /**
     * 用户名
     */
    @Size(min = 3, max = 50, message = "用户名长度必须在 3 到 50 个字符之间")
    private String username;

    /**
     * 邮箱
     */
    private String mail;

    /**
     * 密码
     */
    private String password;

    /**
     * 手机号
     */
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
     * 简介
     * */
    @Size(max = 100, message = "个性签名不能超过100字！")
    private String introduction;

    /**
     * 头像
     */
    @Size(max = 500, message = "头像 URL 过长")
    private String avatarUrl;

    /**
     * 首页大图url
     */
    @Size(max = 500, message = "首页顶部大图 URL 过长！")
    private String topImgUrl;


    private long expireTime;


    private UserRoleEntity userRoleEntity;


    private boolean loginStatus;
    private Integer otp;
    private String key;

    @ListValue(value = {0, 1})
    private Integer status;

    /**
     * 封禁截至日期
     * 0 为永久封禁
     */
    private Long blockEndTime;


    /**
     * 负责携带头像文件或者首页图文件的ID
     * */
    private Long fileId;
}
