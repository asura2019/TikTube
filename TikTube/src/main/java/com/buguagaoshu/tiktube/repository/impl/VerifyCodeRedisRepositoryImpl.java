package com.buguagaoshu.tiktube.repository.impl;

import com.buguagaoshu.tiktube.repository.RedisRepository;
import com.buguagaoshu.tiktube.repository.VerifyCodeRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * @create 2025-05-15
 * 验证码的Redis存储实现
 */
@Repository("verifyCodeRedisRepository")
public class VerifyCodeRedisRepositoryImpl implements VerifyCodeRepository {

    private static final String VERIFY_CODE_KEY_PREFIX = "verify:code:";
    
    private final RedisRepository redisRepository;

    public VerifyCodeRedisRepositoryImpl(RedisRepository redisRepository) {
        this.redisRepository = redisRepository;
    }

    @Override
    public void save(String key, String code) {
        // 默认10分钟过期
        save(key, code, 10L);
    }

    @Override
    public void save(String key, String code, Long time) {
        String redisKey = VERIFY_CODE_KEY_PREFIX + key;
        // 存储验证码，设置过期时间（分钟）
        redisRepository.set(redisKey, code, time * 60);
    }

    @Override
    public String find(String key) {
        String redisKey = VERIFY_CODE_KEY_PREFIX + key;
        Object value = redisRepository.get(redisKey);
        return value != null ? value.toString() : null;
    }

    @Override
    public void remove(String key) {
        String redisKey = VERIFY_CODE_KEY_PREFIX + key;
        redisRepository.del(redisKey);
    }
}