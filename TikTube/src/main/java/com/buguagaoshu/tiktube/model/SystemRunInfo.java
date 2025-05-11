package com.buguagaoshu.tiktube.model;

import com.buguagaoshu.tiktube.TikTubeApplication;
import lombok.Data;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * @create 2025-05-09
 * TODO 写入数据库保存
 */
@Data
public class SystemRunInfo {
    private String javaVersion;

    private String osName;

    private String osVersion;

    private String webServerVersion = TikTubeApplication.VERSION;

    /**
     * 垃圾回收总次数
     * */
    private Long gcCount;

    /**
     * 垃圾回收时间
     * */
    private Long gcTime;

    /**
     * 垃圾回收最大耗时
     * */
    private Double gcTopTime;

    /**
     * 运行时间
     * */
    private Long runTime;


    /**
     * cpu 数量
     * */
    private Integer cpuCount;

    /**
     * 系统CPU使用率
     * */
    private Double systemCpuLoad;

    /**
     * 系统总内存
     * */
    private Long systemTotalMemory;

    /**
     * 系统剩余空闲内存
     * */
    private Long systemFreeMemory;

    /**
     * 当前硬盘可用空间
     * */
    private Long diskAvailableSpace;

    /**
     * 当前磁盘总空间
     * */
    private Long diskTotalSpace;

    /**
     * JVM已分配内存
     * */
    private Long totalMemory;

    /**
     *  JVM空闲内存
     * */
    private Long freeMemory;

    /**
     * JVM最大可用内存
     * */
    private Long maxMemory;

    /**
     * VM已使用内存
     * */
    private Long usedMemory;
}
