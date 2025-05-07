package com.buguagaoshu.tiktube.dao;

import com.buguagaoshu.tiktube.entity.AdvertisementEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * 广告以及系统公告
 *
 * @author Pu Zhiwei
 * @email puzhiweipuzhiwei@foxmail.com
 * @date 2020-09-05 14:38:43
 */
@Mapper
public interface AdvertisementDao extends BaseMapper<AdvertisementEntity> {
    /**
     * 批量更新广告浏览量
     *
     * @param countMap key为广告ID，value为需要增加的浏览量
     * @return 更新的记录数
     */
    int batchUpdateCount(@Param("col") String col, @Param("countMap") Map<Integer, Long> countMap);

}