package com.buguagaoshu.tiktube.cache;

import com.buguagaoshu.tiktube.entity.AiConfigEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * @create 2025-05-17
 */
public class AIConfigCache {
    public static Map<Long, AiConfigEntity> aiConfigCache = new HashMap<>();


    public static Map<Long, List<AiConfigEntity>> typeAICache = new HashMap<>();


    public static AiConfigEntity getTypeConfig(long type) {
        List<AiConfigEntity> list = typeAICache.get(type);
        if (list == null) {
            return null;
        }
        //int max = list.size();
        //int min = 0;
        //Random random = new Random();
        //int randomNum = random.nextInt(max - min) + min;
        return list.get(0);
    }
}
