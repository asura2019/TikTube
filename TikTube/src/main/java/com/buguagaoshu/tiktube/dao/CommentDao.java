package com.buguagaoshu.tiktube.dao;

import com.buguagaoshu.tiktube.entity.CommentEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * 
 * 
 * @author Pu Zhiwei
 * @email puzhiweipuzhiwei@foxmail.com
 * @date 2020-09-05 14:38:43
 */
@Mapper
public interface CommentDao extends BaseMapper<CommentEntity> {
    void addCount(@Param("col") String col, @Param("id") long commentId, @Param("count") Long count);
    
    /**
     * 批量更新评论计数
     * @param col 要更新的列名
     * @param countMap ID和计数的映射，key是评论ID，value是要增加的数值
     * @return 更新的记录数
     */
    int batchUpdateCount(@Param("col") String col, @Param("countMap") Map<Long, Long> countMap);
}
