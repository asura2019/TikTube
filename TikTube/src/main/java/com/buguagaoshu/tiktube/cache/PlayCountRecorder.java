package com.buguagaoshu.tiktube.cache;

import com.buguagaoshu.tiktube.config.MyConfigProperties;
import com.buguagaoshu.tiktube.repository.RedisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * @create 2025-04-22
 * 播放量缓存计数器
 * 缓存播放量，定期写入数据库
 * 减少数据库读写压力
 */
@Component
public class PlayCountRecorder {
    // Redis中存储的键名前缀
    private static final String PLAY_COUNT_KEY_PREFIX = "play:count:";
    private static final String PLAY_IP_KEY_PREFIX = "play:ip:";
    
    private final PlayStorage playStorage;

    @Autowired
    public PlayCountRecorder(RedisRepository redisRepository, MyConfigProperties configProperties) {
        // 根据配置决定使用哪种存储实现
        if (configProperties.getOpenRedis() && redisRepository.isAvailable()) {
            this.playStorage = new RedisPlayStorage(redisRepository);
        } else {
            this.playStorage = new MemoryPlayStorage();
        }
    }

    public void recordPlay(Long videoId, String ipAddress) {
        playStorage.recordPlay(videoId, ipAddress);
    }

    public Map<Long, Long> getPlayMap() {
        return playStorage.getPlayMap();
    }

    public long getSize() {
        return playStorage.getSize();
    }

    public long getPlayCount(Long videoId) {
        return playStorage.getPlayCount(videoId);
    }

    public void clean() {
        playStorage.clean();
    }
    
    // 播放存储接口
    private interface PlayStorage {
        void recordPlay(Long videoId, String ipAddress);
        Map<Long, Long> getPlayMap();
        long getSize();
        long getPlayCount(Long videoId);
        void clean();
    }
    
    // 内存存储实现
    private static class MemoryPlayStorage implements PlayStorage {
        private final Map<Long, Long> playCounts;
        private final Map<Long, Set<String>> recordedIPs;
        
        public MemoryPlayStorage() {
            this.playCounts = new ConcurrentHashMap<>();
            this.recordedIPs = new ConcurrentHashMap<>();
        }
        
        @Override
        public void recordPlay(Long videoId, String ipAddress) {
            Set<String> ipSet = recordedIPs.computeIfAbsent(videoId, k ->
                    Collections.newSetFromMap(new ConcurrentHashMap<>()));

            // 使用 putIfAbsent 原子操作检查 IP 是否已经存在
            if (ipSet.add(ipAddress)) {
                // 原子性地增加播放量
                playCounts.compute(videoId, (k, v) -> (v == null) ? 1 : v + 1);
            }
        }
        
        @Override
        public Map<Long, Long> getPlayMap() {
            return playCounts;
        }
        
        @Override
        public long getSize() {
            return playCounts.size();
        }
        
        @Override
        public long getPlayCount(Long videoId) {
            return playCounts.getOrDefault(videoId, 0L);
        }
        
        @Override
        public void clean() {
            playCounts.clear();
            recordedIPs.clear();
        }
    }
    
    // Redis存储实现
    private static class RedisPlayStorage implements PlayStorage {
        private final RedisRepository redisRepository;
        
        public RedisPlayStorage(RedisRepository redisRepository) {
            this.redisRepository = redisRepository;
        }
        
        @Override
        public void recordPlay(Long videoId, String ipAddress) {
            String ipKey = PLAY_IP_KEY_PREFIX + videoId;
            // 尝试将IP添加到集合中，如果添加成功（之前不存在）则返回1
            if (redisRepository.sSet(ipKey, ipAddress) > 0) {
                // 增加计数
                redisRepository.incr(PLAY_COUNT_KEY_PREFIX + videoId, 1);
            }
        }
        
        @Override
        public Map<Long, Long> getPlayMap() {
            Map<Long, Long> result = new java.util.HashMap<>();
            // 使用scan替代keys，获取所有以PLAY_COUNT_KEY_PREFIX开头的键
            Set<String> keys = redisRepository.scan(PLAY_COUNT_KEY_PREFIX + "*");
            if (keys != null && !keys.isEmpty()) {
                for (String keyStr : keys) {
                    try {
                        // 提取视频ID
                        Long videoId = Long.parseLong(keyStr.substring(PLAY_COUNT_KEY_PREFIX.length()));
                        // 获取计数
                        Object value = redisRepository.get(keyStr);
                        if (value != null) {
                            result.put(videoId, Long.parseLong(value.toString()));
                        }
                    } catch (NumberFormatException e) {
                        // 忽略无法解析为整数的键
                        continue;
                    }
                }
            }
            return result;
        }
        
        @Override
        public long getSize() {
            // 使用scan替代keys
            Set<String> keys = redisRepository.scan(PLAY_COUNT_KEY_PREFIX + "*");
            return keys != null ? keys.size() : 0;
        }
        
        @Override
        public long getPlayCount(Long videoId) {
            Object count = redisRepository.get(PLAY_COUNT_KEY_PREFIX + videoId);
            return count != null ? Long.parseLong(count.toString()) : 0L;
        }
        
        @Override
        public void clean() {
            // 清除Redis中的数据
            // 使用scan替代keys，获取所有计数键
            Set<String> countKeys = redisRepository.scan(PLAY_COUNT_KEY_PREFIX + "*");
            if (countKeys != null && !countKeys.isEmpty()) {
                for (String key : countKeys) {
                    redisRepository.del(key);
                }
            }
            
            // 使用scan替代keys，获取所有IP集合键
            Set<String> ipKeys = redisRepository.scan(PLAY_IP_KEY_PREFIX + "*");
            if (ipKeys != null && !ipKeys.isEmpty()) {
                for (String key : ipKeys) {
                    redisRepository.del(key);
                }
            }
        }
    }
}
