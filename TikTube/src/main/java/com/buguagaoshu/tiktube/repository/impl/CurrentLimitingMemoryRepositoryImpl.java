package com.buguagaoshu.tiktube.repository.impl;

import com.buguagaoshu.tiktube.config.WebConstant;
import com.buguagaoshu.tiktube.repository.APICurrentLimitingRepository;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * @create 2025-05-21
 * 接口限流的内存实现
 */
@Repository("currentLimitingMemoryRepository")
public class CurrentLimitingMemoryRepositoryImpl implements APICurrentLimitingRepository {

    /**
     * 访问时间限制
     * 单位秒
     * 10 秒内只能访问一次
     * */
    public final static int TIME_LIMIT = 10;
    
    /**
     * 用户访问记录
     * key: 用户ID_类型
     * value: 上次访问时间
     */
    private final Map<String, Long> visitRecords = new ConcurrentHashMap<>();
    
    @Override
    public boolean hasVisit(int type, long userId) {
        String key = userId + "_" + type;
        long currentTime = System.currentTimeMillis() / 1000;
        
        // 获取上次访问时间
        Long lastVisitTime = visitRecords.get(key);
        
        // 如果是首次访问或者已经超过限制时间，则允许访问
        if (lastVisitTime == null || (currentTime - lastVisitTime) >= TIME_LIMIT) {
            // 更新访问时间
            visitRecords.put(key, currentTime);
            return true;
        }
        visitRecords.put(key, currentTime);
        return false;
    }
    
    @Override
    public int getRemainingTime(int type, long userId) {
        String key = userId + "_" + type;
        long currentTime = System.currentTimeMillis() / 1000;
        
        // 获取上次访问时间
        Long lastVisitTime = visitRecords.get(key);
        
        // 如果是首次访问或者已经超过限制时间，则剩余时间为0
        if (lastVisitTime == null || (currentTime - lastVisitTime) >= TIME_LIMIT) {
            return 0;
        }
        
        // 计算剩余时间
        return (int) (TIME_LIMIT - (currentTime - lastVisitTime));
    }
    
    @Override
    public void clearUserRecord(long userId) {
        String articleKey = userId + "_" + WebConstant.SIMPLE_CURRENT_LIMITING_TYPE_ARTICLE;
        String commentKey = userId + "_" + WebConstant.SIMPLE_CURRENT_LIMITING_TYPE_COMMENT;
        String danmakuKey = userId + "_" + WebConstant.SIMPLE_CURRENT_LIMITING_TYPE_DANMAKU;
        
        visitRecords.remove(articleKey);
        visitRecords.remove(commentKey);
        visitRecords.remove(danmakuKey);
    }

    @Override
    public void clearAllRecords() {
        visitRecords.clear();
    }
}