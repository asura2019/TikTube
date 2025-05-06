package com.buguagaoshu.tiktube.cache;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * @create 2025-05-03
 */
@Component
public class AdsCountRecorder {

    private final ConcurrentMap<Integer, Long> adsCounts;

    private final ConcurrentMap<Integer, Set<String>> recordedIPs;

    public AdsCountRecorder() {
        this.adsCounts = new ConcurrentHashMap<>();
        this.recordedIPs = new ConcurrentHashMap<>();
    }

    public void recordAds(Integer adsId, String ipAddress) {
        Set<String> ipSet = recordedIPs.computeIfAbsent(adsId, k ->
                Collections.newSetFromMap(new ConcurrentHashMap<>()));

        // 使用 putIfAbsent 原子操作检查 IP 是否已经存在
        if (ipSet.add(ipAddress)) {
            // 原子性地增加播放量
            adsCounts.compute(adsId, (k, v) -> (v == null) ? 1 : v + 1);
        }
    }

    public Map<Integer, Long> getAdsMap() {
        return adsCounts;
    }

    public long getSize() {
        return adsCounts.size();
    }

    public long getAdsCount(Integer videoId) {
        // 从并发 Map 中获取播放量，如果不存在则返回 0
        return adsCounts.getOrDefault(videoId, 0L);
    }

    public void clean() {
        adsCounts.clear();
        recordedIPs.clear();
    }
}
