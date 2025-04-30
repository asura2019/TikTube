package com.buguagaoshu.tiktube.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import com.buguagaoshu.tiktube.valid.ListValue;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * OSS配置表
 * 
 * @author Pu Zhiwei
 * @email puzhiweipuzhiwei@foxmail.com
 * @date 2020-09-07 16:00:19
 */
@Data
@TableName("oss_config")
public class OSSConfigEntity {
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 配置名称
     */
    @Size(min = 1, max = 255)
    private String configName;

    /**
     * 存储桶名字
     */
    @Size(min = 1, max = 255)
    private String bucketName;

    /**
     * 端点
     */
    @Size(min = 1, max = 999)
    private String endpoint;

    /**
     * 访问密钥ID
     */
    @Size(min = 1, max = 999)
    private String accessKey;

    /**
     * 访问密钥密码
     */
    @Size(min = 1, max = 999)
    private String secretKey;

    /**
     * 地区
     */
    @Size(min = 1, max = 255)
    private String region;

    /**
     * 自定义域名
     */
    @Size(min = 1, max = 255)
    private String urlPrefix;

    /**
     * Endpoint 访问风格 0 path style，1Virtual Hosted Style
     */
    @ListValue(value = {0,1})
    private Integer pathStyleAccess;

    /**
     * 其它参数配置
     */
    private String other;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 0 关闭，1启用
     */
    @ListValue(value = {0,1})
    private Integer status;

    /**
     * 创建人
     */
    private Long creatorId;

    /**
     * 更新人
     */
    private Long updaterId;

    /**
     * 更新时间
     */
    private Long updateTime;
}