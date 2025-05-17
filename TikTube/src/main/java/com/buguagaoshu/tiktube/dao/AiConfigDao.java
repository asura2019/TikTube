package com.buguagaoshu.tiktube.dao;

import com.buguagaoshu.tiktube.entity.AiConfigEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * AI配置表
 * 
 * @author Pu Zhiwei
 * @email puzhiweipuzhiwei@foxmail.com
 * @date 2023-09-05 15:03:54
 */
@Mapper
public interface AiConfigDao extends BaseMapper<AiConfigEntity> {
    int batchUpdateCount(@Param("col") String col, @Param("countMap") Map<Long, Long> countMap);
}