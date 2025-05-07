package com.buguagaoshu.tiktube.dao;

import com.buguagaoshu.tiktube.entity.OpinionEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 举报以及意见反馈表
 * 
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * @create 2025-05-07
 */
@Mapper
public interface OpinionDao extends BaseMapper<OpinionEntity> {
    /**
     * 根据目标ID查询举报信息
     * 
     * @param targetId 目标ID
     * @return 举报信息列表
     */
    OpinionEntity findByTargetId(Long targetId);
    
    /**
     * 查询未处理的举报信息
     * 
     * @return 未处理的举报信息列表
     */
    OpinionEntity findUnprocessedOpinions();
}
