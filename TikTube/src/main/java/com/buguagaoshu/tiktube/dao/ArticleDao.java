package com.buguagaoshu.tiktube.dao;

import com.buguagaoshu.tiktube.entity.ArticleEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 视频，图片，文章 发帖表

TODO 回复消息可见，加密帖子，视频等
 * 
 * @author Pu Zhiwei
 * @email puzhiweipuzhiwei@foxmail.com
 * @date 2020-09-05 14:38:43
 */
@Mapper
public interface ArticleDao extends BaseMapper<ArticleEntity> {
    void addDanmakuCount(@Param("id") long articleId, @Param("count") Long count);


    void addViewCount(@Param("id") long articleId, @Param("count") Long count);

    void addCount(@Param("col") String col, @Param("id") long articleId, @Param("count") Long count);
    
    /**
     * 批量更新文章计数
     * @param col 要更新的列名
     * @param countMap ID和计数的映射，key是文章ID，value是要增加的数值
     * @return 更新的记录数
     */
    int batchUpdateCount(@Param("col") String col, @Param("countMap") Map<Long, Long> countMap);
    
    /**
     * 查询热门文章，直接在数据库层计算热度值
     * @param startTime 开始时间（24小时内）
     * @param limit 限制数量
     * @return 热门文章列表
     */
    List<ArticleEntity> findHotArticlesWithScore(@Param("startTime") long startTime, @Param("limit") int limit);
    
    /**
     * 查询最新发布的文章并计算热度值
     * @param limit 限制数量
     * @return 按创建时间排序的最新文章列表
     */
    List<ArticleEntity> findLatestArticlesWithScore(@Param("limit") int limit);
    
    /**
     * 根据标签查找相似文章
     * @param tagLikeList 标签模糊匹配列表
     * @param articleId 当前文章ID（排除自身）
     * @param limit 限制数量
     * @return 相似文章列表
     */
    List<ArticleEntity> findSimilarArticlesByTags(@Param("tagLikeList") List<String> tagLikeList, 
                                                 @Param("articleId") Long articleId,
                                                 @Param("limit") int limit);
}
