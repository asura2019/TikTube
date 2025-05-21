package com.buguagaoshu.tiktube.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import com.buguagaoshu.tiktube.valid.ListValue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 举报以及意见反馈表
 * 
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * @create 2025-05-07
 */
@Data
@TableName("opinion_table")
public class OpinionEntity {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 被举报的目标
     */
    private Long targetId;

    /**
     * 举报申诉人
     */
    private Long userId;

    /**
     * 用户举报原因，意见建议
     */
    @Size(max = 10000)
    @NotBlank(message = "举报原因不能为空！")
    private String userOpinion;

    /**
     * 举报类型 0 稿件 1 评论 2 弹幕
     */
    @ListValue(value = {0,1,2,3,4,5,10})
    private Integer type;

    /**
     * 状态 0 未处理，1已处理,2不予受理
     */
    private Integer status;

    /**
     * 申诉处理状态，0 恢复， 1 不恢复
     * */
    private Integer appealStatus;

    /**
     * 处理意见
     */
    private String opinion;

    /**
     * 处理人
     */
    private Long opinionUser;

    /**
     * 举报时间
     */
    private Long createTime;

    /**
     * 处理日期
     */
    private Long opinionTime;

    /**
     * 其它信息
     */
    private String otherInfo;


    private String ip;

    private String ua;

    private String city;
}
