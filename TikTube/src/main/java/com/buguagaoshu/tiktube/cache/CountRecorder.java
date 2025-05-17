package com.buguagaoshu.tiktube.cache;

import com.buguagaoshu.tiktube.config.MyConfigProperties;
import com.buguagaoshu.tiktube.entity.ArticleEntity;
import com.buguagaoshu.tiktube.entity.CommentEntity;
import com.buguagaoshu.tiktube.repository.RedisRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * @create 2025-04-22
 * 缓存计数器
 */
@Component
public class CountRecorder {
    // Redis中存储的键名前缀
    private static final String ARTICLE_COMMENT_COUNT_KEY_PREFIX = "count:article:comment:";
    private static final String ARTICLE_LIKE_COUNT_KEY_PREFIX = "count:article:like:";
    private static final String ARTICLE_FAVORITE_COUNT_KEY_PREFIX = "count:article:favorite:";
    private static final String ARTICLE_DISLIKE_COUNT_KEY_PREFIX = "count:article:dislike:";
    private static final String COMMENT_LIKE_COUNT_KEY_PREFIX = "count:comment:like:";
    private static final String COMMENT_DISLIKE_COUNT_KEY_PREFIX = "count:comment:dislike:";
    private static final String COMMENT_COUNT_KEY_PREFIX = "count:comment:count:";
    private static final String DANMAKU_COUNT_KEY_PREFIX = "count:danmaku:";
    private static final String AI_MODEL_TOKEN_COUNT_KEY_PREFIX = "count:ai:token:";
    
    private final CountStorage countStorage;

    @Autowired
    public CountRecorder(RedisRepository redisRepository, MyConfigProperties configProperties) {
        // 根据配置决定使用哪种存储实现
        if (configProperties.getOpenRedis() && redisRepository.isAvailable()) {
            this.countStorage = new RedisCountStorage(redisRepository);
        } else {
            this.countStorage = new MemoryCountStorage();
        }
    }

    /**
     * 稿件数据同步
     * */
    public void syncArticleCount(ArticleEntity articleEntity) {
        articleEntity.setCommentCount(articleEntity.getCommentCount() + getArticleCommentCount(articleEntity.getId()));
        articleEntity.setLikeCount(articleEntity.getLikeCount() + getArticleLikeCount(articleEntity.getId()));
        articleEntity.setFavoriteCount(articleEntity.getFavoriteCount() + getArticleFavoriteCount(articleEntity.getId()));
        articleEntity.setDislikeCount(articleEntity.getDislikeCount() + getArticleDislikeCount(articleEntity.getId()));
        articleEntity.setDanmakuCount(articleEntity.getDanmakuCount() + getDanmakuCount(articleEntity.getId()));
    }

    public void syncCommentCount(CommentEntity comment) {
        comment.setCommentCount(comment.getCommentCount() + getCommentCount(comment.getId()));
        comment.setLikeCount(comment.getLikeCount() + getCommentLikeCount(comment.getId()));
        comment.setDislikeCount(comment.getDislikeCount() + getCommentDislikeCount(comment.getId()));
    }

    public void clear() {
        countStorage.clear();
    }

    // 文章评论计数相关方法
    public void recordArticleComment(long articleId, long count) {
        countStorage.recordArticleComment(articleId, count);
    }

    public long getArticleCommentCount(long articleId) {
        return countStorage.getArticleCommentCount(articleId);
    }

    public Map<Long, Long> getArticleCommentCountMap() {
        return countStorage.getArticleCommentCountMap();
    }

    // 评论计数相关方法
    public void recordComment(long commentId, Long count) {
        countStorage.recordComment(commentId, count);
    }

    public long getCommentCount(long commentId) {
        return countStorage.getCommentCount(commentId);
    }

    public Map<Long, Long> getCommentCountMap() {
        return countStorage.getCommentCountMap();
    }

    // 弹幕计数相关方法
    public void recordDanmaku(long articleId, Long count) {
        countStorage.recordDanmaku(articleId, count);
    }

    public long getDanmakuCount(long articleId) {
        return countStorage.getDanmakuCount(articleId);
    }

    public Map<Long, Long> getDanmakuCountMap() {
        return countStorage.getDanmakuCountMap();
    }

    // 文章点赞计数相关方法
    public void recordArticleLike(long articleId, long count) {
        countStorage.recordArticleLike(articleId, count);
    }

    public long getArticleLikeCount(long articleId) {
        return countStorage.getArticleLikeCount(articleId);
    }

    public Map<Long, Long> getArticleLikeCountMap() {
        return countStorage.getArticleLikeCountMap();
    }

    // 文章收藏计数相关方法
    public void recordArticleFavorite(long articleId, long count) {
        countStorage.recordArticleFavorite(articleId, count);
    }

    public long getArticleFavoriteCount(long articleId) {
        return countStorage.getArticleFavoriteCount(articleId);
    }

    public Map<Long, Long> getArticleFavoriteCountMap() {
        return countStorage.getArticleFavoriteCountMap();
    }

    // 文章点踩计数相关方法
    public void recordArticleDislike(long articleId, long count) {
        countStorage.recordArticleDislike(articleId, count);
    }

    public long getArticleDislikeCount(long articleId) {
        return countStorage.getArticleDislikeCount(articleId);
    }

    public Map<Long, Long> getArticleDislikeCountMap() {
        return countStorage.getArticleDislikeCountMap();
    }

    // 评论点赞计数相关方法
    public void recordCommentLike(long commentId, long count) {
        countStorage.recordCommentLike(commentId, count);
    }

    public long getCommentLikeCount(long commentId) {
        return countStorage.getCommentLikeCount(commentId);
    }

    public Map<Long, Long> getCommentLikeCountMap() {
        return countStorage.getCommentLikeCountMap();
    }

    // 评论点踩计数相关方法
    public void recordCommentDislike(long commentId, long count) {
        countStorage.recordCommentDislike(commentId, count);
    }

    public long getCommentDislikeCount(long commentId) {
        return countStorage.getCommentDislikeCount(commentId);
    }

    public Map<Long, Long> getCommentDislikeCountMap() {
        return countStorage.getCommentDislikeCountMap();
    }

    public void recordAiModelToken(Long modelId, long count) {
        countStorage.recordAiModelToken(modelId, count);
    }

    public long getAiModelTokenCount(Long modelId) {
        return countStorage.getAiModelTokenCount(modelId);
    }

    public Map<Long, Long> getAiModelTokenCountMap() {
        return countStorage.getAiModelTokenCountMap();
    }
    
    // 计数存储接口
    private interface CountStorage {
        // 清除所有计数
        void clear();
        
        // 文章评论计数
        void recordArticleComment(long articleId, long count);
        long getArticleCommentCount(long articleId);
        Map<Long, Long> getArticleCommentCountMap();
        
        // 评论计数
        void recordComment(long commentId, long count);
        long getCommentCount(long commentId);
        Map<Long, Long> getCommentCountMap();
        
        // 弹幕计数
        void recordDanmaku(long articleId, long count);
        long getDanmakuCount(long articleId);
        Map<Long, Long> getDanmakuCountMap();
        
        // 文章点赞计数
        void recordArticleLike(long articleId, long count);
        long getArticleLikeCount(long articleId);
        Map<Long, Long> getArticleLikeCountMap();
        
        // 文章收藏计数
        void recordArticleFavorite(long articleId, long count);
        long getArticleFavoriteCount(long articleId);
        Map<Long, Long> getArticleFavoriteCountMap();
        
        // 文章点踩计数
        void recordArticleDislike(long articleId, long count);
        long getArticleDislikeCount(long articleId);
        Map<Long, Long> getArticleDislikeCountMap();
        
        // 评论点赞计数
        void recordCommentLike(long commentId, long count);
        long getCommentLikeCount(long commentId);
        Map<Long, Long> getCommentLikeCountMap();
        
        // 评论点踩计数
        void recordCommentDislike(long commentId, long count);
        long getCommentDislikeCount(long commentId);
        Map<Long, Long> getCommentDislikeCountMap();
        
        // AI大模型token计数
        void recordAiModelToken(Long modelId, long count);
        long getAiModelTokenCount(Long modelId);
        Map<Long, Long> getAiModelTokenCountMap();
    }
    
    // 内存存储实现
    private static class MemoryCountStorage implements CountStorage {
        private final ConcurrentMap<Long, Long> articleCommentCount = new ConcurrentHashMap<>();
        private final ConcurrentMap<Long, Long> articleLikeCount = new ConcurrentHashMap<>();
        private final ConcurrentMap<Long, Long> articleFavoriteCount = new ConcurrentHashMap<>();
        private final ConcurrentMap<Long, Long> articleDislikeCount = new ConcurrentHashMap<>();
        private final ConcurrentMap<Long, Long> commentLikeCount = new ConcurrentHashMap<>();
        private final ConcurrentMap<Long, Long> commentDislikeCount = new ConcurrentHashMap<>();
        private final ConcurrentMap<Long, Long> commentCount = new ConcurrentHashMap<>();
        private final ConcurrentMap<Long, Long> danmakuCount = new ConcurrentHashMap<>();

        private final ConcurrentMap<Long, Long> aiModelTokenCount = new ConcurrentHashMap<>();
        
        @Override
        public void clear() {
            articleCommentCount.clear();
            articleLikeCount.clear();
            articleFavoriteCount.clear();
            articleDislikeCount.clear();
            commentLikeCount.clear();
            commentDislikeCount.clear();
            commentCount.clear();
            danmakuCount.clear();
            aiModelTokenCount.clear();
        }
        
        // 文章评论计数
        @Override
        public void recordArticleComment(long articleId, long count) {
            articleCommentCount.compute(articleId, (k, v) -> v == null ? count : v + count);
        }
        
        @Override
        public long getArticleCommentCount(long articleId) {
            return articleCommentCount.getOrDefault(articleId, 0L);
        }
        
        @Override
        public Map<Long, Long> getArticleCommentCountMap() {
            return articleCommentCount;
        }
        
        // 评论计数
        @Override
        public void recordComment(long commentId, long count) {
            commentCount.compute(commentId, (k, v) -> v == null ? count : v + count);
        }
        
        @Override
        public long getCommentCount(long commentId) {
            return commentCount.getOrDefault(commentId, 0L);
        }
        
        @Override
        public Map<Long, Long> getCommentCountMap() {
            return commentCount;
        }
        
        // 弹幕计数
        @Override
        public void recordDanmaku(long articleId, long count) {
            danmakuCount.compute(articleId, (k, v) -> v == null ? count : v + count);
        }
        
        @Override
        public long getDanmakuCount(long articleId) {
            return danmakuCount.getOrDefault(articleId, 0L);
        }
        
        @Override
        public Map<Long, Long> getDanmakuCountMap() {
            return danmakuCount;
        }
        
        // 文章点赞计数
        @Override
        public void recordArticleLike(long articleId, long count) {
            articleLikeCount.compute(articleId, (k, v) -> v == null ? count : v + count);
        }
        
        @Override
        public long getArticleLikeCount(long articleId) {
            return articleLikeCount.getOrDefault(articleId, 0L);
        }
        
        @Override
        public Map<Long, Long> getArticleLikeCountMap() {
            return articleLikeCount;
        }
        
        // 文章收藏计数
        @Override
        public void recordArticleFavorite(long articleId, long count) {
            articleFavoriteCount.compute(articleId, (k, v) -> v == null ? count : v + count);
        }
        
        @Override
        public long getArticleFavoriteCount(long articleId) {
            return articleFavoriteCount.getOrDefault(articleId, 0L);
        }
        
        @Override
        public Map<Long, Long> getArticleFavoriteCountMap() {
            return articleFavoriteCount;
        }
        
        // 文章点踩计数
        @Override
        public void recordArticleDislike(long articleId, long count) {
            articleDislikeCount.compute(articleId, (k, v) -> v == null ? count : v + count);
        }
        
        @Override
        public long getArticleDislikeCount(long articleId) {
            return articleDislikeCount.getOrDefault(articleId, 0L);
        }
        
        @Override
        public Map<Long, Long> getArticleDislikeCountMap() {
            return articleDislikeCount;
        }
        
        // 评论点赞计数
        @Override
        public void recordCommentLike(long commentId, long count) {
            commentLikeCount.compute(commentId, (k, v) -> v == null ? count : v + count);
        }
        
        @Override
        public long getCommentLikeCount(long commentId) {
            return commentLikeCount.getOrDefault(commentId, 0L);
        }
        
        @Override
        public Map<Long, Long> getCommentLikeCountMap() {
            return commentLikeCount;
        }
        
        // 评论点踩计数
        @Override
        public void recordCommentDislike(long commentId, long count) {
            commentDislikeCount.compute(commentId, (k, v) -> v == null ? count : v + count);
        }
        
        @Override
        public long getCommentDislikeCount(long commentId) {
            return commentDislikeCount.getOrDefault(commentId, 0L);
        }
        
        @Override
        public Map<Long, Long> getCommentDislikeCountMap() {
            return commentDislikeCount;
        }

        // AI大模型token计数
        @Override
        public void recordAiModelToken(Long modelId, long count) {
            aiModelTokenCount.compute(modelId, (k, v) -> v == null ? count : v + count);
        }

        @Override
        public long getAiModelTokenCount(Long modelId) {
            return aiModelTokenCount.getOrDefault(modelId, 0L);
        }

        @Override
        public Map<Long, Long> getAiModelTokenCountMap() {
            return aiModelTokenCount;
        }
    }
    
    // Redis存储实现
    private static class RedisCountStorage implements CountStorage {
        private final RedisRepository redisRepository;
        
        public RedisCountStorage(RedisRepository redisRepository) {
            this.redisRepository = redisRepository;
        }
        
        @Override
        public void clear() {
            // 清除所有计数数据
            clearKeysByPrefix(ARTICLE_COMMENT_COUNT_KEY_PREFIX);
            clearKeysByPrefix(ARTICLE_LIKE_COUNT_KEY_PREFIX);
            clearKeysByPrefix(ARTICLE_FAVORITE_COUNT_KEY_PREFIX);
            clearKeysByPrefix(ARTICLE_DISLIKE_COUNT_KEY_PREFIX);
            clearKeysByPrefix(COMMENT_LIKE_COUNT_KEY_PREFIX);
            clearKeysByPrefix(COMMENT_DISLIKE_COUNT_KEY_PREFIX);
            clearKeysByPrefix(COMMENT_COUNT_KEY_PREFIX);
            clearKeysByPrefix(DANMAKU_COUNT_KEY_PREFIX);
            clearKeysByPrefix(AI_MODEL_TOKEN_COUNT_KEY_PREFIX);
        }
        
        private void clearKeysByPrefix(String prefix) {
            Set<String> keys = redisRepository.scan(prefix + "*");
            if (keys != null && !keys.isEmpty()) {
                for (String key : keys) {
                    redisRepository.del(key);
                }
            }
        }
        
        // 文章评论计数
        @Override
        public void recordArticleComment(long articleId, long count) {
            redisRepository.incr(ARTICLE_COMMENT_COUNT_KEY_PREFIX + articleId, count);
        }
        
        @Override
        public long getArticleCommentCount(long articleId) {
            Object count = redisRepository.get(ARTICLE_COMMENT_COUNT_KEY_PREFIX + articleId);
            return count != null ? Long.parseLong(count.toString()) : 0L;
        }
        
        @Override
        public Map<Long, Long> getArticleCommentCountMap() {
            return getCountMapByPrefix(ARTICLE_COMMENT_COUNT_KEY_PREFIX);
        }
        
        // 评论计数
        @Override
        public void recordComment(long commentId, long count) {
            redisRepository.incr(COMMENT_COUNT_KEY_PREFIX + commentId, count);
        }
        
        @Override
        public long getCommentCount(long commentId) {
            Object count = redisRepository.get(COMMENT_COUNT_KEY_PREFIX + commentId);
            return count != null ? Long.parseLong(count.toString()) : 0L;
        }
        
        @Override
        public Map<Long, Long> getCommentCountMap() {
            return getCountMapByPrefix(COMMENT_COUNT_KEY_PREFIX);
        }
        
        // 弹幕计数
        @Override
        public void recordDanmaku(long articleId, long count) {
            redisRepository.incr(DANMAKU_COUNT_KEY_PREFIX + articleId, count);
        }
        
        @Override
        public long getDanmakuCount(long articleId) {
            Object count = redisRepository.get(DANMAKU_COUNT_KEY_PREFIX + articleId);
            return count != null ? Long.parseLong(count.toString()) : 0L;
        }
        
        @Override
        public Map<Long, Long> getDanmakuCountMap() {
            return getCountMapByPrefix(DANMAKU_COUNT_KEY_PREFIX);
        }
        
        // 文章点赞计数
        @Override
        public void recordArticleLike(long articleId, long count) {
            redisRepository.incr(ARTICLE_LIKE_COUNT_KEY_PREFIX + articleId, count);
        }
        
        @Override
        public long getArticleLikeCount(long articleId) {
            Object count = redisRepository.get(ARTICLE_LIKE_COUNT_KEY_PREFIX + articleId);
            return count != null ? Long.parseLong(count.toString()) : 0L;
        }
        
        @Override
        public Map<Long, Long> getArticleLikeCountMap() {
            return getCountMapByPrefix(ARTICLE_LIKE_COUNT_KEY_PREFIX);
        }
        
        // 文章收藏计数
        @Override
        public void recordArticleFavorite(long articleId, long count) {
            redisRepository.incr(ARTICLE_FAVORITE_COUNT_KEY_PREFIX + articleId, count);
        }
        
        @Override
        public long getArticleFavoriteCount(long articleId) {
            Object count = redisRepository.get(ARTICLE_FAVORITE_COUNT_KEY_PREFIX + articleId);
            return count != null ? Long.parseLong(count.toString()) : 0L;
        }
        
        @Override
        public Map<Long, Long> getArticleFavoriteCountMap() {
            return getCountMapByPrefix(ARTICLE_FAVORITE_COUNT_KEY_PREFIX);
        }
        
        // 文章点踩计数
        @Override
        public void recordArticleDislike(long articleId, long count) {
            redisRepository.incr(ARTICLE_DISLIKE_COUNT_KEY_PREFIX + articleId, count);
        }
        
        @Override
        public long getArticleDislikeCount(long articleId) {
            Object count = redisRepository.get(ARTICLE_DISLIKE_COUNT_KEY_PREFIX + articleId);
            return count != null ? Long.parseLong(count.toString()) : 0L;
        }
        
        @Override
        public Map<Long, Long> getArticleDislikeCountMap() {
            return getCountMapByPrefix(ARTICLE_DISLIKE_COUNT_KEY_PREFIX);
        }
        
        // 评论点赞计数
        @Override
        public void recordCommentLike(long commentId, long count) {
            redisRepository.incr(COMMENT_LIKE_COUNT_KEY_PREFIX + commentId, count);
        }
        
        @Override
        public long getCommentLikeCount(long commentId) {
            Object count = redisRepository.get(COMMENT_LIKE_COUNT_KEY_PREFIX + commentId);
            return count != null ? Long.parseLong(count.toString()) : 0L;
        }
        
        @Override
        public Map<Long, Long> getCommentLikeCountMap() {
            return getCountMapByPrefix(COMMENT_LIKE_COUNT_KEY_PREFIX);
        }
        
        // 评论点踩计数
        @Override
        public void recordCommentDislike(long commentId, long count) {
            redisRepository.incr(COMMENT_DISLIKE_COUNT_KEY_PREFIX + commentId, count);
        }
        
        @Override
        public long getCommentDislikeCount(long commentId) {
            Object count = redisRepository.get(COMMENT_DISLIKE_COUNT_KEY_PREFIX + commentId);
            return count != null ? Long.parseLong(count.toString()) : 0L;
        }
        
        @Override
        public Map<Long, Long> getCommentDislikeCountMap() {
            return getCountMapByPrefix(COMMENT_DISLIKE_COUNT_KEY_PREFIX);
        }


        @Override
        public void recordAiModelToken(Long modelId, long count) {
            redisRepository.incr(AI_MODEL_TOKEN_COUNT_KEY_PREFIX + modelId, count);
        }

        @Override
        public long getAiModelTokenCount(Long modelId) {
            Object count = redisRepository.get(AI_MODEL_TOKEN_COUNT_KEY_PREFIX + modelId);
            return count != null ? Long.parseLong(count.toString()) : 0L;
        }

        @Override
        public Map<Long, Long> getAiModelTokenCountMap() {
            return getCountMapByPrefix(AI_MODEL_TOKEN_COUNT_KEY_PREFIX);
        }
        
        // 通用方法：根据前缀获取计数映射
        private Map<Long, Long> getCountMapByPrefix(String prefix) {
            Map<Long, Long> result = new HashMap<>();
            Set<String> keys = redisRepository.scan(prefix + "*");
            if (keys != null && !keys.isEmpty()) {
                for (String keyStr : keys) {
                    try {
                        // 提取ID
                        Long id = Long.parseLong(keyStr.substring(prefix.length()));
                        // 获取计数
                        Object value = redisRepository.get(keyStr);
                        if (value != null) {
                            result.put(id, Long.parseLong(value.toString()));
                        }
                    } catch (NumberFormatException e) {
                        // 忽略无法解析为整数的键
                        continue;
                    }
                }
            }
            return result;
        }
    }
}
