package com.buguagaoshu.tiktube.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Objects;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * @create 2025-04-23
 */
@ConfigurationProperties(
        prefix = "tiktube"
)
@Data
public class MyConfigProperties {
    private String ipDbPath;

    private Boolean isTheProxyConfigured = false;

    private Boolean openRedis = false;

    // Redis发布订阅的频道名称
    private String commentChannelName = "comment_examine_channel";

    private String danmakuChannelName = "danmaku_examine_channel";
}
