package com.buguagaoshu.tiktube.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

/**
 * 配置设置表
 * 
 * @author Pu Zhiwei
 * @email puzhiweipuzhiwei@foxmail.com
 * @date 2020-09-05 15:03:54
 */
@Data
@TableName("web_config")
public class WebConfigEntity {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 配置json文件
     */
    private String jsonText;

    /**
     * 创建用户
     */
    private Long createUser;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * ip
     */
    private String ip;
}