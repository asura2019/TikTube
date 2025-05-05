package com.buguagaoshu.tiktube.config;

import com.buguagaoshu.tiktube.model.MailConfigData;
import com.buguagaoshu.tiktube.valid.ListValue;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * @create 2025-05-05
 * 存储系统配置
 * web_setting 表已经弃用
 * 采用 web_config 保存配置文件
 */
@Data
public class WebConfigData {
    /**
     * 网站名
     */
    private String name;

    /**
     * 是否开启非vip每日观看次数限制 [0 关闭， 1 开启]
     * TODO 待实现
     */
    @ListValue(value = {0, 1})
    private Integer openNoVipLimit;

    /**
     * 非vip 每日观看次数
     * TODO 待实现
     */
    private Integer noVipViewCount;

    /**
     * 网页logo地址
     */
    private String logoUrl;

    /**
     * 是否开启邀请码注册 【0 关闭， 1开启】
     */
    @ListValue(value = {0, 1})
    private Integer openInvitationRegister;

    /**
     * 网页简短的描述
     */
    private String webDescribe;

    /**
     * 是否开启每日上传视频增加非会员观看次数 【0 关闭， 1开启】
     * TODO 待实现
     */
    @ListValue(value = {0, 1})
    private Integer openUploadVideoAddViewCount;

    /**
     * 是否开启视频，文章，图片审核 【0 关闭， 1 开启】
     */
    @ListValue(value = {0, 1})
    private Integer openExamine;


    private Integer homeMaxVideoCount;

    /**
     * 是否开启邮箱设置
     * */
    @ListValue(value = {0, 1})
    private Integer openEmail;


    private MailConfigData mailConfig;

    /**
     * 设置默认配置
     * */
    public static WebConfigData defaultConfig() {
        WebConfigData webConfigData = new WebConfigData();
        webConfigData.setName("TikTube");
        webConfigData.setOpenNoVipLimit(0);
        webConfigData.setNoVipViewCount(0);
        webConfigData.setOpenInvitationRegister(1);
        webConfigData.setWebDescribe("一个牛逼的视频网站!");
        webConfigData.setOpenUploadVideoAddViewCount(0);
        webConfigData.setOpenExamine(1);
        webConfigData.setHomeMaxVideoCount(50);
        webConfigData.setOpenEmail(0);
        webConfigData.setLogoUrl("/favicon.jpg");
        webConfigData.setMailConfig(new MailConfigData());
        return webConfigData;

    }

    /**
     * 拷贝对象
     * */
    public WebConfigData copy() {
        WebConfigData webConfigData = new WebConfigData();
        webConfigData.setName(this.name);
        webConfigData.setOpenNoVipLimit(this.openNoVipLimit);
        webConfigData.setNoVipViewCount(this.noVipViewCount);
        webConfigData.setOpenInvitationRegister(this.openInvitationRegister);
        webConfigData.setWebDescribe(this.webDescribe);
        webConfigData.setOpenUploadVideoAddViewCount(this.openUploadVideoAddViewCount);
        webConfigData.setOpenExamine(this.openExamine);
        webConfigData.setHomeMaxVideoCount(this.homeMaxVideoCount);
        webConfigData.setOpenEmail(this.openEmail);
        webConfigData.setLogoUrl(this.logoUrl);
        webConfigData.setMailConfig(this.mailConfig);
        return webConfigData;
    }


    /**
     * 数据脱敏
     * */
    public WebConfigData clean() {
        this.mailConfig = null;
        return this;
    }

    /**
     * 创建保存到数据库的数据
     * */
    public static String createSaveData(WebConfigData webConfigData) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(webConfigData);
        } catch (Exception e) {
            return "";
        }
    }

    public static WebConfigData analysisData(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(json, WebConfigData.class);
        } catch (Exception e) {
            return null;
        }
    }
}
