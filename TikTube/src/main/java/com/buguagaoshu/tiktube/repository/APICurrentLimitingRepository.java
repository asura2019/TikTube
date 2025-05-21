package com.buguagaoshu.tiktube.repository;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * @create 2025-05-21
 * 接口限流仓库接口
 */
public interface APICurrentLimitingRepository {
    
    /**
     * 检查是否有接口访问权限
     * @param type 接口类型
     * @param userId 用户ID
     * @return 是否有访问权限
     */
    boolean hasVisit(int type, long userId);
    
    /**
     * 获取剩余等待时间
     * @param type 接口类型
     * @param userId 用户ID
     * @return 剩余等待时间（秒）
     */
    int getRemainingTime(int type, long userId);
    
    /**
     * 清除用户访问记录
     * @param userId 用户ID
     */
    void clearUserRecord(long userId);

    /**
     * 清除所有用户访问记录
     * */
    void clearAllRecords();
}