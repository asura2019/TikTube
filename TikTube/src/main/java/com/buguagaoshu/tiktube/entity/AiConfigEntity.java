package com.buguagaoshu.tiktube.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

/**
 * AI配置表
 * 
 * @author Pu Zhiwei
 * @email puzhiweipuzhiwei@foxmail.com
 * @date 2023-09-05 15:03:54
 */
@Data
@TableName("ai_config")
public class AiConfigEntity {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 配置名称
     */
    private String name;

    /**
     * API地址
     */
    private String apiUrl;

    /**
     * API密钥
     */
    private String apiKey;

    /**
     * 模型名称
     */
    private String model;

    /**
     * 评论与弹幕审核使用暂时只能使用默认Prompt
     */
    private String prompt;

    /**
     * 类型 0 默认， 1，评论弹幕审核
     */
    private Integer type;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 创建用户
     */
    private Long createUser;

    /**
     * 更新时间
     */
    private Long updateTime;

    /**
     * 更新用户
     */
    private Long updateUser;

    /**
     * 启用状态，0未启用，1启用
     */
    private Integer status;

    /**
     * 该配置已经使用的token数量
     */
    private Long useTokens;

    private Long maxTokens;
}