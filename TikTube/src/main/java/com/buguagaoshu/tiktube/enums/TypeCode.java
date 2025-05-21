package com.buguagaoshu.tiktube.enums;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * @create 2025-05-01
 */
public class TypeCode {
    /**
     * 账号被锁定
     * */
    public final static int USER_LOCK = 1;


    /**
     * 正常账号
     * */
    public final static int USER_NOT_LOCK = 0;


    /**
     * 正常
     * */
    public final static int NORMAL = 0;


    /**
     * 删除
     * */
    public final static int DELETE = 1;

    /**
     * 待审核
     * */
    public final static int EXAM = -1;

    /**
     * 举报稿件
     * */
    public final static int OPINION_TYPE_ARTICLE = 0;


    /**
     * 举报评论
     * */
    public final static int OPINION_TYPE_COMMENT = 1;

    /**
     * 举报弹幕
     * */
    public final static int OPINION_TYPE_DANMAKU = 2;


    /**
     * 稿件申诉
     * */
    public final static int APPEAL_TYPE_ARTICLE = 3;

    /**
     * 评论申诉
     * */
    public final static int APPEAL_TYPE_COMMENT = 4;

    /**
     * 弹幕申诉
     * */
    public final static int APPEAL_TYPE_DANMAKU = 5;

    /**
     * 意见反馈
     * */
    public final static int OPINION_TYPE_FEEDBACK = 10;


    /**
     * 举报未处理
     * */
    public final static int OPINION_STATUS_UNTREATED = 0;

    /**
     * 举报已处理
     * */
    public final static int OPINION_STATUS_PROCESSED = 1;


    /**
     * 不予受理
     * */
    public final static int OPINION_STATUS_INADMISSIBLE = 2;

    /**
     * 账号邮箱验证未通过
     * */
    public final static int USER_EMAIL_NOT_CHECK = -1;

    /**
     * 首页公告
     * */
    public final static int AD_TYPE_HOME_NOTICE = 0;

    /**
     * 首页弹窗广告
     * */
    public final static int AD_TYPE_HOME_DIALOG = 1;

    /**
     * 视频贴片广告
     * */
    public final static int AD_TYPE_VIDEO = 2;


    /**
     * 轮播新闻
     * */
    public final static int AD_TYPE_NEWS = 3;


    /**
     * 评论与弹幕审核采用的 AI API
     * */
    public final static int AI_TYPE_EXAMINE_COMMENT = 1;


    /**
     * 文本稿件摘要生成
     * */
    public final static int AI_TYPE_TEXT_ABSTRACT = 2;


    /**
     * 图片审核
     * */
    public final static int AI_TYPE_IMAGE = 3;
}
