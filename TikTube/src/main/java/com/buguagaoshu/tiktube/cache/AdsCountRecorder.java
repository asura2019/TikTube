package com.buguagaoshu.tiktube.cache;

import com.buguagaoshu.tiktube.config.MyConfigProperties;
import com.buguagaoshu.tiktube.repository.RedisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * @create 2025-05-03
 */
@Component
public class AdsCountRecorder {
    
    // Redis中存储的键名前缀
    private static final String ADS_COUNT_KEY_PREFIX = "ads:count:";
    private static final String ADS_IP_KEY_PREFIX = "ads:ip:";
    
    private final AdsStorage adsStorage;

    @Autowired
    public AdsCountRecorder(RedisRepository redisRepository, MyConfigProperties configProperties) {
        // 根据配置决定使用哪种存储实现
        if (configProperties.getOpenRedis() && redisRepository.isAvailable()) {
            this.adsStorage = new RedisAdsStorage(redisRepository);
        } else {
            this.adsStorage = new MemoryAdsStorage();
        }
    }

    public void recordAds(Integer adsId, String ipAddress) {
        adsStorage.recordAds(adsId, ipAddress);
    }

    public Map<Integer, Long> getAdsMap() {
        return adsStorage.getAdsMap();
    }

    public long getSize() {
        return adsStorage.getSize();
    }

    public long getAdsCount(Integer adsId) {
        return adsStorage.getAdsCount(adsId);
    }

    public void clean() {
        adsStorage.clean();
    }
    
    // 广告存储接口
    private interface AdsStorage {
        void recordAds(Integer adsId, String ipAddress);
        Map<Integer, Long> getAdsMap();
        long getSize();
        long getAdsCount(Integer adsId);
        void clean();
    }
    
    // 内存存储实现
    private static class MemoryAdsStorage implements AdsStorage {
        private final Map<Integer, Long> adsCounts;
        private final Map<Integer, Set<String>> recordedIPs;
        
        public MemoryAdsStorage() {
            this.adsCounts = new java.util.concurrent.ConcurrentHashMap<>();
            this.recordedIPs = new java.util.concurrent.ConcurrentHashMap<>();
        }
        
        @Override
        public void recordAds(Integer adsId, String ipAddress) {
            Set<String> ipSet = recordedIPs.computeIfAbsent(adsId, k ->
                    java.util.Collections.newSetFromMap(new java.util.concurrent.ConcurrentHashMap<>()));

            // 使用 putIfAbsent 原子操作检查 IP 是否已经存在
            if (ipSet.add(ipAddress)) {
                // 原子性地增加播放量
                adsCounts.compute(adsId, (k, v) -> (v == null) ? 1 : v + 1);
            }
        }
        
        @Override
        public Map<Integer, Long> getAdsMap() {
            return adsCounts;
        }
        
        @Override
        public long getSize() {
            return adsCounts.size();
        }
        
        @Override
        public long getAdsCount(Integer adsId) {
            return adsCounts.getOrDefault(adsId, 0L);
        }
        
        @Override
        public void clean() {
            adsCounts.clear();
            recordedIPs.clear();
        }
    }
    
    // Redis存储实现
    private static class RedisAdsStorage implements AdsStorage {
        private final RedisRepository redisRepository;
        
        public RedisAdsStorage(RedisRepository redisRepository) {
            this.redisRepository = redisRepository;
        }
        
        @Override
        public void recordAds(Integer adsId, String ipAddress) {
            String ipKey = ADS_IP_KEY_PREFIX + adsId;
            // 尝试将IP添加到集合中，如果添加成功（之前不存在）则返回1
            if (redisRepository.sSet(ipKey, ipAddress) > 0) {
                // 增加计数
                redisRepository.incr(ADS_COUNT_KEY_PREFIX + adsId, 1);
            }
        }
        
        @Override
        public Map<Integer, Long> getAdsMap() {
            Map<Integer, Long> result = new java.util.HashMap<>();
            // 使用scan替代keys，获取所有以ADS_COUNT_KEY_PREFIX开头的键
            Set<String> keys = redisRepository.scan(ADS_COUNT_KEY_PREFIX + "*");
            if (keys != null && !keys.isEmpty()) {
                for (String keyStr : keys) {
                    try {
                        // 提取广告ID
                        Integer adsId = Integer.parseInt(keyStr.substring(ADS_COUNT_KEY_PREFIX.length()));
                        // 获取计数
                        Object value = redisRepository.get(keyStr);
                        if (value != null) {
                            result.put(adsId, Long.parseLong(value.toString()));
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
            Set<String> keys = redisRepository.scan(ADS_COUNT_KEY_PREFIX + "*");
            return keys != null ? keys.size() : 0;
        }
        
        @Override
        public long getAdsCount(Integer adsId) {
            Object count = redisRepository.get(ADS_COUNT_KEY_PREFIX + adsId);
            return count != null ? Long.parseLong(count.toString()) : 0L;
        }
        
        @Override
        public void clean() {
            // 清除Redis中的数据
            // 使用scan替代keys，获取所有计数键
            Set<String> countKeys = redisRepository.scan(ADS_COUNT_KEY_PREFIX + "*");
            if (countKeys != null && !countKeys.isEmpty()) {
                for (String key : countKeys) {
                    redisRepository.del(key);
                }
            }
            
            // 使用scan替代keys，获取所有IP集合键
            Set<String> ipKeys = redisRepository.scan(ADS_IP_KEY_PREFIX + "*");
            if (ipKeys != null && !ipKeys.isEmpty()) {
                for (String key : ipKeys) {
                    redisRepository.del(key);
                }
            }
        }
    }
}
