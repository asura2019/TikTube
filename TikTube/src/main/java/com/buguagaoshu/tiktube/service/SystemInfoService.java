package com.buguagaoshu.tiktube.service;

import com.buguagaoshu.tiktube.model.SystemRunInfo;
import com.sun.management.OperatingSystemMXBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.List;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * @create 2025-05-09
 */
@Slf4j
@Service
public class SystemInfoService {
    /**
     * 系统启动时间
     */
    private final long startTime = System.currentTimeMillis();

    public SystemRunInfo getSystemRunInfo() {
        SystemRunInfo systemRunInfo = new SystemRunInfo();
        
        // 获取Java版本和操作系统信息
        systemRunInfo.setJavaVersion(System.getProperty("java.version"));
        systemRunInfo.setOsName(System.getProperty("os.name"));
        systemRunInfo.setOsVersion(System.getProperty("os.version"));
        
        // 获取运行时间（毫秒）
        systemRunInfo.setRunTime(System.currentTimeMillis() - startTime);
        
        // 获取CPU信息
        OperatingSystemMXBean operatingSystemMXBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        systemRunInfo.setCpuCount(operatingSystemMXBean.getAvailableProcessors());
        systemRunInfo.setSystemCpuLoad(operatingSystemMXBean.getCpuLoad());
        
        // 获取系统内存信息
        systemRunInfo.setSystemTotalMemory(operatingSystemMXBean.getTotalMemorySize());
        systemRunInfo.setSystemFreeMemory(operatingSystemMXBean.getFreeMemorySize());
        
        // 获取硬盘可用空间
        File file = new File("/");
        systemRunInfo.setDiskAvailableSpace(file.getFreeSpace());
        systemRunInfo.setDiskTotalSpace(file.getTotalSpace());
        
        // 获取JVM内存信息
        // MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        systemRunInfo.setTotalMemory(Runtime.getRuntime().totalMemory());
        systemRunInfo.setFreeMemory(Runtime.getRuntime().freeMemory());
        systemRunInfo.setMaxMemory(Runtime.getRuntime().maxMemory());
        systemRunInfo.setUsedMemory(systemRunInfo.getTotalMemory() - systemRunInfo.getFreeMemory());
        
        // 获取垃圾回收信息
        List<GarbageCollectorMXBean> garbageCollectorMXBeans = ManagementFactory.getGarbageCollectorMXBeans();
        long gcCount = 0;
        long gcTime = 0;
        double gcTopTime = 0;
        
        for (GarbageCollectorMXBean garbageCollectorMXBean : garbageCollectorMXBeans) {
            long count = garbageCollectorMXBean.getCollectionCount();
            long time = garbageCollectorMXBean.getCollectionTime();
            
            if (count > 0) {
                gcCount += count;
                gcTime += time;
                
                // 计算平均每次GC的时间，找出最大值
                double avgTime = (double) time / count;
                if (avgTime > gcTopTime) {
                    gcTopTime = avgTime;
                }
            }
        }
        
        systemRunInfo.setGcCount(gcCount);
        systemRunInfo.setGcTime(gcTime);
        systemRunInfo.setGcTopTime(gcTopTime);
        
        return systemRunInfo;
    }
}
