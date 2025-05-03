package com.buguagaoshu.tiktube.dao;

import com.buguagaoshu.tiktube.entity.WebConfigEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 配置设置表
 * 
 * @author Pu Zhiwei
 * @email puzhiweipuzhiwei@foxmail.com
 * @date 2020-09-05 15:03:54
 */
@Mapper
public interface WebConfigDao extends BaseMapper<WebConfigEntity> {
    /**
     * 获取最新的配置
     * 
     * @return 最新配置
     */
    WebConfigEntity findNewConfig();
}