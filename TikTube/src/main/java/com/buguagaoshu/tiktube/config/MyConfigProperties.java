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

    private Boolean isTheProxyConfigured;

    private String appName;

    /**
     * 文件保存位置
     * 0 本地
     * 1 oss
     * */
    private Integer fileSaveLocation;

    /**
     * 对象存储部分配置
     * */
    private String ossEndpoint;

    private String ossAccessKey;

    private String ossSecretKey;

    private String ossBucketName;

    public Boolean getIsTheProxyConfigured() {
        return Objects.requireNonNullElse(this.isTheProxyConfigured, false);
    }

    public String getAppName() {
        return Objects.requireNonNullElse(this.appName, "TikTube");
    }

    public int getFileSaveLocation() {
        return Objects.requireNonNullElse(this.fileSaveLocation, 0);
    }
}
