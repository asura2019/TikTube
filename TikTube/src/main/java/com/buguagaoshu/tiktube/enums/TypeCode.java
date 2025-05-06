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
}
