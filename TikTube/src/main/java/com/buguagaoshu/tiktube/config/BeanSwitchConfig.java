package com.buguagaoshu.tiktube.config;

import com.buguagaoshu.tiktube.repository.CountLimitRepository;
import com.buguagaoshu.tiktube.repository.RedisRepository;
import com.buguagaoshu.tiktube.repository.VerifyCodeRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * @create 2025-05-11
 * 接口实现切换类
 */
@Configuration
public class BeanSwitchConfig {
    
    @Bean
    @Primary
    public CountLimitRepository countLimitRepository(
            @Qualifier("countLimitRedisRepository") CountLimitRepository redisRepository,
            @Qualifier("countLimitRepositoryImpl") CountLimitRepository memoryRepository,
            MyConfigProperties configProperties,
            RedisRepository redisRepositoryService) {
        
        // 根据配置决定使用哪种存储实现
        if (configProperties.getOpenRedis() && redisRepositoryService.isAvailable()) {
            return redisRepository;
        } else {
            return memoryRepository;
        }
    }
    
    @Bean
    @Primary
    public VerifyCodeRepository verifyCodeRepository(
            @Qualifier("verifyCodeRedisRepository") VerifyCodeRepository redisRepository,
            @Qualifier("sessionVerifyCodeRepositoryImpl") VerifyCodeRepository sessionRepository,
            MyConfigProperties configProperties,
            RedisRepository redisRepositoryService) {
        
        // 根据配置决定使用哪种存储实现
        if (configProperties.getOpenRedis() && redisRepositoryService.isAvailable()) {
            return redisRepository;
        } else {
            return sessionRepository;
        }
    }
}