package com.buguagaoshu.tiktube.schedule;

import com.buguagaoshu.tiktube.cache.AdsCountRecorder;
import com.buguagaoshu.tiktube.cache.CountRecorder;
import com.buguagaoshu.tiktube.cache.PlayCountRecorder;
import com.buguagaoshu.tiktube.dao.AiConfigDao;
import com.buguagaoshu.tiktube.dao.ArticleDao;
import com.buguagaoshu.tiktube.dao.CommentDao;
import com.buguagaoshu.tiktube.repository.APICurrentLimitingRepository;
import com.buguagaoshu.tiktube.service.AIConfigServer;
import com.buguagaoshu.tiktube.service.AdvertisementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * @create 2025-04-22
 * 定期向数据库同步数据
 */
@Component
@Slf4j
public class CountTasks {
    private final PlayCountRecorder playCountRecorder;

    private final CountRecorder countRecorder;

    private final ArticleDao articleDao;

    private final CommentDao commentDao;

    private final AdsCountRecorder adsCountRecorder;

    private final AdvertisementService advertisementService;

    private final AiConfigDao aiConfigDao;

    private final AIConfigServer aiConfigServer;

    private final APICurrentLimitingRepository apiCurrentLimitingRepository;

    @Autowired
    public CountTasks(PlayCountRecorder playCountRecorder,
                      CountRecorder countRecorder, ArticleDao articleDao,
                      CommentDao commentDao,
                      AdsCountRecorder adsCountRecorder,
                      AdvertisementService advertisementService,
                      AiConfigDao aiConfigDao,
                      AIConfigServer aiConfigServer,
                      APICurrentLimitingRepository apiCurrentLimitingRepository) {
        this.playCountRecorder = playCountRecorder;
        this.countRecorder = countRecorder;
        this.articleDao = articleDao;
        this.commentDao = commentDao;
        this.adsCountRecorder = adsCountRecorder;
        this.advertisementService = advertisementService;
        this.aiConfigDao = aiConfigDao;
        this.aiConfigServer = aiConfigServer;
        this.apiCurrentLimitingRepository = apiCurrentLimitingRepository;
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
        Map<Long, Long> articleCommentCountMap = countRecorder.getArticleCommentCountMap();
        if (!articleCommentCountMap.isEmpty()) {
            articleDao.batchUpdateCount("comment_count", articleCommentCountMap);
        }
        Map<Long, Long> articleLikeCountMap = countRecorder.getArticleLikeCountMap();
        if (!articleLikeCountMap.isEmpty()) {
            articleDao.batchUpdateCount("like_count", articleLikeCountMap);
        }

        Map<Long, Long> articleFavoriteCountMap = countRecorder.getArticleFavoriteCountMap();
        if (!articleFavoriteCountMap.isEmpty()) {
            articleDao.batchUpdateCount("favorite_count", articleFavoriteCountMap);
        }
        Map<Long, Long> articleDislikeCountMap = countRecorder.getArticleDislikeCountMap();
        if (!articleDislikeCountMap.isEmpty()) {
            articleDao.batchUpdateCount("dislike_count", articleDislikeCountMap);
        }

        Map<Long, Long> danmakuCountMap = countRecorder.getDanmakuCountMap();
        if (!danmakuCountMap.isEmpty()) {
            articleDao.batchUpdateCount("danmaku_count", danmakuCountMap);
        }

        Map<Long, Long> commentCountMap = countRecorder.getCommentCountMap();
        if (!commentCountMap.isEmpty()) {
            commentDao.batchUpdateCount("comment_count", commentCountMap);
        }

        Map<Long, Long> commentLikeCountMap = countRecorder.getCommentLikeCountMap();
        if (!commentLikeCountMap.isEmpty()) {
            commentDao.batchUpdateCount("like_count", commentLikeCountMap);
        }
        Map<Long, Long> commentDislikeCountMap = countRecorder.getCommentDislikeCountMap();
        if (!commentDislikeCountMap.isEmpty()) {
            commentDao.batchUpdateCount("dislike_count", commentDislikeCountMap);
        }

        if (adsCountRecorder.getSize() > 0) {
            log.info("开始同步广告访问数据！");
            advertisementService.syncCount();
        }

        Map<Long, Long> aiToken = countRecorder.getAiModelTokenCountMap();
        if (!aiToken.isEmpty()) {
            log.info("开始同步 AI Token 数据！");
            aiConfigDao.batchUpdateCount("use_tokens", aiToken);
            // 同步缓存数据 TOKEN
            aiConfigServer.setCacheConfig();
        }


        log.info("数据同步完成！");

        playCountRecorder.clean();
        countRecorder.clear();
        apiCurrentLimitingRepository.clearAllRecords();
    }
}
