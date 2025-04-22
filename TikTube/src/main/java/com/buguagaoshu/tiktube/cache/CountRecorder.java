package com.buguagaoshu.tiktube.cache;

import com.buguagaoshu.tiktube.entity.ArticleEntity;
import com.buguagaoshu.tiktube.entity.CommentEntity;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * @create 2025-04-22
 * 缓存计数器
 * TODO 后期可以修改为 redis 等其它缓存数据库
 */
@Getter
@Component
public class CountRecorder {
    private final ConcurrentMap<Long, Long> articleCommentCount;

    private final ConcurrentMap<Long, Long> articleLikeCount;       // 稿件点赞
    private final ConcurrentMap<Long, Long> articleFavoriteCount;   // 稿件收藏
    private final ConcurrentMap<Long, Long> articleDislikeCount;    // 稿件点踩


    private final ConcurrentMap<Long, Long> commentLikeCount;       // 评论点赞
    private final ConcurrentMap<Long, Long> commentDislikeCount;    // 评论点踩

    private final ConcurrentMap<Long, Long> commentCount;

    private final ConcurrentMap<Long, Long> danmakuCount;

    public CountRecorder() {
        this.articleCommentCount = new ConcurrentHashMap<>();
        this.commentCount = new ConcurrentHashMap<>();
        this.danmakuCount = new ConcurrentHashMap<>();
        this.articleLikeCount = new ConcurrentHashMap<>();
        this.articleFavoriteCount = new ConcurrentHashMap<>();
        this.articleDislikeCount = new ConcurrentHashMap<>();
        this.commentLikeCount = new ConcurrentHashMap<>();
        this.commentDislikeCount = new ConcurrentHashMap<>();
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
        articleCommentCount.clear();
        articleLikeCount.clear();
        articleFavoriteCount.clear();
        articleDislikeCount.clear();
        commentLikeCount.clear();
        commentDislikeCount.clear();
        commentCount.clear();
        danmakuCount.clear();
    }

    public void recordArticleComment(long articleId, long count) {
        articleCommentCount.compute(articleId, (k, v) -> v == null ? count : v + count);
    }

    public long getArticleCommentCount(long articleId) {
        return articleCommentCount.getOrDefault(articleId, 0L);
    }

    public void recordComment(long commentId, Long count) {
        commentCount.compute(commentId, (k, v) -> (v == null) ? count : v + count);
    }


    public long getCommentCount(long commentId) {
        return commentCount.getOrDefault(commentId, 0L);
    }

    public void recordDanmaku(long articleId, Long count) {
        danmakuCount.compute(articleId, (k, v) -> (v == null) ? count : v + count);
    }

    public long getDanmakuCount(long articleId) {
        return danmakuCount.getOrDefault(articleId, 0L);
    }

    // 新增方法：记录稿件点赞
    public void recordArticleLike(long articleId, long count) {
        articleLikeCount.compute(articleId, (k, v) -> v == null ? count : v + count);
    }

    public long getArticleLikeCount(long articleId) {
        return articleLikeCount.getOrDefault(articleId, 0L);
    }

    // 新增方法：记录稿件收藏
    public void recordArticleFavorite(long articleId, long count) {
        articleFavoriteCount.compute(articleId, (k, v) -> v == null ? count : v + count);
    }

    public long getArticleFavoriteCount(long articleId) {
        return articleFavoriteCount.getOrDefault(articleId, 0L);
    }

    // 新增方法：记录稿件点踩
    public void recordArticleDislike(long articleId, long count) {
        articleDislikeCount.compute(articleId, (k, v) -> v == null ? count : v + count);
    }

    public long getArticleDislikeCount(long articleId) {
        return articleDislikeCount.getOrDefault(articleId, 0L);
    }

    // 新增方法：记录评论点赞
    public void recordCommentLike(long commentId, long count) {
        commentLikeCount.compute(commentId, (k, v) -> v == null ? count : v + count);
    }

    public long getCommentLikeCount(long commentId) {
        return commentLikeCount.getOrDefault(commentId, 0L);
    }

    // 新增方法：记录评论点踩
    public void recordCommentDislike(long commentId, long count) {
        commentDislikeCount.compute(commentId, (k, v) -> v == null ? count : v + count);
    }

    public long getCommentDislikeCount(long commentId) {
        return commentDislikeCount.getOrDefault(commentId, 0L);
    }
}
