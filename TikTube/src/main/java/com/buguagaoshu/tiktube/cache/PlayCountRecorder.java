package com.buguagaoshu.tiktube.cache;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * @create 2025-04-22
 * 播放量缓存计数器
 * 缓存播放量，定期写入数据库
 * 减少数据库读写压力
 */
@Component
public class PlayCountRecorder {
    // 使用 ConcurrentHashMap 存储每个视频 ID 对应的播放量，支持并发操作
    private final ConcurrentMap<Long, Long> playCounts;
    // 使用 ConcurrentHashMap 存储每个视频 ID 对应的已记录 IP 地址集合，支持并发操作
    private final ConcurrentMap<Long, Set<String>> recordedIPs;

    public PlayCountRecorder() {
        this.playCounts = new ConcurrentHashMap<>();
        this.recordedIPs = new ConcurrentHashMap<>();
    }

    public void recordPlay(Long videoId, String ipAddress) {
        // 尝试获取或创建视频 ID 对应的 IP 地址集合
        Set<String> ipSet = recordedIPs.computeIfAbsent(videoId, k ->
                Collections.newSetFromMap(new ConcurrentHashMap<>()));

        // 使用 putIfAbsent 原子操作检查 IP 是否已经存在
        if (ipSet.add(ipAddress)) {
            // 原子性地增加播放量
            playCounts.compute(videoId, (k, v) -> (v == null) ? 1 : v + 1);
        }
    }

    public Map<Long, Long> getPlayMap() {
        return playCounts;
    }

    public long getSize() {
        return playCounts.size();
    }

    public long getPlayCount(Long videoId) {
        // 从并发 Map 中获取播放量，如果不存在则返回 0
        return playCounts.getOrDefault(videoId, 0L);
    }

    public void clean() {
        playCounts.clear();
        recordedIPs.clear();
    }
}
