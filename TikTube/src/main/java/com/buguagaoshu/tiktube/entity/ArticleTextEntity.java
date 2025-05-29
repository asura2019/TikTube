package com.buguagaoshu.tiktube.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 保存与稿件关联的文章
 * 
 * @author Pu Zhiwei
 * @email puzhiweipuzhiwei@foxmail.com
 */
@Data
@TableName("article_text")
public class ArticleTextEntity {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 关联的文章信息
     */
    private Long articleId;

    /**
     * 文本内容
     */
    private String content;

    /**
     * 作者
     */
    private Long userId;

    /**
     * 文章类型
     * 0 普通文章
     * 1 回复可见
     * 2 加密文章
     */
    private Integer type;

    /**
     * 加密文章密码
     */
    private String password;

    /**
     * 发布时间
     */
    private Long createTime;

    /**
     * 更新时间
     */
    private Long updateTime;

    /**
     * 段落顺序，越小越靠前
     */
    private Integer sort;

    /**
     * 状态
     * 0 正常
     * 1 删除
     */
    private Integer status;
}