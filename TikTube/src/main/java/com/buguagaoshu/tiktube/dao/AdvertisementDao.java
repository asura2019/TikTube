package com.buguagaoshu.tiktube.dao;

import com.buguagaoshu.tiktube.entity.AdvertisementEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 广告以及系统公告
 * 
 * @author Pu Zhiwei
 * @email puzhiweipuzhiwei@foxmail.com
 * @date 2020-09-05 14:38:43
 */
@Mapper
public interface AdvertisementDao extends BaseMapper<AdvertisementEntity> {
	
}