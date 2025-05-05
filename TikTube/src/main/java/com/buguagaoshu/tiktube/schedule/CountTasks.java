package com.buguagaoshu.tiktube.schedule;

import com.buguagaoshu.tiktube.cache.CountRecorder;
import com.buguagaoshu.tiktube.cache.MailCountLimit;
import com.buguagaoshu.tiktube.cache.PlayCountRecorder;
import com.buguagaoshu.tiktube.dao.ArticleDao;
import com.buguagaoshu.tiktube.dao.CommentDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * @create 2025-04-22
 * 定期向数据库同步数据
 */
@Component
@Slf4j
public class CountTasks {
    private final MailCountLimit mailCountLimit;

    private final PlayCountRecorder playCountRecorder;

    private final CountRecorder countRecorder;

    private final ArticleDao articleDao;

    private final CommentDao commentDao;

    @Autowired
    public CountTasks(MailCountLimit mailCountLimit, PlayCountRecorder playCountRecorder,
                      CountRecorder countRecorder, ArticleDao articleDao,
                      CommentDao commentDao) {
        this.mailCountLimit = mailCountLimit;
        this.playCountRecorder = playCountRecorder;
        this.countRecorder = countRecorder;
        this.articleDao = articleDao;
        this.commentDao = commentDao;
    }

    /**
     * 6 个小时缓存一次
     * */
    @Scheduled(fixedRate = 21600000)
    @Transactional(rollbackFor = Exception.class)
    public void saveCount() {
        log.info("开始向数据库写入缓存数据：");
        if (playCountRecorder.getSize() > 0) {

            articleDao.batchUpdateCount("view_count", playCountRecorder.getPlayMap());
        }

        if (!countRecorder.getArticleCommentCount().isEmpty()) {
            articleDao.batchUpdateCount("comment_count", countRecorder.getArticleCommentCount());
        }

        if (!countRecorder.getArticleLikeCount().isEmpty()) {
            articleDao.batchUpdateCount("like_count", countRecorder.getArticleLikeCount());
        }

        if (!countRecorder.getArticleFavoriteCount().isEmpty()) {
            articleDao.batchUpdateCount("favorite_count", countRecorder.getArticleFavoriteCount());
        }

        if (!countRecorder.getArticleDislikeCount().isEmpty()) {
            articleDao.batchUpdateCount("dislike_count", countRecorder.getArticleDislikeCount());
        }

        if (!countRecorder.getDanmakuCount().isEmpty()) {
            articleDao.batchUpdateCount("danmaku_count", countRecorder.getDanmakuCount());
        }

        if (!countRecorder.getCommentCount().isEmpty()) {
            commentDao.batchUpdateCount("comment_count", countRecorder.getCommentCount());
        }

        if (!countRecorder.getCommentLikeCount().isEmpty()) {
            commentDao.batchUpdateCount("like_count", countRecorder.getCommentLikeCount());
        }

        if (!countRecorder.getCommentDislikeCount().isEmpty()) {
            commentDao.batchUpdateCount("dislike_count", countRecorder.getCommentDislikeCount());
        }
        log.info("数据同步完成！");

        playCountRecorder.clean();
        countRecorder.clear();

        log.info("清除邮件发送次数！");
        mailCountLimit.clear();
    }
}
