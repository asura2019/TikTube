package com.buguagaoshu.tiktube.repository.impl;

import com.buguagaoshu.tiktube.config.WebConstant;
import com.buguagaoshu.tiktube.repository.APICurrentLimitingRepository;
import com.buguagaoshu.tiktube.repository.RedisRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * @create 2025-05-21
 * 接口限流的Redis实现
 */
@Slf4j
@Repository("currentLimitingRedisRepository")
public class CurrentLimitingRedisRepositoryImpl implements APICurrentLimitingRepository {

    private final RedisRepository redisRepository;
    
    /**
     * 访问时间限制
     * 单位秒
     * 10 秒内只能访问一次
     * */
    public final static int TIME_LIMIT = 10;
    
    // Redis键前缀
    private static final String VISIT_RECORD_KEY_PREFIX = "visit:record:";
    
    public CurrentLimitingRedisRepositoryImpl(RedisRepository redisRepository) {
        this.redisRepository = redisRepository;
    }
    
    @Override
    public boolean hasVisit(int type, long userId) {
        String key = VISIT_RECORD_KEY_PREFIX + userId + "_" + type;
        long currentTime = System.currentTimeMillis() / 1000;
        
        // 获取上次访问时间
        Object lastVisitTimeObj = redisRepository.get(key);
        
        // 如果是首次访问或者已经超过限制时间，则允许访问
        if (lastVisitTimeObj == null) {
            // 更新访问时间并设置过期时间
            redisRepository.set(key, String.valueOf(currentTime), TIME_LIMIT);
            return true;
        } else {
            long lastVisitTime = Long.parseLong(lastVisitTimeObj.toString());
            if ((currentTime - lastVisitTime) >= TIME_LIMIT) {
                // 更新访问时间并设置过期时间
                redisRepository.set(key, String.valueOf(currentTime), TIME_LIMIT);
                return true;
            }
        }
        redisRepository.set(key, String.valueOf(currentTime), TIME_LIMIT);
        return false;
    }
    
    @Override
    public int getRemainingTime(int type, long userId) {
        String key = VISIT_RECORD_KEY_PREFIX + userId + "_" + type;
        long currentTime = System.currentTimeMillis() / 1000;
        
        // 获取上次访问时间
        Object lastVisitTimeObj = redisRepository.get(key);
        
        // 如果是首次访问或者已经超过限制时间，则剩余时间为0
        if (lastVisitTimeObj == null) {
            return 0;
        }
        
        long lastVisitTime = Long.parseLong(lastVisitTimeObj.toString());
        if ((currentTime - lastVisitTime) >= TIME_LIMIT) {
            return 0;
        }
        
        // 计算剩余时间
        return (int) (TIME_LIMIT - (currentTime - lastVisitTime));
    }
    
    @Override
    public void clearUserRecord(long userId) {
        String articleKey = VISIT_RECORD_KEY_PREFIX + userId + "_" + WebConstant.SIMPLE_CURRENT_LIMITING_TYPE_ARTICLE;
        String commentKey = VISIT_RECORD_KEY_PREFIX + userId + "_" + WebConstant.SIMPLE_CURRENT_LIMITING_TYPE_COMMENT;
        String danmakuKey = VISIT_RECORD_KEY_PREFIX + userId + "_" + WebConstant.SIMPLE_CURRENT_LIMITING_TYPE_DANMAKU;
        
        redisRepository.del(articleKey, commentKey, danmakuKey);
    }

    @Override
    public void clearAllRecords() {
        log.info("使用 Redis 存储，无需手动清除！");
    }
}