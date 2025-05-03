package com.buguagaoshu.tiktube.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import com.buguagaoshu.tiktube.valid.ListValue;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 广告以及系统公告
 *
 * @author Pu Zhiwei
 * @email puzhiweipuzhiwei@foxmail.com
 * @date 2020-09-05 14:38:43
 */
@Data
@TableName("advertisement_table")
public class AdvertisementEntity {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 标题
     */
    @Size(max = 255)
    private String title;

    /**
     * 链接地址
     */
    @Size(max = 999)
    private String url;

    /**
     * 图片
     */
    @Size(max = 999)
    private String imageUrl;

    /**
     * 细节描述
     */
    @Size(max = 999)
    private String content;

    /**
     * 视频广告地址
     */
    @Size(max = 999)
    private String videoUrl;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 更新时间
     */
    private Long updateTime;

    /**
     * 创建用户
     */
    private Long createUser;

    /**
     * 状态 0-禁用 1-启用
     */
    private Integer status;

    /**
     * 类型 0-广告 1-系统公告
     */
    @ListValue(value = {0, 1, 2, 3, 4, 5})
    private Integer type;

    /**
     * 开始时间
     */
    private Long startTime;

    /**
     * 结束时间
     */
    private Long endTime;

    /**
     * 点击次数
     */
    private Long viewCount;

    /**
     * 投放位置
     */
    private Integer position;
}